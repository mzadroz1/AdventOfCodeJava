package year2023.day18;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GridDigging {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day18/input.txt"));

        List<DiggingStep> diggingSteps = new ArrayList<>();
        for(String line : lines) {
            diggingSteps.add(new DiggingStep(line));
        }

        int maxRight = diggingSteps.stream().filter(step -> step.direction==Direction.RIGHT)
                        .mapToInt(DiggingStep::getDistance).sum();
        int maxDown = diggingSteps.stream().filter(step -> step.direction==Direction.DOWN)
                .mapToInt(DiggingStep::getDistance).sum();
        int maxLeft = diggingSteps.stream().filter(step -> step.direction==Direction.LEFT)
                .mapToInt(DiggingStep::getDistance).sum();
        int maxUp = diggingSteps.stream().filter(step -> step.direction==Direction.UP)
                .mapToInt(DiggingStep::getDistance).sum();

        int rows = maxDown + maxUp;
        int cols = maxLeft + 15;
        char[][] grid = new char[rows][];

        for (int i = 0; i < rows; i++) {
            grid[i] = new char[cols];
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }

        int currentRow = maxDown-1;
        int currentCol = 0;
        for(DiggingStep diggingStep : diggingSteps) {
            for(int i = 0; i < diggingStep.distance; i++) {
                grid[currentRow][currentCol] = '#';
                currentRow = currentRow + diggingStep.direction.rowChange;
                currentCol = currentCol + diggingStep.direction.colChange;
            }
        }


        currentRow = maxDown;
        currentCol = 1;
        Queue<Pixel> pixels = new ArrayDeque<>();
        pixels.add(new Pixel(currentRow, currentCol));
        while (!pixels.isEmpty()) {
            Pixel pixel = pixels.poll();
            int i = pixel.i;
            int j = pixel.j;
            grid[i][j] = '+';
            if(i + 1 < rows && grid[i+1][j] == '.') {
                grid[i+1][j] = '+';
                pixels.add(new Pixel(i+1, j));
            }
            if(i - 1 >= 0 && grid[i-1][j] == '.') {
                grid[i-1][j] = '+';
                pixels.add(new Pixel(i-1, j));
            }
            if(j + 1 < cols && grid[i][j+1] == '.') {
                grid[i][j+1] = '+';
                pixels.add(new Pixel(i, j+1));
            }
            if(j - 1 >= 0 && grid[i][j-1] == '.') {
                grid[i][j-1] = '+';
                pixels.add(new Pixel(i, j-1));
            }
        }




        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            StringBuilder row = new StringBuilder();
            boolean showRow = false;
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == '#') {
                    showRow = true;
                }
                row.append(grid[i][j]);
            }
            row.append('\n');
            if(showRow) {
                stringBuilder.append(row);
            }
        }
        System.out.println(stringBuilder);

        int counter = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == '#' || grid[i][j] == '+') {
                    counter++;
                };
            }
        }
        System.out.println(counter);
    }



    @AllArgsConstructor
    static class Pixel {
        int i;
        int j;
    }

    @Getter
    static class DiggingStep {

        Direction direction;
        int distance;
        String color;

        public DiggingStep(String line) {
            String[] parts = line.split(" ");
            this.direction = Direction.parse(parts[0]);
            this.distance = Integer.parseInt(parts[1]);
            this.color = parts[2];
        }
    }

    @Getter
    enum Direction {
        UP('U', -1, 0),
        DOWN('D', 1, 0),
        LEFT('L', 0, -1),
        RIGHT('R', 0, 1);

        private char symbol;

        private int rowChange;
        private int colChange;

        Direction(char symbol, int rowChange, int colChange) {
            this.symbol = symbol;
            this.rowChange = rowChange;
            this.colChange = colChange;
        }

        static Direction parse(String s) {
            return EnumSet.allOf(Direction.class).stream()
                    .filter(p -> p.symbol == s.charAt(0))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal symbol=" + s));
        }
    }
}
