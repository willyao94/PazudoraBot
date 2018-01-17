package wy.PazudoraBot;

import wy.PazudoraBot.Game.BoardSize;

/**
 * Settings for phone
 * TODO: add 5x4 values
 */
public class PhoneSettings {

    // Skills
    protected Point firstSkillPos;
    protected int skillOffset;

    // 6x5 board
    protected Point firstOrbPos;
    protected int orbOffset;

    // 7x6 board
    protected Point firstOrbPos_7x6;
    protected int orbOffset_7x6;

    // Passing
    protected Point gameMenuButton;
    protected Point passButton;
    protected Point passConfirmButton;

    // After clearing dungeon
    protected Point clearButton;
    protected Point friendOkButton;
    protected Point consecBattleYes;
    protected Point consecBattleNo;

    // Ready to start
    protected Point coopReadyButton;

    public PhoneSettings() {}

    public PhoneSettings(Point firstSkillPos, int skillOffset, Point firstOrbPos, int orbOffset) {
        // For a normal 6x5 board
        this.firstSkillPos = firstSkillPos;
        this.skillOffset = skillOffset;

        this.firstOrbPos = firstOrbPos;
        this.orbOffset = orbOffset;
    }

    public void setupPassing(Point gameMenuPos, Point passButtonPos, Point passConfirmButton) {
        this.gameMenuButton = gameMenuPos;
        this.passButton = passButtonPos;
        this.passConfirmButton = passConfirmButton;
    }

    public void setupConsecRuns(Point clearButton, Point friendOkButton, Point consecBattleYes,
                                Point consecBattleNo, Point coopReadyButton) {
        this.clearButton = clearButton;
        this.friendOkButton = friendOkButton;
        this.consecBattleYes = consecBattleYes;
        this.consecBattleNo = consecBattleNo;

        this.coopReadyButton = coopReadyButton;
    }

    public void setup7x6Board(Point firstOrbPos, int orbOffset) {
        this.firstOrbPos_7x6 = firstOrbPos;
        this.orbOffset_7x6 = orbOffset;
    }

    public int getOrbOffset(BoardSize size) {
        int offset;
        switch (size) {
            case _6x5:
                offset = orbOffset;
                break;
            case _7x6:
                offset  = orbOffset_7x6;
                break;
            default:
                offset = orbOffset;
        }
        return offset;
    }

    public int getSkillOffset() {
        return skillOffset;
    }

    public Point getFirstSkillPos() {
        return firstSkillPos;
    }

    public Point getFirstOrbPos(BoardSize size) {
        Point p;
        switch (size) {
            case _6x5:
                p = firstOrbPos;
                break;
            case _7x6:
                p  = firstOrbPos_7x6;
                break;
            default:
                p = firstOrbPos;
        }
        return p;
    }

    public Point getGameMenuButton() {
        return gameMenuButton;
    }

    public Point getPassButton() {
        return passButton;
    }

    public Point getPassConfirmButton() {
        return passConfirmButton;
    }

    public Point getClearButton() {
        return clearButton;
    }

    public Point getFriendOkButton() {
        return friendOkButton;
    }

    public Point getConsecBattleYes() {
        return consecBattleYes;
    }

    public Point getConsecBattleNo() {
        return consecBattleNo;
    }

    public Point getCoopReadyButton() {
        return coopReadyButton;
    }
}
