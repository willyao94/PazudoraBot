package wy.PazudoraBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Game game;
    private static final String FILES_PATH = System.getProperty("user.dir") + "\\Files\\";
    private static final String JSON_FORMAT_STRING = ".json";
    private static final int PORT_NUM = 9090;

    public static void main(String[] args) {
        // Initialize variables
        Robot bot = new Robot();
        String input;
        Scanner scanner = new Scanner(System.in);
        Socket socket;
        PhoneSettings settings;

        try {
            settings = new SettingsParser().parse(FILES_PATH + "settings" + JSON_FORMAT_STRING);
            game = new Game(bot, settings);
            System.out.println("Loaded settings.");
        } catch (Exception e) {
            System.out.println("Failed to find a settings file to parse.");
        }

        while (scanner.hasNext()) {
            // Get user inputted file to parse
            input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("q") || input.equals("exit"))
                break;

            // Help
            if (input.equals("h")) {
                System.out.println("Commands:\n"
                        + "\tl - Execute moves from a given Dawnglare replay link\n"
                        + "\tm - Execute moves from a JSON file\n"
                        + "\th - Start hosting player.\n"
                        + "\tc - Start client player.\n");
            }

            switch (input) {
                case "l":
                    System.out.println("Please enter link.");
                    input = scanner.nextLine().trim().toLowerCase();
                    cmdExecuteMoves(input);
                    break;
                case "m": { // Have braces around this case to create nested scope
                    System.out.println("Please enter the file name.");
                    input = scanner.nextLine().trim();
                    List<Command[]> commands = parseFileCommand(FILES_PATH + input + JSON_FORMAT_STRING);
                    if (commands != null) {
                        game.setCommands(commands);
                        game.simulate();
                        System.out.println("Complete");
                    }
                    break;
                }
                case "h": {     // Host
                    // Get file commands
                    System.out.println("Please enter the file name.");
                    String fileName = scanner.nextLine().trim();
                    List<Command[]> commands = parseFileCommand(FILES_PATH + fileName +
                            JSON_FORMAT_STRING);
                    if (commands == null) return;

                    // Get # of runs
                    System.out.println("Please enter number of runs.");
                    while (true) {
                        input = scanner.nextLine().trim().toLowerCase();

                        if (!input.matches("[1-9][0-9]*")) {
                            System.out.println("Please enter a number > 0.");
                            continue;
                        } else
                            break;
                    }

                    int runs = Integer.parseInt(input);
                    int counter = 0;

                    try {
                        ServerSocket listener = new ServerSocket(PORT_NUM);
                        System.out.println("Waiting for connection..");
                        socket = listener.accept();
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // Send the file name that will be used
                        out.println(fileName);
                        boolean ack = Boolean.parseBoolean(in.readLine());
                        if (!ack) {
                            System.out.println("Client does not have file: " + fileName + ". Aborting.");
                            continue;
                        }

                        // Execute starting step
                        int step = 0;
                        game.setCommands(commands);
                        game.simulate(step);
                        // Increment step
                        step++;
                        // Send step
                        out.println(step);

                        while (true) {
                            String response = in.readLine().toLowerCase().trim();
                            System.out.println("Received response: " + response);

                            if (response.equals("q")) {
                                counter++;
                                System.out.println("Finished run # " + counter);
                                // Quit when finished all runs
                                if (counter >= runs) {
                                    out.println("q");
                                    break;
                                } else {
                                    // Wait for finish
                                    bot.pause(10000);
                                    out.println("r");
                                    game.goNextRun(false);
                                    // Give client extra time so it can
                                    // be ready first
                                    bot.pause(5000);
                                    continue;
                                }
                            } else if (response.equals("r")) {
                                // Reset the step (hacky?)
                                response = "0";
                                game.ready();
                                // Wait till entered into dungeon
                                bot.pause(18000);
                            }

                            step = Integer.parseInt(response);
                            // execute next step
                            game.simulate(step);
                            step++;

                            // Send instruction to goto next step
                            out.println(step);
                        }

                        listener.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "c": {     // Client
                    // Get host IP
                    System.out.println("Please the host's IP address.");
                    while (true) {
                        input = scanner.nextLine().trim().toLowerCase();

                        if (!input.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                            System.out.println(input + " is not a valid IP address. Please try again.");
                            continue;
                        } else
                            break;
                    }

                    try {
                        socket = new Socket(input, PORT_NUM);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        int step;
                        // Get file name from host
                        String fileName= in.readLine().trim();
                        List<Command[]> commands = parseFileCommand(FILES_PATH + fileName +
                                JSON_FORMAT_STRING);
                        // Send ack back to confirm parsed correctly
                        if (commands == null) {
                            out.println(false);
                            return;
                        } else
                            out.println(true);

                        game.setCommands(commands);
                        int cmdsCount = commands.size();
                        while (true) {
                            String response = in.readLine().trim().toLowerCase();
                            System.out.println("Received response: " + response);

                            if (response.equals("q")) {
                                break;
                            } else if (response.equals("r")) {
                                game.goNextRun(true);
                                // Tell host you're ready
                                out.println("r");
                                continue;
                            }

                            step = Integer.parseInt(response);
                            game.simulate(step);
                            step++;

                            if (step >= cmdsCount) {
                                out.println("q");
                            } else
                                out.println(step);
                        }

                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    System.out.println("Unrecognized command. Please input h for help.");
                    continue;
            }
        }
        bot.dispose();
        System.exit(0);
    }

    /**
     * Parses the replay from a Dawnglare link
     * and executing that to the device
     * @param link - A URL with the format
     *             https://candyninja001.github.io/Puzzled/
     *             ?patt=[board]&replay=[moves]
     *             where moves are integers between [0,29] & delimited by .
     */
    private static void cmdExecuteMoves(String link) {
        // Append duplicate last move in case movement cuts short
        int index = link.lastIndexOf(".");
        link = link + link.substring(index);
        index = link.lastIndexOf("=");
        String replay = link.substring(index + 1);
        String[] stringArr = replay.split("\\.");
        try {
            // https://stackoverflow.com/a/6886537
            int[] moveList = Arrays.stream(stringArr).mapToInt(Integer::parseInt).toArray();
            game.executeMoveList(moveList);
        } catch (Exception e) {
            System.out.println("Failed to parse replay from link.");
        }
    }

    private static List<Command[]> parseFileCommand(String path) {
        File parseFile = new File(path);

        // if doesn't exist or not a file, exit
        if (!parseFile.exists()) {
            System.out.println(path + " does not exist.");
            return null;
        } else if (!parseFile.isFile()) {
            System.out.println(path + " is not a file.");
            return null;
        }

        CommandsParser parser = new CommandsParser();
        return parser.parse(parseFile);
    }
}
