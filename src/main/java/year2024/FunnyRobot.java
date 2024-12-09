package year2024;

import java.awt.*;

public class FunnyRobot {

    public static void main(String[] args) throws InterruptedException, AWTException {
        int seconds = 100;
        Robot robot = new Robot();
        Point location = MouseInfo.getPointerInfo().getLocation();
        while (true) {
            robot.mouseMove(location.x + 100, location.y + 100);
            Thread.sleep(100);
            robot.mouseMove(location.x, location.y);
            Thread.sleep(seconds * 1000);
        }
    }
}
