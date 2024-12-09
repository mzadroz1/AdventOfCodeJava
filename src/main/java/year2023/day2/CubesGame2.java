package year2023.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CubesGame2 {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day2/input.txt"));

        int sumOfPowers = 0;
        for(String line : lines) {
            System.out.println(line);
            int id = Integer.parseInt(line.split(": ")[0].split(" ")[1]);
            String[] draws = line.split(": ")[1].split("; ");
            int maxGreen = 0;
            int maxRed = 0;
            int maxBlue = 0;
            for(String draw : draws) {
                String[] cubes = draw.split(", ");
                int green = 0;
                int red = 0;
                int blue = 0;

                for(String cube : cubes) {
                    String[] x = cube.split(" ");
                    if(x[1].equals("green")) {
                        green = Integer.parseInt(x[0]);
                    }
                    if(x[1].equals("red")) {
                        red = Integer.parseInt(x[0]);
                    }
                    if(x[1].equals("blue")) {
                        blue = Integer.parseInt(x[0]);
                    }
                }
                if(red > maxRed) {
                    maxRed = red;
                }
                if(blue > maxBlue) {
                    maxBlue = blue;
                }
                if(green > maxGreen) {
                    maxGreen = green;
                }
            }
            System.out.println("red=" + maxRed + " green=" + maxGreen + " blue=" + maxBlue);
            int power = maxBlue * maxGreen * maxRed;
            sumOfPowers += power;
        }
        System.out.println(sumOfPowers);
    }
}
