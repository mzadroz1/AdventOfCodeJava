package year2023.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CubesGame {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day2/input.txt"));

        int sumOfIds = 0;
        for(String line : lines) {
//            System.out.println(line);
            boolean gamePossible = true;
            int id = Integer.parseInt(line.split(": ")[0].split(" ")[1]);
            String[] draws = line.split(": ")[1].split("; ");
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
                if(red > 12 || green > 13 || blue > 14) {
                    gamePossible = false;
                }
//                System.out.println("red=" + red + " green=" + green + " blue=" + blue);
            }
            if(gamePossible) {
                sumOfIds += id;
            }
        }
        System.out.println(sumOfIds);
    }
}
