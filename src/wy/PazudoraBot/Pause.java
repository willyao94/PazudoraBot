package wy.PazudoraBot;

public class Pause extends Command {

    public final static String CMD_NAME = "Pause";

    // In ms
    private int duration;

    public Pause(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getCmdName() {
        return CMD_NAME;
    }
}
