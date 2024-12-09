package year2023.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EngineParts {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day3/input.txt"));

        char[][] engine = new char[lines.size()][];

        for (int i = 0; i < lines.size(); i++) {
            engine[i] = new char[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                engine[i][j] = lines.get(i).charAt(j);
            }
        }

        int sumOfPartsNumbers = 0;

        int numberStart = 0;
        int numberEnd = 0;
        for (int i = 0; i < engine.length; i++) {
            for (int j = 0; j < engine[i].length; j++) {
                if (Character.isDigit((engine[i][j]))) {
                    StringBuilder number = new StringBuilder(engine[i][j]);
                    numberStart = j;
                    while (j < engine[i].length && Character.isDigit((engine[i][j]))) {
                        number.append(engine[i][j]);
                        numberEnd = j;
                        j++;
                    }
//                    System.out.println(number);
//                    System.out.println(numberStart + " " + engine[i][numberStart] + " " + numberEnd + " " + engine[i][numberEnd]);
                    boolean specialCharFound = false;
                    //row above
                    if((i-1) > 0) {
                        for(int x = numberStart - 1; x <= numberEnd + 1; x++) {
                            if(x > 0 && x < engine[i].length) {
                                String neighbour = String.valueOf(engine[i - 1][x]);
                                if(!(neighbour.equals(".") || Character.isDigit(engine[i - 1][x]))) {
                                    specialCharFound = true;
                                }
                            }
                        }
                    }
                    //current row
                    for(int x = numberStart - 1; x <= numberEnd + 1; x++) {
                        if(x > 0 && x < engine[i].length) {
                            String neighbour = String.valueOf(engine[i][x]);
                            if(!(neighbour.equals(".") || Character.isDigit(engine[i][x]))) {
                                specialCharFound = true;
                            }
                        }
                    }
                    //row below
                    if((i+1) < engine.length) {
                        for(int x = numberStart - 1; x <= numberEnd + 1; x++) {
                            if(x > 0 && x < engine[i].length) {
                                String neighbour = String.valueOf(engine[i + 1][x]);
                                if(!(neighbour.equals(".") || Character.isDigit(engine[i + 1][x]))) {
                                    specialCharFound = true;
                                }
                            }
                        }
                    }
                    if(specialCharFound) {
                        sumOfPartsNumbers += Integer.parseInt(number.toString());
                    }
//                    System.out.println(number + " " + specialCharFound);
                }
            }
        }

        System.out.println(sumOfPartsNumbers);
    }

}
