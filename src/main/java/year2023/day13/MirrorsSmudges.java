package year2023.day13;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MirrorsSmudges {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day13/input.txt"));

        List<Mirror> mirrors = new ArrayList<>();
        List<String> newMirror = new ArrayList<>();
        for (String line : lines) {
            if (line.equals("")) {
                mirrors.add(new Mirror(newMirror));
                newMirror = new ArrayList<>();
                continue;
            }
            newMirror.add(line);
        }
        for (int i = 0; i < mirrors.size(); i++) {
            Mirror mirror = mirrors.get(i);
            if(i == 80) {
                System.out.println(i + " " + mirror.getReflectionsNumber() + " " + mirror.reflectionsInCleanedUpMirror());
            }
        }
        System.out.println(mirrors.stream().mapToInt(Mirror::reflectionsInCleanedUpMirror).sum());

    }

    @Getter
    static class Mirror {

        List<String> mirrorRows = new ArrayList<>();
        List<String> mirrorCols = new ArrayList<>();

        Integer reflectionsNumber;


        public Mirror(List<String> lines) {
            this.mirrorRows.addAll(lines);
            for (int j = 0; j < lines.get(0).length(); j++) {
                StringBuilder newCol = new StringBuilder();
                for (String line : lines) {
                    newCol.append(line.charAt(j));
                }
                this.mirrorCols.add(newCol.toString());
            }
            this.reflectionsNumber = getNumberOfReflectedRows();
        }

        public Mirror(List<String> mirrorRows, List<String> mirrorCols) {
            this.mirrorRows = mirrorRows;
            this.mirrorCols = mirrorCols;
        }

        public Integer reflectionsInCleanedUpMirror() {
            for (int i = 0; i < mirrorRows.size(); i++) {
                for(int j = i+1; j < mirrorRows.size(); j++) {
                    String row = mirrorRows.get(i);
                    Integer indexOfDifference = locateSmudge(row, mirrorRows.get(j));
                    if(indexOfDifference != null) {
                        StringBuilder cleanedUpRow = new StringBuilder(row);
                        if(row.charAt(indexOfDifference) == '.') {
                            cleanedUpRow.setCharAt(indexOfDifference, '#');
                        } else {
                            cleanedUpRow.setCharAt(indexOfDifference, '.');
                        }
                        String dirtyCol = this.mirrorCols.get(indexOfDifference);
                        StringBuilder cleanedUpCol = new StringBuilder(dirtyCol);
                        if(dirtyCol.charAt(i) == '.') {
                            cleanedUpCol.setCharAt(i, '#');
                        } else {
                            cleanedUpCol.setCharAt(i, '.');
                        }
                        List<String> cleanedUpRows = new ArrayList<>(this.mirrorRows);
                        List<String> cleanedUpCols = new ArrayList<>(this.mirrorCols);
                        cleanedUpRows.set(i, cleanedUpRow.toString());
                        cleanedUpCols.set(indexOfDifference, cleanedUpCol.toString());
                        Mirror cleanedUpMirror = new Mirror(cleanedUpRows, cleanedUpCols);
                        int numberOfReflectedRows = cleanedUpMirror.getNumberOfReflectedRowsAfterCleanUp(this.reflectionsNumber);
                        if(numberOfReflectedRows != 0) {
                            return numberOfReflectedRows;
                        }
                    }
                }
            }
            for (int i = 0; i < mirrorCols.size(); i++) {
                for(int j = i+1; j < mirrorCols.size(); j++) {
                    String col = mirrorCols.get(i);
                    String xol = mirrorCols.get(j);
                    Integer indexOfDifference = locateSmudge(col, xol);
                    if(indexOfDifference != null) {
                        StringBuilder cleanedUpCol = new StringBuilder(col);
                        if(col.charAt(indexOfDifference) == '.') {
                            cleanedUpCol.setCharAt(indexOfDifference, '#');
                        } else {
                            cleanedUpCol.setCharAt(indexOfDifference, '.');
                        }
                        String dirtyRow = this.mirrorRows.get(indexOfDifference);
                        StringBuilder cleanedUpRow = new StringBuilder(dirtyRow);
                        if(dirtyRow.charAt(i) == '.') {
                            cleanedUpRow.setCharAt(i, '#');
                        } else {
                            cleanedUpRow.setCharAt(i, '.');
                        }
                        List<String> cleanedUpRows = new ArrayList<>(this.mirrorRows);
                        List<String> cleanedUpCols = new ArrayList<>(this.mirrorCols);
                        cleanedUpCols.set(i, cleanedUpCol.toString());
                        cleanedUpRows.set(indexOfDifference, cleanedUpRow.toString());
                        Mirror cleanedUpMirror = new Mirror(cleanedUpRows, cleanedUpCols);
                        int numberOfReflectedRows = cleanedUpMirror.getNumberOfReflectedRowsAfterCleanUp(this.reflectionsNumber);
                        if(numberOfReflectedRows != 0) {
                            return numberOfReflectedRows;
                        }
                    }
                }
            }
            return null;
        }

        public static Integer locateSmudge(String x, String y) {
            int differencesCount = 0;
            int indexOfDifference = 0;
            for(int i = 0; i < x.length(); i++) {
                if(differencesCount > 1) {
                    return null;
                }
                if(x.charAt(i) != y.charAt(i)) {
                    indexOfDifference = i;
                    differencesCount++;
                }
            }
            if(differencesCount == 1) {
                return indexOfDifference;
            }
            return null;
        }

        public int getNumberOfReflectedRows() {
            List<Integer> rowReflections = findRowReflection();
            for(Integer rowReflection : rowReflections) {
                if(rowReflection > 0) {
                    return 100 * rowReflection;
                }
            }
            List<Integer> colReflections = findColReflection();
            for(Integer colReflection : colReflections) {
                if(colReflection > 0) {
                    return colReflection;
                }
            }
            return 0;
        }

        public int getNumberOfReflectedRowsAfterCleanUp(int currentReflectionNumber) {
            List<Integer> rowReflections = findRowReflection();
            for(Integer rowReflection : rowReflections) {
                if(rowReflection > 0 && rowReflection*100 != currentReflectionNumber) {
                    return 100 * rowReflection;
                }
            }
            List<Integer> colReflections = findColReflection();
            for(Integer colReflection : colReflections) {
                if(colReflection > 0 && colReflection != currentReflectionNumber) {
                    return colReflection;
                }
            }
            return 0;
        }

        public List<Integer> findRowReflection() {
            return findReflection(mirrorRows);
        }

        public List<Integer> findColReflection() {
            return findReflection(mirrorCols);
        }

        private List<Integer> findReflection(List<String> mirrorCols) {
            List<Integer> results = new ArrayList<>();
            for(int i = 0; (i+1) < mirrorCols.size(); i++) {
                if(mirrorCols.get(i).equals(mirrorCols.get(i+1))) {
                    if(verifyReflection(mirrorCols, i+1, i)) {
                        results.add(i+1);
                    }
                }
            }
            return results;
        }

        private static boolean verifyReflection(List<String> mirror, int upperBound, int lowerBound) {
            while(lowerBound >= 0 && (upperBound) < mirror.size()) {
                if(mirror.get(upperBound).equals(mirror.get(lowerBound))) {
                    upperBound++;
                    lowerBound--;
                }
                else {
                    return false;
                }
            }
            return true;
        }
    }


}
