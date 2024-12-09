package year2024.day4;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;

public class Xmas {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day4/input.txt"));

        XmasLetter[][] xmasLettersMap = new XmasLetter[lines.size()][];

        for (int i = 0; i < lines.size(); i++) {
            xmasLettersMap[i] = new XmasLetter[lines.get(i).length()];
            for (int j = 0; j < lines.get(i).length(); j++) {
                xmasLettersMap[i][j] = XmasLetter.of(String.valueOf(lines.get(i).charAt(j)));
            }
        }

        int xmasCounter = 0;

        for(int i = 0; i < xmasLettersMap.length; i++) {
            for (int j = 0; j < xmasLettersMap[i].length; j++) {
                if(xmasLettersMap[i][j] == XmasLetter.X) {
                    List<XmasDirection> xmasDirections = EnumSet.allOf(XmasDirection.class).stream().toList();
                    for (XmasDirection xmasDirection : xmasDirections) {
                        if(i + xmasDirection.rowShift * 3 >= 0 && i + xmasDirection.rowShift * 3 < xmasLettersMap[i].length
                            && j + xmasDirection.colShift * 3 < xmasLettersMap.length && j + xmasDirection.colShift * 3 >= 0) {
                            if(xmasLettersMap[i + xmasDirection.rowShift][j + xmasDirection.colShift] == XmasLetter.M &&
                                    xmasLettersMap[i + 2 * xmasDirection.rowShift][j + 2 * xmasDirection.colShift] == XmasLetter.A &&
                                    xmasLettersMap[i + 3 * xmasDirection.rowShift][j + 3 * xmasDirection.colShift] == XmasLetter.S

                            ) {
                                xmasCounter++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println(xmasCounter);

        //part 2
        int x_masCounter = 0;
        for(int i = 0; i < xmasLettersMap.length; i++) {
            for (int j = 0; j < xmasLettersMap[i].length; j++) {
                if(xmasLettersMap[i][j] == XmasLetter.A) {
                    List<X_masDirection> xmasDirections = EnumSet.allOf(X_masDirection.class).stream().toList();
                    for (X_masDirection xmasDirection : xmasDirections) {
                        if(i - 1 >= 0 && i + 1 < xmasLettersMap[i].length && j - 1 >= 0 && j + 1 < xmasLettersMap.length) {
                            if(xmasLettersMap[i - 1][j - 1] == xmasDirection.leftUpLetter
                            && xmasLettersMap[i - 1][j + 1] == xmasDirection.rightUpLetter
                            && xmasLettersMap[i + 1][j + 1] == xmasDirection.rightBottomLetter
                            && xmasLettersMap[i + 1][j - 1] == xmasDirection.leftBottomLetter) {
                                x_masCounter++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println(x_masCounter);
    }

    enum XmasLetter {
        X, M, A, S;

        public static XmasLetter of(String letter) {
            return EnumSet.allOf(XmasLetter.class).stream()
                    .filter(xmasLetter -> xmasLetter.toString().equals(letter))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid letter: " + letter));
        }
    }

    @Getter
    @AllArgsConstructor
    enum XmasDirection {

        TOP_TO_BOTTOM(1, 0),
        BOTTOM_TO_TOP(-1, 0),
        RIGHT_TO_LEFT(0, -1),
        LEFT_TO_RIGHT(0, 1),
        DIAGONALLY_UP_RIGHT(-1, 1),
        DIAGONALLY_UP_LEFT(-1, -1),
        DIAGONALLY_DOWN_RIGHT(1, 1),
        DIAGONALLY_DOWN_LEFT(1, -1);

        private final int rowShift;
        private final int colShift;

    }

    @Getter
    @AllArgsConstructor
    enum X_masDirection {

        BOTH_TOP_DOWN(XmasLetter.M, XmasLetter.M, XmasLetter.S, XmasLetter.S),
        BOTH_DOWN_TOP(XmasLetter.S, XmasLetter.S, XmasLetter.M, XmasLetter.M),
        LEFT_TOP_DOWN(XmasLetter.M, XmasLetter.S, XmasLetter.M, XmasLetter.S),
        RIGHT_TOP_DOWN(XmasLetter.S, XmasLetter.M, XmasLetter.S, XmasLetter.M);

        private final XmasLetter leftUpLetter;
        private final XmasLetter rightUpLetter;
        private final XmasLetter leftBottomLetter;
        private final XmasLetter rightBottomLetter;

    }

}
