# README
 
### **Disclaimer**: Use at your own risk. Responsibility will not be taken for anything that happens to your account
 
PazudoraBot is a CLI tool that uses [Android's MonkeyRunner](https://developer.android.com/studio/test/monkeyrunner/index.html) to automate executing a solve for a puzzle or macro for an entire dungeon. Additionally, the macro functionality supports 2 devices on the same network running 2P coop mode. As this uses Android's MonkeyRunner, it is **only** compatible with Android devices.
 
#### Getting Started
 
To run or compile the project, first import [json-simple-1.1.1.jar](https://code.google.com/archive/p/json-simple/downloads).
 
Then from [Android SDK](https://developer.android.com/studio/index.html), import the latest versions of:
```
chimpchat-x.x.x-dev.jar
common-x.x.x-dev.jar
ddmlik-x.x.x-dev.jar
guava-x.x.jar
jython-standalone-x.x.x.jar
monkeyrunner--dev.jar
sdklib.x.x.x.-dev.jar
```
 
After getting the project running, a Json settings file must be updated with information for the tool to run correctly. This file should be placed in a folder called Files. Check [Examples](/Examples) for some templates.
The only required parameters in the settings file are:

| Key   | Description
| ----- | ---------------
| FirstSkillPos | The X,Y coordinate of the lead card when running a dungeon.
| StartOrbPos   | The X,Y coordinate of the first orb on a 6x5 board.
| OrbOffset     | The delta distance between 2 orbs on a 6x5 board. This is also used to calculate the skill position of the cards following the lead card.

Take a screenshot when in a dungeon and follow this image to obtain the values:

![](https://i.imgur.com/aWxffwg.png)

Use whichever tool that can show the position of pixels in your screenshot (e.g. Paint).
Measuring more accurately will produce better results using this tool.

Repeating the instructions above for a 7x6 board, the following group of parameters can be included to support it:

| Key   | Description
| ----- | ---------------
| StartOrbPos7x6    | The X,Y coordinate of the first orb on a 7x6 board.
| OrbOffset7x6      | The delta distance between 2 orbs on a 7x6 board.

For 2P coop mode support, include the following group of parameters:

| Key   | Description   | Image
| ----- | ---------------   | --------
| GameMenuButton		    | The X,Y coordinate of the menu button while in a dungeon. | [https://i.imgur.com/niSW5rm.png](https://i.imgur.com/niSW5rm.png)
| PassButton				| The X,Y coordinate of the pass button in the dungeon menu.
| PassConfrmButton  		| The X,Y coordinate of the pass confirmation button.
| ClearButton				| The X,Y coordinate of the OK button when a dungeon is cleared.    | [https://i.imgur.com/cdDqKCm.png](https://i.imgur.com/cdDqKCm.png)
| FriendOkButton			| The X,Y coordinate of the OK button of the friend used in the rewards interface.  | [https://i.imgur.com/hPCrWzK.png](https://i.imgur.com/hPCrWzK.png)
| ConsecBattleYesButton	    | The X,Y coordinate of the Yes button when prompted to run a dungeon again with the same partner.    | [https://i.imgur.com/TdZLHWL.png](https://i.imgur.com/TdZLHWL.png)
| ConsecBattleNoButton	    | The X,Y coordinate of the No button when prompted to run a dungeon again with te same partner.    | Same as above
| CoopReadyButton			| The X,Y coordinate of the Start/Ready button in a 2P coop mode lobby. | [https://i.imgur.com/xHkhyf6.png](https://i.imgur.com/xHkhyf6.png)


User Guide
------
 
#### Commands:
| Command   | Shortcut  | Description          
| --------- | --------- | -------------------- 
| help      | h         | Prints the help.
| quit      | q         | Terminates the tool.
| link      | l         | Execute moves from a replay link input.
| macro     | m         | Execute a macro from a given macro name. Macro file must exist in Files folder.
| host      | h         | Starts the host connection. Refer to Macros for 2P Coop.
| client    | c         | Start the client connection. Refer to Macros for 2P Coop.
 
 
 
#### Macros:
A macro is a Json file that the tool will parse to follow numerically ordered instructions starting at 1. A CmdCount key must with the value being the number of instructions for the macro must also be included. All key value pairs should be strings i.e. wrapped with "".
An instruction is a Json array of commands the tool can do. These include:

| Key   | Example Value | Description   
| ----- | ------------  | ------------  
| ActiveSkill   | Sub3  | An enum denoting a card's skill. Takes 1 of the following values: Ldr, Sub1, Sub2, Sub3, Sub4, or Frd.
| MoveSet       | 25.19.18.12.13.19.20.21.27.28.29.23.22.16.10.4.3.2.8  | A string of numbers delimited by '.' representing the movement of a path. Similar to a replay file from [here](https://candyninja001.github.io/Puzzled/?patt=BBDDBDDDBBDBDBBDBDBBDDBDBDBBDB&replay=25.19.18.12.13.19.20.21.27.28.29.23.22.16.10.4.3.2.8)
| Pause | 6500      | Time (ms) to wait. Typically used to wait for any animations to finish. You will have to experiment around to find a value that works for your use case.
 
#### Macros for 2P Coop
As mentioned above, this tool supports running macros over 2 devices for 2P coop. The 2 devices must be on the same LAN for this functionality to work.
 
* To begin, have both players ready up and enter the dungeon in game.
* The host, player 1 of the 2P coop, will now execute the host command and enter the name of the macro without the file extension (i.e. ".json") located in the Files folder to run.
* Next, enter the number of times the macro will repeatedly run and wait for a client to connect.
* Player 2 will now execute the client command and enter the LAN IP address of the host (e.g. 192.168.0.10).
* The computers will try to establish a connection over port 9090 and the macro will start.