package year2024.day6;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import year2024.day4.Xmas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GuardRoute {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day6/input.txt"));

        Lab lab = new Lab(lines);

        while(lab.guard.canMove(lab.numberOfRows, lab.numberOfColumns)) {
            lab.moveGuard();
        }

        System.out.println(lab);
        System.out.println(lab.getNumberOfVisitedPositions());

        int numberOfLoops = 0;
        List<Position> positions = lab.guard.uniqueVisitedPositions();
        System.out.println(positions.size());
        System.out.println(lab.guard.visitedPlaces.size());
        for(Position newObstaclePlace : positions) {
            Lab newObstacleLab = new Lab(lines);
            newObstacleLab.addNewObstacle(newObstaclePlace.row, newObstaclePlace.column);
            while(newObstacleLab.guard.canMove(lab.numberOfRows, lab.numberOfColumns)
                    && !newObstacleLab.guard.isInLoop()) {
                newObstacleLab.moveGuard();
            }
            if(newObstacleLab.guard.isInLoop()) {
                System.out.println(newObstacleLab);
                numberOfLoops++;
            }
        }
        System.out.println(numberOfLoops);
    }

    static class Lab {
        LabObject[][] labObjectsMap;
        int numberOfRows;
        int numberOfColumns;
        Guard guard;

        public Lab(List<String> lines) {
            numberOfRows = lines.size();
            numberOfColumns = lines.get(0).length();
            labObjectsMap = new LabObject[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                labObjectsMap[i] = new LabObject[lines.get(i).length()];
                for (int j = 0; j < lines.get(i).length(); j++) {
                    LabObject labObject = LabObject.of(String.valueOf(lines.get(i).charAt(j)));
                    labObjectsMap[i][j] = labObject;
                    if(labObject.equals(LabObject.GUARD)) {
                        guard = new Guard(i,j);
                    }
                }
            }
        }

        public void moveGuard() {
            if(!guard.justTurned) {
                labObjectsMap[guard.currentRow][guard.currentColumn] = LabObject.X;
            }

            int newRow = guard.currentRow + guard.direction.rowMove;
            int newColumn = guard.currentColumn + guard.direction.columnMove;

            if(labObjectsMap[newRow][newColumn].shouldGuardTurn()) {
                guard.turn();
            } else {
                guard.move(newRow, newColumn);
                labObjectsMap[guard.currentRow][guard.currentColumn] = LabObject.X;
            }
        }

        public void addNewObstacle(int row, int column) {
            labObjectsMap[row][column] = LabObject.OBSTACLE;
        }

        public int getNumberOfVisitedPositions() {
            int numberOfVisitedPositions = 0;
            for (LabObject[] labObjects : labObjectsMap) {
                for (LabObject labObject : labObjects) {
                    if (labObject.equals(LabObject.X)) {
                        numberOfVisitedPositions++;
                    }
                }
            }
            return numberOfVisitedPositions;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (LabObject[] labObjects : labObjectsMap) {
                for (LabObject labObject : labObjects) {
                    builder.append(labObject.symbol);
                }
                builder.append("\n");
            }
            return builder.toString();
        }
    }

    static class Guard {
        private Direction direction;
        private int currentRow;
        private int currentColumn;
        private boolean justTurned;
        private List<VisitedPlace> visitedPlaces;

        public Guard(int currentRow, int currentColumn) {
            this.currentRow = currentRow;
            this.currentColumn = currentColumn;
            this.direction = Direction.NORTH;
            this.justTurned = false;
            this.visitedPlaces = new ArrayList<>();
        }

        public boolean canMove(int totalNumberOfRows, int totalNumberOfColumns) {
            return currentRow + direction.rowMove >= 0 && currentColumn + direction.columnMove >= 0
                    && currentRow + direction.rowMove < totalNumberOfRows && currentColumn + direction.columnMove < totalNumberOfColumns;
        }

        public void move(int newRow, int newColumn) {
            this.currentRow = newRow;
            this.currentColumn = newColumn;
            this.justTurned = false;
            this.visitedPlaces.add(new VisitedPlace(currentRow, currentColumn, direction));
        }

        public void turn() {
            this.direction = this.direction.directionAfterTurn();
            this.justTurned = true;
            this.visitedPlaces.add(new VisitedPlace(currentRow, currentColumn, direction));
        }

        public boolean isInLoop() {
            Set<VisitedPlace> set = new HashSet<>(this.visitedPlaces);
            return set.size() < visitedPlaces.size();
        }

        public List<Position> uniqueVisitedPositions() {
            List<Position> uniqueVisitedPositions = new ArrayList<>();
            for (VisitedPlace visitedPlace : visitedPlaces) {
                Position position = new Position(visitedPlace.row, visitedPlace.col);
                if(!uniqueVisitedPositions.contains(position)) {
                    uniqueVisitedPositions.add(position);
                }
            }
            return uniqueVisitedPositions;
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    static class VisitedPlace {
        int row;
        int col;
        Direction direction;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    static class Position {
        int row;
        int column;
    }

    @AllArgsConstructor
    enum Direction {
        NORTH(-1,0),
        EAST(0,1),
        SOUTH(1,0),
        WEST(0,-1);

        private final int rowMove;
        private final int columnMove;

        public Direction directionAfterTurn() {
            return switch(this) {
                case NORTH: yield EAST;
                case EAST: yield SOUTH;
                case SOUTH: yield WEST;
                case WEST: yield NORTH;
            };
        }
    }

    @AllArgsConstructor
    enum LabObject {
        DOT("."),
        HASH("#"),
        GUARD("^"),
        X("X"),
        OBSTACLE("O");

        private final String symbol;

        public static LabObject of(String symbol) {
            return EnumSet.allOf(LabObject.class).stream()
                    .filter(labObject -> labObject.symbol.equals(symbol))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid symbol: " + symbol));
        }

        public boolean shouldGuardTurn() {
            return this == HASH || this == OBSTACLE;
        }

    }


}
