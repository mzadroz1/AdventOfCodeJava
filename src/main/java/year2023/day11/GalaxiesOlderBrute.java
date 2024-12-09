package year2023.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GalaxiesOlderBrute {

    public static final int GALAXY_AGE = 999999;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day11/testInput.txt"));

        SpaceSymbol[][] spaceMap = new SpaceSymbol[lines.size()][];

        for (int i = 0; i < lines.size(); i++) {
            spaceMap[i] = new SpaceSymbol[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                spaceMap[i][j] = SpaceSymbol.parse(lines.get(i).charAt(j));
            }
        }

        SpaceSymbol[][] expandedSpace = expandSpace(spaceMap);
//        System.out.println(Arrays.deepToString(expandedSpace));

        List<Galaxy> galaxies = new ArrayList<>();

        for (int i = 0; i < expandedSpace.length; i++) {
            for (int j = 0; j < expandedSpace[i].length; j++) {
                if (expandedSpace[i][j] == SpaceSymbol.GALAXY) {
                    galaxies.add(new Galaxy(i, j));
                }
            }
        }

        int sumOfDistances = 0;

        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                sumOfDistances += galaxies.get(i).findDistanceTo(galaxies.get(j));
            }
        }

        System.out.println(sumOfDistances);
    }

    static SpaceSymbol[][] expandSpace(SpaceSymbol[][] spaceMap) {
        List<Integer> rowNumToExpand = new ArrayList<>();
        List<Integer> colNumToExpand = new ArrayList<>();

        for (int i = 0; i < spaceMap.length; i++) {
            boolean onlyVoidInRow = true;
            for (int j = 0; j < spaceMap[i].length; j++) {
                if (spaceMap[i][j] != SpaceSymbol.VOID) {
                    onlyVoidInRow = false;
                }
            }
            if (onlyVoidInRow) {
                rowNumToExpand.add(i);
            }
        }
        for (int j = 0; j < spaceMap[0].length; j++) {
            boolean onlyVoidInCol = true;
            for (int i = 0; i < spaceMap.length; i++) {
                if (spaceMap[i][j] != SpaceSymbol.VOID) {
                    onlyVoidInCol = false;
                }
            }
            if (onlyVoidInCol) {
                colNumToExpand.add(j);
            }
        }

        int newRowNum = spaceMap.length + rowNumToExpand.size()*GALAXY_AGE;
        int newColNum = spaceMap[0].length + colNumToExpand.size()*GALAXY_AGE;

        SpaceSymbol[][] expandedSpaceMap = new SpaceSymbol[newRowNum][];

        int rowIndexOriginal = 0;
        int colIndexOriginal = 0;
        int rowIndexNew = 0;
        int colIndexNew = 0;
        while (rowIndexNew < newRowNum) {
            expandedSpaceMap[rowIndexNew] = new SpaceSymbol[newColNum];
            if (rowNumToExpand.contains(rowIndexOriginal)) {
                for(int i = 0; i < GALAXY_AGE; i++) {
                    for (colIndexNew = 0; colIndexNew < newColNum; colIndexNew++) {
                        expandedSpaceMap[rowIndexNew][colIndexNew] = SpaceSymbol.VOID;
                    }
                    rowIndexNew++;
                    expandedSpaceMap[rowIndexNew] = new SpaceSymbol[newColNum];
                    colIndexNew = 0;
                    colIndexOriginal = 0;
                }
            }
            while (colIndexOriginal < spaceMap[rowIndexOriginal].length) {
                if (colNumToExpand.contains(colIndexOriginal)) {
                    for(int i = 0; i< GALAXY_AGE; i++) {
                        expandedSpaceMap[rowIndexNew][colIndexNew] = SpaceSymbol.VOID;
                        colIndexNew++;
                    }
                }
                expandedSpaceMap[rowIndexNew][colIndexNew] = spaceMap[rowIndexOriginal][colIndexOriginal];
                colIndexNew++;
                colIndexOriginal++;

            }
            rowIndexOriginal++;
            rowIndexNew++;
            colIndexOriginal = 0;
            colIndexNew = 0;
        }
        return expandedSpaceMap;
    }

    static class Galaxy {
        int rowNum;
        int colNum;

        public Galaxy(int rowNum, int colNum) {
            this.rowNum = rowNum;
            this.colNum = colNum;
        }

        int findDistanceTo(Galaxy otherGalaxy) {
            return Math.abs(rowNum - otherGalaxy.rowNum) + Math.abs(colNum - otherGalaxy.colNum);
        }
    }

    enum SpaceSymbol {

        GALAXY('#'),
        VOID('.');

        private char symbol;

        SpaceSymbol(char symbol) {
            this.symbol = symbol;
        }

        static SpaceSymbol parse(char s) {
            return EnumSet.allOf(SpaceSymbol.class).stream()
                    .filter(p -> p.symbol == s)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal space symbol=" + s));
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

}
