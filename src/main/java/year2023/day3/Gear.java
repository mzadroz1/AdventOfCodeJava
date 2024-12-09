package year2023.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Gear {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day3/input.txt"));

        char[][] engine = new char[lines.size()][];

        for (int i = 0; i < lines.size(); i++) {
            engine[i] = new char[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                engine[i][j] = lines.get(i).charAt(j);
            }
        }

        int sumOfGearRatios = 0;
        List<EnginePart> numbers = new ArrayList<>();

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
                    numbers.add(new EnginePart(number.toString(), i, numberStart, numberEnd));
                }
            }
        }

//        System.out.println(numbers);

        for (int i = 0; i < engine.length; i++) {
            for (int j = 0; j < engine[i].length; j++) {
                if (engine[i][j] == '*') {
                    Set<EnginePart> neighbourParts = new HashSet<>();
                        for (EnginePart enginePart : numbers) {
                            if ((enginePart.numberRow == (i - 1)
                            || enginePart.numberRow == i
                            || enginePart.numberRow == (i+1))
                                    && enginePart.numberStartIndex <= (j + 1)
                                    && enginePart.numberEndIndex >= (j - 1)
                            ) {
                                neighbourParts.add(enginePart);
                            }
                        }

                    if (neighbourParts.size() == 2) {
                        Integer gearRatio = 1;
                        for(EnginePart enginePart : neighbourParts) {
                            gearRatio *= Integer.parseInt(enginePart.number);
                        }
                        sumOfGearRatios += gearRatio;
                    }
                }
            }
        }
        System.out.println(sumOfGearRatios);
    }


    static class EnginePart {
        String number;

        int numberRow;
        int numberStartIndex;
        int numberEndIndex;

        public EnginePart(String number, int numberRow, int numberStartIndex, int numberEndIndex) {
            this.number = number;
            this.numberRow = numberRow;
            this.numberStartIndex = numberStartIndex;
            this.numberEndIndex = numberEndIndex;
        }

        @Override
        public String toString() {
            return "EnginePart{" +
                    "number='" + number + '\'' +
                    ", numberRow=" + numberRow +
                    ", numberStartIndex=" + numberStartIndex +
                    ", numberEndIndex=" + numberEndIndex +
                    '}';
        }
    }
}
