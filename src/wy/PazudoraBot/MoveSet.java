package wy.PazudoraBot;

public class MoveSet extends Command{

    public final static String CMD_NAME = "MoveSet";

    private int[] replay;

    public MoveSet(String[] replay) {
        this.replay = new int[replay.length];
        for (int i=0; i<replay.length; i++) {
            this.replay[i] = Integer.parseInt(replay[i]);
        }
    }

    public int[] getReplay() {
        return replay;
    }

    @Override
    public String getCmdName() {
        return CMD_NAME;
    }
}
