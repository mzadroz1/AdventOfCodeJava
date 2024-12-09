package year2023.day17;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class HeatNavigationTry2 {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day17/testInput.txt"));

        List<GraphVertex> vertices = new ArrayList<>();
        for(int i = 0; i < lines.size(); i++) {
            for(int j = 0; j < lines.get(i).length(); j++) {
//                if(i == 0 && j == 0) {
//                    vertices.add(new GraphVertex(i, j, Integer.parseInt(String.valueOf(lines.get(combo).charAt(j))), Direction.RIGHT, 2));
//                }
                for(Direction direction : EnumSet.allOf(Direction.class)) {
                    for(int combo = 0; combo < 3; combo++) {
                        vertices.add(new GraphVertex(i, j, Integer.parseInt(String.valueOf(lines.get(i).charAt(j))), direction, combo));
                    }
                }
            }
        }
        vertices.stream().filter(v -> v.rowNum==0 && v.colNum==0)
                        .forEach(v-> {
                            v.heatLossFromSource = 0;
                            v.remainingDirectionCombo = 3;
                        });
        Graph graph = new Graph(vertices, lines.size(), lines.get(0).length());

        while (!graph.areAllVisited()) {
            GraphVertex u = graph.getNextVertex();
            u.setVisited(true);
            if(u.heatLossFromSource < Integer.MAX_VALUE && u.rowNum==12 && u.colNum==12) {
                System.out.println(u.heatLossFromSource);
                graph.showPath(u.previousVertices);
            }

            List<GraphNeighbour> neighbours = graph.getNeighbours(u);
            for(GraphNeighbour v : neighbours) {
                int totalHeat = v.vertex.heatLoss + u.heatLossFromSource;
                if(totalHeat < v.vertex.heatLossFromSource) {
                    v.vertex.heatLossFromSource = totalHeat;
                    v.vertex.previousVertices = new ArrayList<>();
                    v.vertex.previousVertices.addAll(u.previousVertices);
                    v.vertex.previousVertices.add(u);
                    v.vertex.currentDirection = v.direction;
                }
            }
        }

        List<GraphVertex> list = graph.vertices.stream().filter(v -> v.rowNum == 12 && v.colNum == 12).toList();
        System.out.println(list);

    }

    static class Graph {
        List<GraphVertex> vertices;
        int totalRows;
        int totalCols;

        public Graph(List<GraphVertex> vertices, int totalRows, int totalCols) {
            this.vertices = vertices;
            this.totalRows = totalRows;
            this.totalCols = totalCols;
        }

        public boolean areAllVisited() {
            return vertices.stream().allMatch(GraphVertex::isVisited);
        }

        public GraphVertex getNextVertex() {
            return vertices.stream().filter(v -> !v.visited)
                    .sorted(Comparator.comparing(x -> x.heatLossFromSource)).toList().get(0);
        }

        public List<GraphNeighbour> getNeighbours(GraphVertex u) {
            Direction currentDirection = u.getCurrentDirection();

            List<Direction> allowedDirections = new ArrayList<>(currentDirection.allowedDirections());
            if(u.remainingDirectionCombo == 0) {
                allowedDirections.remove(currentDirection);
            }
            List<GraphNeighbour> neighbours = new ArrayList<>();
            for(Direction direction : allowedDirections) {
                int newCombo;
                if(direction == u.currentDirection) {
                    newCombo = u.remainingDirectionCombo - 1;
                } else {
                    newCombo = 2;
                }

                    GraphVertex vertex = vertices.stream().filter(
                                    v -> v.rowNum == u.rowNum + direction.rowChange
                                            && v.colNum == u.colNum + direction.colChange
                            && v.currentDirection == direction
                            && v.remainingDirectionCombo == newCombo)
                            .findFirst()
                            .orElse(null);
                    if(vertex != null ) {
                        neighbours.add(new GraphNeighbour(
                                vertex,
                                direction
                        ));
                    }

            }
            return neighbours;
        }

        public void showPath(List<GraphVertex> previous) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < totalRows; i++) {
                for(int j = 0; j < totalCols; j++) {
                    int finalI = i;
                    int finalJ = j;

                    if(previous.stream().anyMatch(v -> v.rowNum == finalI && v.colNum == finalJ)) {
                        sb.append(previous.stream().filter(v -> v.rowNum == finalI && v.colNum == finalJ)
                                .findFirst().map(v -> v.currentDirection.symbol).orElse(null));
                    }
                    else {
                        sb.append(vertices.stream().filter(v -> v.rowNum == finalI && v.colNum == finalJ).findFirst().map(v-> v.heatLoss).orElse(null));
                    }
                }
                sb.append('\n');
            }
            System.out.println(sb.toString());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < totalRows; i++) {
                for(int j = 0; j < totalCols; j++) {
                    sb.append(vertices.get(i*totalCols + j).heatLoss);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    @AllArgsConstructor
    static class GraphNeighbour {
        GraphVertex vertex;
        Direction direction;
    }

    @Getter
    @Setter
    static class GraphVertex {
        int rowNum;
        int colNum;
        boolean visited = false;
        int heatLoss;
        List<GraphVertex> previousVertices = new ArrayList<>();
        Integer heatLossFromSource = Integer.MAX_VALUE;
        Direction currentDirection;
        int remainingDirectionCombo;

        public GraphVertex(int rowNum, int colNum, int heatLoss, Direction direction, int combo) {
            this.rowNum = rowNum;
            this.colNum = colNum;
            this.heatLoss = heatLoss;
            this.currentDirection = direction;
            this.remainingDirectionCombo = combo;
        }

        public boolean hasToChangeDirection() {
            return previousVertices.size() >= 3 &&
                    (currentDirection == previousVertices.get(previousVertices.size()-1).currentDirection) &&
                    (currentDirection == previousVertices.get(previousVertices.size()-2).currentDirection);
        }
    }

    enum Direction {
        UP(-1, 0, '^'),
        DOWN(1, 0, 'v'),
        LEFT(0, -1, '<'),
        RIGHT(0,1, '>');

        int rowChange;
        int colChange;

        char symbol;

        Direction(int rowChange, int colChange, char symbol) {
            this.rowChange = rowChange;
            this.colChange = colChange;
            this.symbol = symbol;
        }

        public List<Direction> allowedDirections() {
            if(this == UP) {
                return List.of(UP, RIGHT, LEFT);
            }
            if(this == DOWN) {
                return List.of(DOWN, RIGHT, LEFT);
            }
            if(this == RIGHT) {
                return List.of(UP, RIGHT, DOWN);
            }
            if(this == LEFT) {
                return List.of(UP, LEFT, LEFT);
            }
            return null;
        }
    }
}