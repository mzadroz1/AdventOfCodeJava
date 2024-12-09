package year2024.day8;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BunnyAntennas {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day8/input.txt"));

        //part 1
        AntennasMap antennasMap = new AntennasMap(lines);
        antennasMap.findAntinodes();
        System.out.println(antennasMap.countAntinodes());

        //part 2
        AntennasMap antennasMap2 = new AntennasMap(lines);
        antennasMap2.findAntinodesWithResonantHarmonics();
        System.out.println(antennasMap2.countAntinodes());

    }

    static class AntennasMap {
        MapObject[][] objectsMap;
        int numberOfRows;
        int numberOfColumns;
        Map<String, List<MapObject>> groupedObjects = new HashMap<>();
        Boolean[][] antinodesMap;

        public AntennasMap(List<String> lines) {
            numberOfRows = lines.size();
            numberOfColumns = lines.get(0).length();
            objectsMap = new MapObject[lines.size()][];
            antinodesMap = new Boolean[numberOfRows][numberOfColumns];
            for (int i = 0; i < lines.size(); i++) {
                objectsMap[i] = new MapObject[lines.get(i).length()];
                for (int j = 0; j < lines.get(i).length(); j++) {
                    String symbol = String.valueOf(lines.get(i).charAt(j));
                    MapObject mapObject = new MapObject(symbol, i, j);
                    objectsMap[i][j] = mapObject;
                    if(!groupedObjects.containsKey(symbol)) {
                        groupedObjects.put(symbol, new ArrayList<>());
                    }
                    groupedObjects.get(symbol).add(mapObject);
                    antinodesMap[i][j] = false;
                }
            }
        }

        public void findAntinodes() {
            for(String symbol : groupedObjects.keySet()) {
                if(symbol.equals(".")) {
                    continue;
                }
                List<MapObject> mapObjects = groupedObjects.get(symbol);
                for(int i = 0; i < mapObjects.size(); i++) {
                    for(int j = 0; j < mapObjects.size(); j++) {
                        if(i != j) {
                            MapObject firstAntenna = mapObjects.get(i);
                            MapObject secondAntenna = mapObjects.get(j);
                            int rowShift = secondAntenna.row - firstAntenna.row;
                            int columnShift = secondAntenna.column - firstAntenna.column;
                            int potentialAntinode1Row = secondAntenna.row + rowShift;
                            int potentialAntinode2Row = firstAntenna.row - rowShift;
                            int potentialAntinode1Col = secondAntenna.column + columnShift;
                            int potentialAntinode2Col = firstAntenna.column - columnShift;
                            if(potentialAntinode1Row >= 0 && potentialAntinode1Row < numberOfRows
                                    && potentialAntinode1Col >= 0 && potentialAntinode1Col < numberOfColumns) {
                                antinodesMap[potentialAntinode1Row][potentialAntinode1Col] = true;
                            }
                            if(potentialAntinode2Row >= 0 && potentialAntinode2Row < numberOfRows
                                    && potentialAntinode2Col >= 0 && potentialAntinode2Col < numberOfColumns) {
                                antinodesMap[potentialAntinode2Row][potentialAntinode2Col] = true;
                            }
                        }
                    }
                }
            }
        }

        public void findAntinodesWithResonantHarmonics() {
            for(String symbol : groupedObjects.keySet()) {
                if(symbol.equals(".")) {
                    continue;
                }
                List<MapObject> mapObjects = groupedObjects.get(symbol);
                for(int i = 0; i < mapObjects.size(); i++) {
                    for(int j = 0; j < mapObjects.size(); j++) {
                        if(i != j) {
                            MapObject firstAntenna = mapObjects.get(i);
                            MapObject secondAntenna = mapObjects.get(j);
                            antinodesMap[firstAntenna.row][firstAntenna.column] = true;
                            antinodesMap[secondAntenna.row][secondAntenna.column] = true;
                            int rowShift = secondAntenna.row - firstAntenna.row;
                            int columnShift = secondAntenna.column - firstAntenna.column;
                            int potentialAntinode1Row = secondAntenna.row + rowShift;
                            int potentialAntinode2Row = firstAntenna.row - rowShift;
                            int potentialAntinode1Col = secondAntenna.column + columnShift;
                            int potentialAntinode2Col = firstAntenna.column - columnShift;
                            int distance = 1;
                            while(potentialAntinode1Row >= 0 && potentialAntinode1Row < numberOfRows
                                    && potentialAntinode1Col >= 0 && potentialAntinode1Col < numberOfColumns) {
                                antinodesMap[potentialAntinode1Row][potentialAntinode1Col] = true;
                                distance++;
                                potentialAntinode1Row = secondAntenna.row + rowShift * distance;
                                potentialAntinode1Col = secondAntenna.column + columnShift * distance;
                            }
                            distance = 1;
                            while(potentialAntinode2Row >= 0 && potentialAntinode2Row < numberOfRows
                                    && potentialAntinode2Col >= 0 && potentialAntinode2Col < numberOfColumns) {
                                antinodesMap[potentialAntinode2Row][potentialAntinode2Col] = true;
                                distance++;
                                potentialAntinode2Row = firstAntenna.row - rowShift * distance;
                                potentialAntinode2Col = firstAntenna.column - columnShift * distance;
                            }
                        }
                    }
                }
            }
        }

        public int countAntinodes() {
            int count = 0;
            for(int i = 0; i < numberOfRows; i++) {
                for(int j = 0; j < numberOfColumns; j++) {
                    if(antinodesMap[i][j]) {
                        count++;
                    }
                }
            }
            return count;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (MapObject[] mapObjects : objectsMap) {
                for (MapObject mapObject : mapObjects) {
                    builder.append(mapObject.symbol);
                }
                builder.append("\n");
            }
            return builder.toString();
        }
    }

    @AllArgsConstructor
    static class MapObject {
        String symbol;
        int row;
        int column;
    }


}
