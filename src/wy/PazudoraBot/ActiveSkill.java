package wy.PazudoraBot;

public class ActiveSkill extends Command {

    public final static String CMD_NAME = "ActiveSkill";

    private Game.SkillPosition pos;

    public ActiveSkill(Game.SkillPosition pos) {
        this.pos = pos;
    }

    public Game.SkillPosition getPos() {
        return pos;
    }

    @Override
    public String getCmdName() {
        return CMD_NAME;
    }
}
