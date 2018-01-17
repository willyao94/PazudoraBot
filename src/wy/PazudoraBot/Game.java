package wy.PazudoraBot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Game {

    public static String SCREENSHOT_PATH = System.getProperty("user.dir") + "\\Files\\";
    public static String SCREENSHOT_FORMAT = "png";

    // Time(ms) for move
    public static int MOVE_DURATION = 20;
    public static int TOUCH_DURATION = 300;

    // Number of steps taking interpolating points when using drag
    public static int INTERP_STEP = 50;

    public enum SkillPosition {
        Ldr(0),
        Sub1(1),
        Sub2(2),
        Sub3(3),
        Sub4(4),
        Frd(5);

        private int value;

        SkillPosition(int value) { this.value = value; }
    }

    public enum BoardSize {
        _6x5(6),
        _5x4(5),
        _7x6(7);

        private int value;

        BoardSize(int value) { this.value = value; }
    }

    private Robot bot;
    private DateFormat dateFormat;
    private PhoneSettings settings;
    private List<Command[]> commands;

    private BoardSize boardSize;
    private int orbOffset;
    private int skillOffset;
    private Point firstSkillPos;
    private Point firstOrbPos;

    private static final int[] SWIPE_MOVE = new int[]{0,0,1,1,0,0};

    public Game (Robot bot, PhoneSettings settings) {
        this.bot = bot;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        this.settings = settings;

        // Default 6x5 board
        boardSize = BoardSize._6x5;
        orbOffset = settings.getOrbOffset(BoardSize._6x5);
        skillOffset = orbOffset;
        firstSkillPos = settings.getFirstSkillPos();
        firstOrbPos = settings.getFirstOrbPos(BoardSize._6x5);
    }

    public Game (Robot bot, PhoneSettings settings, BoardSize size) {
        this(bot, settings);
        boardSize = size;

        // Update based on board size
        orbOffset = settings.getOrbOffset(size);
        skillOffset = settings.skillOffset;
        firstOrbPos = settings.getFirstOrbPos(size);
    }

    public void executeMoveList(int[] moveList) {
        if (moveList.length == 0) return;

        startMove(moveList[0]);
        Point start = new Point(0,0), end = new Point(0,0);
        for (int i=0; i<moveList.length-1; i++) {
            start = convertToPoint(moveList[i]);
            end = convertToPoint(moveList[i+1]);
            bot.move(start.getX() * orbOffset + firstOrbPos.getX(),
                    start.getY() * orbOffset + firstOrbPos.getY(),
                    end.getX() * orbOffset + firstOrbPos.getX(),
                    end.getY() * orbOffset + firstOrbPos.getY(),
                    MOVE_DURATION);
        }

        // Duplicate last position move to make sure it moves
        bot.move(end.getX() * orbOffset + firstOrbPos.getX(),
                end.getY() * orbOffset + firstOrbPos.getY(),
                end.getX() * orbOffset + firstOrbPos.getX(),
                end.getY() * orbOffset + firstOrbPos.getY(),
                MOVE_DURATION);
        endMove(moveList[moveList.length-1]);
    }

    // Go through dungeon report screens
    // and ready up for next run
    public void goNextRun(boolean ready) {
        Point p = settings.getClearButton();
        bot.tap(p.getX(), p.getY());
        bot.pause(10000);

        bot.hold(p.getX(), p.getY());
        bot.pause(4000);
        bot.release(p.getX(), p.getY());
        bot.pause(3000);

        bot.tap(p.getX(), p.getY());
        bot.pause(1000);
        bot.tap(p.getX(), p.getY());
        bot.pause(1000);
        bot.tap(p.getX(), p.getY());
        bot.pause(7000);

        p = settings.getFriendOkButton();
        bot.tap(p.getX(),p.getY());
        bot.pause(2000);

        p = settings.getConsecBattleYes();
        bot.tap(p.getX(), p.getY());
        bot.pause(5000);

        if (ready)
            ready();
    }

    public void ready() {
        Point p = settings.getCoopReadyButton();
        bot.tap(p.getX(), p.getY());
    }

    public void setCommands(List<Command[]> commands) {
        this.commands = commands;
    }

    // Execute entire command set
    public void simulate() {
        for (Command[] cmds : commands) {
            for (int i=0; i<cmds.length; i++) {
                Command c = cmds[i];
                switch (c.getCmdName()) {
                    case ActiveSkill.CMD_NAME:
                        ActiveSkill as = (ActiveSkill) c;
                        useActive(as.getPos());
                        break;
                    case Pause.CMD_NAME:
                        Pause p = (Pause) c;
                        bot.pause(p.getDuration());
                        break;
                    case MoveSet.CMD_NAME:
                        MoveSet ms = (MoveSet) c;
                        executeMoveList(ms.getReplay());
                        break;
                }
            }
        }
    }

    // Execute a specific command
    public void simulate(int step) {
        if (this.commands == null || this.commands.size() == 0 || step >= this.commands.size()) return;
        Command[] cmds = this.commands.get(step);
        for (int i=0; i<cmds.length; i++) {
            Command c = cmds[i];
            switch (c.getCmdName()) {
                case ActiveSkill.CMD_NAME:
                    ActiveSkill as = (ActiveSkill) c;
                    useActive(as.getPos());
                    break;
                case Pause.CMD_NAME:
                    Pause p = (Pause) c;
                    bot.pause(p.getDuration());
                    break;
                case MoveSet.CMD_NAME:
                    MoveSet ms = (MoveSet) c;
                    executeMoveList(ms.getReplay());
                    break;
            }
        }
    }

    public String takeScreenshot() {
        BufferedImage ss = bot.screenshot();
        if (ss == null) return null;
        Date date = new Date();
        String dateNow = dateFormat.format(date);
        File outputFile = new File(SCREENSHOT_PATH + "Screenshot-" + dateNow + "."
                + SCREENSHOT_FORMAT);
        try {
            ImageIO.write(ss, SCREENSHOT_FORMAT, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile.getAbsolutePath();
    }

    private void pass() {
        try {
            Point p = settings.getGameMenuButton();
            bot.tap(p.getX(), p.getY());
            bot.pause(1000);

            p = settings.getPassButton();
            bot.tap(p.getX(), p.getY());
            bot.pause(1000);

            p = settings.getPassConfirmButton();
            bot.tap(p.getX(), p.getY());
            bot.pause(1000);
        } catch (Exception e) {
            System.out.println("Failed to perform pass. Please check if the settings file " +
                    "is missing the coordinates for passing.");
        }
    }

    private void useActive(SkillPosition skill) {
        int x = firstSkillPos.getX() + skillOffset * skill.value;
        int y = firstSkillPos.getY();
        bot.tap(x,y);
    }

    private void startMove(int n) {
        Point p = convertToPoint(n);
        bot.hold(p.getX() * orbOffset + firstOrbPos.getX(),
                p.getY() * orbOffset + firstOrbPos.getY());
    }

    private void endMove(int n) {
        Point p = convertToPoint(n);
        bot.release(p.getX() * orbOffset + firstOrbPos.getX(),
                p.getY() * orbOffset + firstOrbPos.getY());
    }

    private Point convertToPoint(int n) {
        int x = (int) Math.floor(n % boardSize.value);
        int y = (int) Math.floor(n / boardSize.value);

        return new Point(x, y);
    }
}
