package wy.PazudoraBot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SettingsParser {

    private JSONParser parser;

    // Keys to parse for
    private static final String FIRST_SKILL_POS = "FirstSkillPos";
    private static final String START_ORB_POS = "StartOrbPos";
    private static final String ORB_OFFSET = "OrbOffset";

    private static final String GAME_MENU_BUTTON = "GameMenuButton" ;
    private static final String PASS_BUTTON = "PassButton" ;
    private static final String PASS_CONFIRM_BUTTON = "PassConfirmButton" ;

    private static final String CLEAR_BTN = "ClearButton";
    private static final String FRD_OK_BTN = "FriendOkButton";
    private static final String CONSEC_BATTLE_YES_BUTTON = "ConsecBattleYesButton";
    private static final String CONSEC_BATTLE_NO_BUTTON = "ConsecBattleNoButton";
    private static final String COOP_READY_BUTTON = "CoopReadyButton";

    private static final String START_ORB_POS_7x6 = "StartOrbPos7x6";
    private static final String ORB_OFFSET_7x6 = "OrbOffset7x6";

    public SettingsParser() {
        parser = new JSONParser();
    }

    public PhoneSettings parse(String path){
        File parseFile = new File(path);

        // If doesn't exist or not a file, exit
        if (!parseFile.exists()) {
            System.out.println(path + " does not exist.");
            return null;
        } else if (!parseFile.isFile()) {
            System.out.println(path + " is not a file.");
            return null;
        }

        PhoneSettings settings;
        JSONObject jsonObj;
        JSONArray jsonArr;
        // Parse required settings
        try {
            Object obj = parser.parse(new FileReader(parseFile.getAbsoluteFile()));
            // Cast as JSONObject
            jsonObj= (JSONObject) obj;
            // Look up keys
            // First skill pos
            jsonArr = (JSONArray) jsonObj.get(FIRST_SKILL_POS);
            Point firstSkillPos = parsePoint((JSONObject) jsonArr.get(0));

            // Start orb pos
            jsonArr = (JSONArray) jsonObj.get(START_ORB_POS);
            Point startOrbPos = parsePoint((JSONObject) jsonArr.get(0));

            // Skill & orb offset
            int offset = Integer.parseInt((String) jsonObj.get(ORB_OFFSET));

            settings = new PhoneSettings(firstSkillPos, offset, startOrbPos, offset);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to find settings file.");
            return null;
        } catch (Exception e) {
            System.out.println("Failed to parse settings file. Please confirm the settings file follow " +
                    "the guidelines.");
            return null;
        }

        // Parse settings for Coop
        try {
            // Passing
            // Game menu pos
            jsonArr = (JSONArray) jsonObj.get(GAME_MENU_BUTTON);
            Point gameMenuPos = parsePoint((JSONObject) jsonArr.get(0));

            // Game menu pos
            jsonArr = (JSONArray) jsonObj.get(PASS_BUTTON);
            Point passBtnPos = parsePoint((JSONObject) jsonArr.get(0));

            // Game menu pos
            jsonArr = (JSONArray) jsonObj.get(PASS_CONFIRM_BUTTON);
            Point passConfrimPos = parsePoint((JSONObject) jsonArr.get(0));
            settings.setupPassing(gameMenuPos, passBtnPos, passConfrimPos);

            // Consecutive runs
            // Clear button pos
            jsonArr = (JSONArray) jsonObj.get(CLEAR_BTN);
            Point clearBtnPos = parsePoint((JSONObject) jsonArr.get(0));

            // Friend Ok button pos
            jsonArr = (JSONArray) jsonObj.get(FRD_OK_BTN);
            Point frdOkBtnPos = parsePoint((JSONObject) jsonArr.get(0));

            // Consec battle yes button pos
            jsonArr = (JSONArray) jsonObj.get(CONSEC_BATTLE_YES_BUTTON);
            Point consecBattleYesPos = parsePoint((JSONObject) jsonArr.get(0));

            // Consec battle no button pos
            jsonArr = (JSONArray) jsonObj.get(CONSEC_BATTLE_NO_BUTTON);
            Point consecBattleNoPos = parsePoint((JSONObject) jsonArr.get(0));

            // Coop ready button pos
            jsonArr = (JSONArray) jsonObj.get(COOP_READY_BUTTON);
            Point coopReadyBtnPos = parsePoint((JSONObject) jsonArr.get(0));
            settings.setupConsecRuns(clearBtnPos, frdOkBtnPos, consecBattleYesPos, consecBattleNoPos,
                    coopReadyBtnPos);

        } catch (Exception e) {
            System.out.println("Settings file did not contain additional information used for Coop. " +
                    "Continuing..");
        }

        // Parse settings for 7x6
        try {
            // Start orb pos
            jsonArr = (JSONArray) jsonObj.get(START_ORB_POS_7x6);
            Point startOrbPos7x6 = parsePoint((JSONObject) jsonArr.get(0));

            // Orb offset
            int orbOffset7x6 = Integer.parseInt((String) jsonObj.get(ORB_OFFSET_7x6));
            settings.setup7x6Board(startOrbPos7x6, orbOffset7x6);
            return settings;
        } catch (Exception e) {
            System.out.println("Settings file did not contain additional information used for 7x6 boards.");
            return settings;
        }
    }

    private Point parsePoint(JSONObject obj) {
        int x = Integer.parseInt((String) obj.get("X"));
        int y = Integer.parseInt((String) obj.get("Y"));
        return new Point(x, y);
    }
}
