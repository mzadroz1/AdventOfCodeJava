package year2023.day21;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GardenNavigation {

    private static final int STEPS_NUMBER = 64;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day21/input.txt"));

        Garden garden = new Garden(lines);

        System.out.println(garden);


        for (int step = 0; step < STEPS_NUMBER; step++) {
            List<GardenTile> currentPossiblePositions = new ArrayList<>();

            for (GardenTile currentPosition : garden.currentPossiblePositions) {
                int i = currentPosition.i;
                int j = currentPosition.j;

                //NORTH
                if (i - 1 >= 0 && garden.gardenGrid[i - 1][j].symbol == GardenSymbol.PLOT) {
                    GardenTile newPossiblePosition = new GardenTile(GardenSymbol.POSSIBLE_STEP, i - 1, j);
                    garden.gardenGrid[i-1][j] = newPossiblePosition;
                    currentPossiblePositions.add(newPossiblePosition);
                }
                //SOUTH
                if (i + 1 < garden.gardenGrid.length && garden.gardenGrid[i + 1][j].symbol == GardenSymbol.PLOT) {
                    GardenTile newPossiblePosition = new GardenTile(GardenSymbol.POSSIBLE_STEP, i + 1, j);
                    garden.gardenGrid[i+1][j] = newPossiblePosition;
                    currentPossiblePositions.add(newPossiblePosition);
                }
                //WEST
                if (j - 1 >= 0 && garden.gardenGrid[i][j - 1].symbol == GardenSymbol.PLOT) {
                    GardenTile newPossiblePosition = new GardenTile(GardenSymbol.POSSIBLE_STEP, i, j - 1);
                    garden.gardenGrid[i][j - 1] = newPossiblePosition;
                    currentPossiblePositions.add(newPossiblePosition);
                }
                //EAST
                if (j + 1 < garden.gardenGrid[0].length && garden.gardenGrid[i][j + 1].symbol == GardenSymbol.PLOT) {
                    GardenTile newPossiblePosition = new GardenTile(GardenSymbol.POSSIBLE_STEP, i, j + 1);
                    garden.gardenGrid[i][j + 1] = newPossiblePosition;
                    currentPossiblePositions.add(newPossiblePosition);
                }
                garden.gardenGrid[i][j] = new GardenTile(GardenSymbol.PLOT, i, j);
            }
            garden.currentPossiblePositions = currentPossiblePositions;
            System.out.println(garden);
        }
        System.out.println(garden.possiblePlotsReachCount());
    }

    @Setter
    @AllArgsConstructor
    static class Garden {
        GardenTile[][] gardenGrid;
        List<GardenTile> currentPossiblePositions = new ArrayList<>();

        public Garden(List<String> lines) {
            gardenGrid = new GardenTile[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                gardenGrid[i] = new GardenTile[lines.get(i).length()];
                for (int j = 0; j < lines.get(i).length(); j++) {
                    gardenGrid[i][j] = new GardenTile(GardenSymbol.parse(lines.get(i).charAt(j)), i, j);
                    if (gardenGrid[i][j].symbol == GardenSymbol.START) {
                        currentPossiblePositions.add(gardenGrid[i][j]);
                    }
                }
            }
        }

        public int possiblePlotsReachCount() {
            int result = 0;
            for (int i = 0; i < gardenGrid.length; i++) {
                for (int j = 0; j < gardenGrid[i].length; j++) {
                    if(gardenGrid[i][j].symbol == GardenSymbol.POSSIBLE_STEP) {
                        result++;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < gardenGrid.length; i++) {
                for (int j = 0; j < gardenGrid[i].length; j++) {
                    sb.append(gardenGrid[i][j].symbol.symbol);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    @Getter
    @AllArgsConstructor
    static class GardenTile {

        GardenSymbol symbol;
        int i;
        int j;

    }

    @AllArgsConstructor
    enum GardenSymbol {
        START('S'),
        PLOT('.'),
        ROCK('#'),
        POSSIBLE_STEP('O');

        final char symbol;

        static GardenSymbol parse(char s) {
            return EnumSet.allOf(GardenSymbol.class).stream()
                    .filter(p -> p.symbol == s)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal symbol=" + s));
        }
    }
}
