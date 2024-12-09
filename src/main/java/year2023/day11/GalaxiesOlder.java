package year2023.day11;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GalaxiesOlder {

    public static final int GALAXY_AGE = 999999;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day11/input.txt"));

        SpaceSymbol[][] spaceMap = new SpaceSymbol[lines.size()][];
        List<Galaxy> galaxies = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            spaceMap[i] = new SpaceSymbol[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                spaceMap[i][j] = SpaceSymbol.parse(lines.get(i).charAt(j));
                if (spaceMap[i][j] == SpaceSymbol.GALAXY) {
                    galaxies.add(new Galaxy(i, j));
                }
            }
        }

        List<Integer> rowsToExpand = findRowsToExpand(spaceMap);
        List<Integer> colsToExpand = findColsToExpand(spaceMap);

        BigInteger sumOfDistances = BigInteger.ZERO;

        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                sumOfDistances = sumOfDistances.add(galaxies.get(i).findDistanceTo(galaxies.get(j), rowsToExpand, colsToExpand));
//                sumOfDistances += galaxies.get(i).findDistanceTo(galaxies.get(j), rowsToExpand, colsToExpand);
            }
        }

        System.out.println(sumOfDistances);
    }

    static List<Integer> findRowsToExpand(SpaceSymbol[][] spaceMap) {
        List<Integer> rowNumToExpand = new ArrayList<>();

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
        return rowNumToExpand;
    }

    static List<Integer> findColsToExpand(SpaceSymbol[][] spaceMap) {
        List<Integer> colNumToExpand = new ArrayList<>();

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
        return colNumToExpand;
    }

    static class Galaxy {
        int rowNum;
        int colNum;

        public Galaxy(int rowNum, int colNum) {
            this.rowNum = rowNum;
            this.colNum = colNum;
        }

        BigInteger findDistanceTo(Galaxy otherGalaxy, List<Integer> rowsToExpand, List<Integer> colsToExpand) {
            int smallerRowNum = Math.min(rowNum, otherGalaxy.rowNum);
            int biggerRowNum = Math.max(rowNum, otherGalaxy.rowNum);
            int smallerColNum = Math.min(colNum, otherGalaxy.colNum);
            int biggerColNum = Math.max(colNum, otherGalaxy.colNum);
            List<Integer> emptyRowsBetweenGalaxies = rowsToExpand.stream()
                    .filter(rowNum -> rowNum > smallerRowNum && rowNum < biggerRowNum)
                    .toList();
            List<Integer> emptyColsBetweenGalaxies = colsToExpand.stream()
                    .filter(colNum -> colNum > smallerColNum && colNum < biggerColNum)
                    .toList();
            return BigInteger.valueOf(Math.abs(rowNum - otherGalaxy.rowNum)).add(
                    BigInteger.valueOf(emptyRowsBetweenGalaxies.size() * GALAXY_AGE)).add(
                    BigInteger.valueOf(Math.abs(colNum - otherGalaxy.colNum))).add(
                    BigInteger.valueOf(emptyColsBetweenGalaxies.size() * GALAXY_AGE));
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
