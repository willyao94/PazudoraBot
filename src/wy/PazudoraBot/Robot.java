package wy.PazudoraBot;

import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import com.android.chimpchat.adb.AdbBackend;

import java.awt.image.BufferedImage;

/**
 * Using MonkeyRunner API to execute inputs
 */

public class Robot {

    private IChimpDevice device;

    public Robot() {
        try {
            device = new AdbBackend().waitForConnection();
            System.out.println("Device connected: " + device.getProperty("build.model"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void wake() {
        if (device != null)
            device.wake();
    }

    public void dispose() {
        if (device != null)
            device.dispose();
    }

    public void drag(int x0, int y0, int x1, int y1, int steps, int duration) {
        if (device != null) {
            device.drag(x0, y0, x1, y1, steps, duration);
        }
    }

    public void hold(int x, int y) {
        if (device != null) {
            device.touch(x, y, TouchPressType.DOWN);
        }
    }

    public void move(int startx, int starty, int endx, int endy, int duration) {
        if (device != null) {
            device.touch(startx, starty, TouchPressType.MOVE);
            pause(duration);
            device.touch(endx, endy, TouchPressType.MOVE);
            pause(duration);
        }
    }

    public void release(int x, int y) {
        if (device != null) {
            device.touch(x, y, TouchPressType.UP);
        }
    }

    public void tap(int x, int y) {
        if (device != null) {
            device.touch(x, y, TouchPressType.DOWN);
            pause(200); // TouchPresType.DOWN_AND_UP doesn't seem to work
            device.touch(x, y, TouchPressType.UP);
        }
    }

    public BufferedImage screenshot() {
        if (device != null) {
            return device.takeSnapshot().createBufferedImage();
        }
        return null;
    }


    public void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }
}

