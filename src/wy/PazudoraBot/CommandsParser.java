package wy.PazudoraBot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parse JSON file for commands to input to robot
 */
public class CommandsParser {

    private static final String CMD_COUNT_KEY = "CmdCount";
    private JSONParser parser;

    public CommandsParser() {
        parser = new JSONParser();
    }

    public List<Command[]> parse(File file) {
        List<Command[]> cmds = new ArrayList<>();
        try {
            // Parse
            Object obj = parser.parse(new FileReader(file.getAbsoluteFile()));

            // Cast as JSONObject
            JSONObject jsonObj = (JSONObject) obj;
            // Parse for number of commands
            long cmdCount = (Long) jsonObj.get(CMD_COUNT_KEY);
            // Parse rest of file for commands
            for (int i=1; i<=cmdCount; i++) {
                // Using array because it is ordered
                JSONArray jsonCmdsArr = (JSONArray) jsonObj.get(String.valueOf(i));
                Command[] cmdsArr = new Command[jsonCmdsArr.size()];
                Iterator<JSONObject> it = jsonCmdsArr.iterator();
                for (int j=0; it.hasNext(); j++) {
                    JSONObject jsonCmd = it.next();
                    // Should only have 1 key since order is not preserved
                    String firstKey = (String) jsonCmd.keySet().iterator().next();
                    Command c = commandFactory(firstKey, jsonCmd.get(firstKey).toString());
                    cmdsArr[j] = c;
                }
                cmds.add(cmdsArr);
            }
            return cmds;
        } catch (FileNotFoundException e) {
            System.out.println("Failed to find file: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to parse commands. Please double check your JSON file.");
            e.printStackTrace();
        }
        return null;
    }

    private Command commandFactory(String cmdName, String value) {
        try {
            Command cmd;
            switch (cmdName) {
                case ActiveSkill.CMD_NAME:
                    cmd = new ActiveSkill(Game.SkillPosition.valueOf(value));
                    break;
                case Pause.CMD_NAME:
                    cmd = new Pause(Integer.parseInt(value));
                    break;
                case MoveSet.CMD_NAME:
                    cmd = new MoveSet(value.split("\\."));
                    break;
                default:
                    cmd = null;
            }
            return cmd;
        } catch (Exception e) {
            System.out.println("Failed to parse command: \"" + cmdName + "\":\"" + value + "\"");
            e.printStackTrace();
            return null;
        }
    }
}