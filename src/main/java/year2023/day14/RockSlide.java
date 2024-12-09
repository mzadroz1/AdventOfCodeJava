package year2023.day14;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class RockSlide {

    public static void main(String[] args) throws IOException {
//        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day14/testInput.txt"));
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day14/input.txt"));

        RocksPlatform rocksPlatform = new RocksPlatform(lines);

        HashMap<Integer, RocksPlatform> foundStates = new HashMap<>();

        RocksPlatform tilted = RocksPlatform.fromColumnsList(rocksPlatform.rocksCols);
        for(int i = 0; i < 1000000000; i++) {
            if(i%1000000 == 0) {
                System.out.println(i + " cycles processed");
            }
            tilted = tiltCycle(tilted);
            if(foundStates.containsValue(tilted)) {
                RocksPlatform finalTilted = tilted;
                Integer previousI = foundStates.entrySet()
                        .stream()
                        .filter(entry -> Objects.equals(entry.getValue(), finalTilted))
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElseThrow(() -> new IllegalStateException("boom"));
                System.out.println(i + "!!!" + " previous i:" + previousI);
                int cycleLength = i - previousI;
                int end = (1000000000 - i) % cycleLength - 1;
                RocksPlatform newTilted = RocksPlatform.fromColumnsList(foundStates.get(previousI).rocksCols);
                for(int j = 0; j < end; j++) {
                    newTilted = tiltCycle(newTilted);
                    System.out.println(newTilted.calculateLoadOnTiltedNorth());
                }
                return;
            }
            foundStates.put(i, tilted);
        }
        System.out.println(tilted.calculateLoadOnTiltedNorth());

    }

    private static RocksPlatform tiltCycle(RocksPlatform rocksPlatform) {
        RocksPlatform tilted = rocksPlatform.tilt(Direction.NORTH);
        tilted = tilted.tilt(Direction.WEST);
        tilted = tilted.tilt(Direction.SOUTH);
        tilted = tilted.tilt(Direction.EAST);
        return tilted;
    }

    @NoArgsConstructor
    @Getter
    static class RocksPlatform {

        int rowNum;

        List<String> rocksRows = new ArrayList<>();
        List<String> rocksCols = new ArrayList<>();

        public RocksPlatform(List<String> lines) {
            this.rowNum = lines.size();
            this.rocksRows.addAll(lines);
            for (int j = 0; j < lines.get(0).length(); j++) {
                StringBuilder newCol = new StringBuilder();
                for (String line : lines) {
                    newCol.append(line.charAt(j));
                }
                this.rocksCols.add(newCol.toString());
            }
        }

        public static RocksPlatform fromColumnsList(List<String> columns) {
            RocksPlatform rocksPlatform = new RocksPlatform();
            rocksPlatform.rocksCols = columns;
            for (int i = 0; i < columns.get(0).length(); i++) {
                StringBuilder newRow = new StringBuilder();
                for (String col : columns) {
                    newRow.append(col.charAt(i));
                }
                rocksPlatform.rocksRows.add(newRow.toString());
            }
            rocksPlatform.rowNum = rocksPlatform.rocksRows.size();
            return rocksPlatform;
        }

        private static RocksPlatform fromRowsList(List<String> rows) {
            RocksPlatform rocksPlatform = new RocksPlatform();
            rocksPlatform.rocksRows = rows;
            for (int i = 0; i < rows.get(0).length(); i++) {
                StringBuilder newCol = new StringBuilder();
                for (String col : rows) {
                    newCol.append(col.charAt(i));
                }
                rocksPlatform.rocksCols.add(newCol.toString());
            }
            rocksPlatform.rowNum = rocksPlatform.rocksRows.size();
            return rocksPlatform;
        }

        public RocksPlatform tilt(Direction direction) {
            List<String> newCollection = new ArrayList<>();
            List<String> rowsOrCols;
            if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
                rowsOrCols = rocksCols;
            } else {
                rowsOrCols = rocksRows;
            }
            for (String str : rowsOrCols) {
                StringBuilder tiltedStr = new StringBuilder();
                String[] segments = str.split("#");
                for (int i = 0; i < segments.length; i++) {
                    String segment = segments[i];
                    char[] ar = segment.toCharArray();
                    Arrays.sort(ar);
                    StringBuilder sortedSegment;
                    if (direction == Direction.NORTH || direction == Direction.WEST) {
                        sortedSegment = new StringBuilder(new String(ar)).reverse();
                    } else {
                        sortedSegment = new StringBuilder(new String(ar));
                    }
                    tiltedStr.append(sortedSegment);
                    if (i + 1 < segments.length) {
                        tiltedStr.append("#");
                    }
                }
                while (tiltedStr.length() < str.length()) {
                    tiltedStr.append("#");
                }
                newCollection.add(tiltedStr.toString());
            }
            if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
                return RocksPlatform.fromColumnsList(newCollection);
            } else {
                return RocksPlatform.fromRowsList(newCollection);
            }
        }
        public Integer calculateLoadOnTiltedNorth() {
            int sum = 0;
            for (String column : rocksCols) {
                for (int i = 0; i < column.length(); i++) {
                    if (column.charAt(i) == 'O') {
                        sum += (rowNum - i);
                    }
                }
            }
            return sum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RocksPlatform that = (RocksPlatform) o;
            return rowNum == that.rowNum && Objects.equals(rocksRows, that.rocksRows) && Objects.equals(rocksCols, that.rocksCols);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rowNum, rocksRows, rocksCols);
        }
    }

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
