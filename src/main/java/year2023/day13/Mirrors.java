package year2023.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Mirrors {

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
        System.out.println(mirrors.stream().mapToInt(Mirror::getNumberOfReflectedRows).sum());

    }

    static class Mirror {

        int rowNum;
        int colNum;

        char[][] mirror;

        List<String> mirrorRows = new ArrayList<>();
        List<String> mirrorCols = new ArrayList<>();


        public Mirror(List<String> lines) {
            this.rowNum = lines.size();
            this.colNum = lines.get(0).length();
//            mirror = new char[lines.size()][];
//            for (int i = 0; i < lines.size(); i++) {
//
//                this.mirror[i] = new char[lines.get(i).length()];
//                for (int j = 0; j < lines.get(i).length(); j++) {
//                    this.mirror[i][j] = lines.get(i).charAt(j);
//                }
//            }
            this.mirrorRows.addAll(lines);
            for (int j = 0; j < lines.get(0).length(); j++) {
                StringBuilder newCol = new StringBuilder();
                for (String line : lines) {
                    newCol.append(line.charAt(j));
                }
                this.mirrorCols.add(newCol.toString());
            }
        }

        public int getNumberOfReflectedRows() {
            return findColReflection() + 100 * findRowReflection();
        }

        public int findRowReflection() {
            return findReflection(mirrorRows);
        }

        public int findColReflection() {
            return findReflection(mirrorCols);
        }

        private int findReflection(List<String> mirrorCols) {
            for(int i = 0; (i+1) < mirrorCols.size(); i++) {
                if(mirrorCols.get(i).equals(mirrorCols.get(i+1))) {
                    if(verifyReflection(mirrorCols, i+1, i)) {
                        return i+1;
                    }
                }
            }
            return 0;
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
