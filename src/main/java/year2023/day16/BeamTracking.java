package year2023.day16;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BeamTracking {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day16/input.txt"));

        ContraptionGrid contraptionGrid = new ContraptionGrid(lines);

        while(contraptionGrid.isAnyRayMoving()) {
            contraptionGrid.rayTracing();
        }

        System.out.println(contraptionGrid.getNumberOfEnergizedTiles());
    }

    static class ContraptionGrid {
        ContraptionTile[][] contraptionGrid;
        List<BeamRay> rays = new ArrayList<>();

        List<BeamRayPosition> visitedPositions = new ArrayList<>();

        public ContraptionGrid(List<String> lines) {
            contraptionGrid = new ContraptionTile[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                contraptionGrid[i] = new ContraptionTile[lines.get(i).length()];
                for (int j = 0; j < lines.get(i).length(); j++) {
                    contraptionGrid[i][j] = new ContraptionTile(ContraptionSymbol.parse(lines.get(i).charAt(j)), i, j, false);
                }
            }
            rays.add(new BeamRay(0, 0, Direction.RIGHT));
        }

        public boolean isAnyRayMoving() {
            return rays.stream().anyMatch(BeamRay::isMoving);
        }

        public void rayTracing() {
            for (int i = 0; i < rays.size(); i++) {
                BeamRay ray = rays.get(i);
                if (hasRayReachedTheWall(ray)) {
                    ray.stopMoving();
                }
                if(this.visitedPositions.contains(ray.getPosition())) {
                    ray.stopMoving();
                } else {
                    this.visitedPositions.add(ray.getPosition());
                }
                if (!ray.isMoving()) {
                    continue;
                }

                contraptionGrid[ray.getRow()][ray.getCol()].setEnergized(true);
                ContraptionSymbol currentTile = contraptionGrid[ray.getRow()][ray.getCol()].getSymbol();
                List<Direction> newDirections = currentTile.transitDirection(ray.getDirection());
                if (newDirections.size() > 1) {
                    BeamRay newRay = new BeamRay(ray.getRow(), ray.getCol(), newDirections.get(1));
                    newRay.move(newDirections.get(1));
                    this.rays.add(newRay);
                }
                ray.move(newDirections.get(0));
            }
        }

        public int getNumberOfEnergizedTiles() {
            int sum = 0;
            for(int i = 0; i < contraptionGrid.length; i++) {
                for(int j = 0; j < contraptionGrid[i].length; j++) {
                    if(contraptionGrid[i][j].energized) {
                        sum++;
                    }
                }
            }
            return sum;
        }

        private boolean hasRayReachedTheWall(BeamRay ray) {
            return ray.getRow() < 0 || ray.getRow() >= contraptionGrid.length
                    || ray.getCol() < 0 || ray.getCol() >= contraptionGrid[ray.getRow()].length;
        }
    }

    static class BeamRay {
        BeamRayPosition position;
        boolean moving;


        public BeamRay(int row, int col, Direction direction) {
            this.position = new BeamRayPosition(row, col, direction);
            this.moving = true;
        }

        public void move(Direction direction) {
            this.position = new BeamRayPosition(
                    this.position.row + direction.getRowTransition(),
                    this.position.col + direction.getColTransition(),
                    direction);
        }

        public boolean isMoving() {
            return moving;
        }

        public void stopMoving() {
            this.moving = false;
        }

        public int getRow() {
            return position.row;
        }

        public int getCol() {
            return position.col;
        }

        public BeamRayPosition getPosition() {
            return position;
        }

        public Direction getDirection() {
            return position.direction;
        }

    }

    @AllArgsConstructor
    static class BeamRayPosition {
        int row;
        int col;
        Direction direction;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BeamRayPosition that = (BeamRayPosition) o;
            return row == that.row && col == that.col && direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, direction);
        }
    }

    @Getter
    @AllArgsConstructor
    static class ContraptionTile {


        ContraptionSymbol symbol;
        int i;
        int j;
        boolean energized;

        public void setEnergized(boolean energized) {
            this.energized = energized;
        }
    }

    enum ContraptionSymbol {

        EMPTY_SPACE('.', List.of(
                DirectionTransition.of(Direction.UP, Direction.UP),
                DirectionTransition.of(Direction.RIGHT, Direction.RIGHT),
                DirectionTransition.of(Direction.LEFT, Direction.LEFT),
                DirectionTransition.of(Direction.DOWN, Direction.DOWN)
        )),
        MIRROR_LEFT('\\', List.of(
                DirectionTransition.of(Direction.UP, Direction.LEFT),
                DirectionTransition.of(Direction.RIGHT, Direction.DOWN),
                DirectionTransition.of(Direction.LEFT, Direction.UP),
                DirectionTransition.of(Direction.DOWN, Direction.RIGHT)
        )),
        MIRROR_RIGHT('/', List.of(
                DirectionTransition.of(Direction.UP, Direction.RIGHT),
                DirectionTransition.of(Direction.RIGHT, Direction.UP),
                DirectionTransition.of(Direction.LEFT, Direction.DOWN),
                DirectionTransition.of(Direction.DOWN, Direction.LEFT)
        )),
        SPLITTER_VERTICAL('|', List.of(
                DirectionTransition.of(Direction.UP, Direction.UP),
                DirectionTransition.of(Direction.RIGHT, Direction.UP),
                DirectionTransition.of(Direction.RIGHT, Direction.DOWN),
                DirectionTransition.of(Direction.LEFT, Direction.UP),
                DirectionTransition.of(Direction.LEFT, Direction.DOWN),
                DirectionTransition.of(Direction.DOWN, Direction.DOWN)
        )),
        SPLITTER_HORIZONTAL('-', List.of(
                DirectionTransition.of(Direction.UP, Direction.LEFT),
                DirectionTransition.of(Direction.UP, Direction.RIGHT),
                DirectionTransition.of(Direction.RIGHT, Direction.RIGHT),
                DirectionTransition.of(Direction.LEFT, Direction.LEFT),
                DirectionTransition.of(Direction.DOWN, Direction.LEFT),
                DirectionTransition.of(Direction.DOWN, Direction.RIGHT)
        ));

        private final char symbol;
        private final List<DirectionTransition> directionTransitions;

        ContraptionSymbol(char symbol, List<DirectionTransition> directionTransitions) {
            this.symbol = symbol;
            this.directionTransitions = directionTransitions;
        }

        static ContraptionSymbol parse(char s) {
            return EnumSet.allOf(ContraptionSymbol.class).stream()
                    .filter(p -> p.symbol == s)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal symbol=" + s));
        }

        public List<Direction> transitDirection(Direction direction) {
            return this.directionTransitions.stream()
                    .filter(directionTransition -> directionTransition.currentDirection.equals(direction))
                    .map(DirectionTransition::getNewDirection)
                    .toList();
        }
    }

    @Getter
    static class DirectionTransition {
        Direction currentDirection;
        Direction newDirection;

        public static DirectionTransition of(Direction currentDirection, Direction newDirection) {
            DirectionTransition directionTransition = new DirectionTransition();
            directionTransition.currentDirection = currentDirection;
            directionTransition.newDirection = newDirection;
            return directionTransition;
        }
    }

    @Getter
    enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        RIGHT(0, 1),
        LEFT(0, -1);

        private final int rowTransition;
        private final int colTransition;

        Direction(int rowTransition, int colTransition) {
            this.rowTransition = rowTransition;
            this.colTransition = colTransition;
        }
    }
}
