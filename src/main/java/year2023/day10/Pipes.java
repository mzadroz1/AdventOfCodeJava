package year2023.day10;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Pipes {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day10/input.txt"));

        PipeSymbol[][] pipesMap = new PipeSymbol[lines.size()][];

        System.out.println(PipeSymbol.EAST_WEST + " " + PipeSymbol.SOUTH_WEST
                + " " + PipeSymbol.GROUND + " " + PipeSymbol.SOUTH_EAST
                + " " + PipeSymbol.NORTH_WEST + " " + PipeSymbol.NORTH_SOUTH);

        for (int i = 0; i < lines.size(); i++) {
            pipesMap[i] = new PipeSymbol[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                pipesMap[i][j] = PipeSymbol.parse(lines.get(i).charAt(j));
            }
        }

//        List<Pipe> pipesCycle;
        int stepsCount = 0;
        Pipe startingPoint = null;
        for (int i = 0; i < pipesMap.length; i++) {
            for (int j = 0; j < pipesMap[i].length; j++) {
                if (pipesMap[i][j] == PipeSymbol.STARTING_POINT) {
                    startingPoint = new Pipe(i, j, PipeSymbol.NORTH_EAST);
                    System.out.println(stepsCount + ": " + startingPoint.symbol.symbol + " i=" + startingPoint.rowNum + " j=" + startingPoint.colNum);
                    startingPoint.neighbours.add(new Pipe(i - 1, j, pipesMap[i - 1][j]));
                    startingPoint.neighbours.add(new Pipe(i, j + 1, pipesMap[i][j + 1]));
                    stepsCount++;
                    break;
                }
            }
        }
        Pipe previousPipe = startingPoint;
        Pipe newPipe = previousPipe.neighbours.get(0);
        while (newPipe.colNum != startingPoint.colNum || newPipe.rowNum != startingPoint.rowNum) {
            System.out.println(stepsCount + ": " + newPipe.symbol.symbol + " i=" + newPipe.rowNum + " j=" + newPipe.colNum);
            int rowNum = newPipe.rowNum;
            int colNum = newPipe.colNum;
            PipeSymbol symbol = newPipe.symbol;

            if (rowNum + symbol.rowShift1 < pipesMap.length && colNum + symbol.colShift1 < pipesMap[rowNum + symbol.rowShift1].length
                    && rowNum + symbol.rowShift1 >= 0 && colNum + symbol.colShift1 >= 0) {
                newPipe.neighbours.add(
                        new Pipe(rowNum + symbol.rowShift1,
                                colNum + symbol.colShift1,
                                pipesMap[rowNum + symbol.rowShift1][colNum + symbol.colShift1]));
            }
            if (rowNum + symbol.rowShift2 < pipesMap.length && colNum + symbol.colShift2 < pipesMap[rowNum + symbol.rowShift1].length
                    && rowNum + symbol.rowShift2 >= 0 && colNum + symbol.colShift2 >= 0) {
                newPipe.neighbours.add(
                        new Pipe(rowNum + symbol.rowShift2,
                                colNum + symbol.colShift2,
                                pipesMap[rowNum + symbol.rowShift2][colNum + symbol.colShift2]));
            }
            Pipe temp = null;
            if(newPipe.neighbours.get(0).rowNum == previousPipe.rowNum
                    && newPipe.neighbours.get(0).colNum == previousPipe.colNum) {
                temp = newPipe.neighbours.get(1);
            }
            else if(newPipe.neighbours.get(1).rowNum == previousPipe.rowNum
                    && newPipe.neighbours.get(1).colNum == previousPipe.colNum) {
                temp = newPipe.neighbours.get(0);
            }
            previousPipe = newPipe;
            newPipe = temp;
            stepsCount++;
        }


    }

    static class Pipe {
        List<Pipe> neighbours = new ArrayList<>();
        int rowNum;
        int colNum;
        PipeSymbol symbol;

        public Pipe(int rowNum, int colNum, PipeSymbol symbol) {
            this.rowNum = rowNum;
            this.colNum = colNum;
            this.symbol = symbol;
        }
    }

    /*
    | is a vertical pipe connecting north and south.
    - is a horizontal pipe connecting east and west.
    L is a 90-degree bend connecting north and east.
    J is a 90-degree bend connecting north and west.
    7 is a 90-degree bend connecting south and west.
    F is a 90-degree bend connecting south and east.
    . is ground; there is no pipe in this tile.
    S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
     */

    enum PipeSymbol {

        NORTH_SOUTH('|', -1, 0, 1, 0),
        EAST_WEST('-', 0, -1, 0, 1),
        NORTH_EAST('L', -1, 0, 0, 1),
        NORTH_WEST('J', -1, 0, 0, -1),
        SOUTH_WEST('7', 1, 0, 0, -1),
        SOUTH_EAST('F', 1, 0, 0, 1),
        GROUND('.'),
        STARTING_POINT('S', -1, 0, 0, 1); //L

        private char symbol;
        private int rowShift1;
        private int colShift1;
        private int rowShift2;
        private int colShift2;

        PipeSymbol(char symbol, int rowShift1, int colShift1, int rowShift2, int colShift2) {
            this.symbol = symbol;
            this.rowShift1 = rowShift1;
            this.colShift1 = colShift1;
            this.rowShift2 = rowShift2;
            this.colShift2 = colShift2;
        }

        PipeSymbol(char symbol) {
            this.symbol = symbol;
        }

        static PipeSymbol parse(char s) {
            return EnumSet.allOf(PipeSymbol.class).stream()
                    .filter(p -> p.symbol == s)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal pipe symbol=" + s));
        }

    }

}
