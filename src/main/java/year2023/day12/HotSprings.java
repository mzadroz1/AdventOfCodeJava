package year2023.day12;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HotSprings {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day12/input.txt"));

        List<HotSpringsRow> hotSpringsRows = lines.stream().map(HotSpringsRow::new).toList();

        System.out.println(hotSpringsRows.stream().mapToInt(HotSpringsRow::allPossibleArrangementsCount).sum());

    }

    static class HotSpringsRow {

        String symbolsLine;
        List<HotSpringSymbol> symbols;
        List<Integer> damagedSpringsGroups;

        public HotSpringsRow(String line) {
            this.symbolsLine = line.split(" ")[0];
            this.symbols = Arrays.stream(line.split(" ")[0].split("")).map(HotSpringSymbol::parse).toList();
            this.damagedSpringsGroups = Arrays.stream(line.split(" ")[1].split(",")).map(Integer::parseInt).toList();
        }

        int allPossibleArrangementsCount() {
            int possibleArrangementsCount = 0;
            long unknownsCount = symbols.stream().filter(hotSpringSymbol -> hotSpringSymbol == HotSpringSymbol.UNKNOWN).count();
            long optionsCount = (long) Math.pow(2, unknownsCount);
            for(int i = 0; i < optionsCount; i++) {
                String option = replaceUnknowns(i, unknownsCount);
                if(isValidOption(option)) {
                    possibleArrangementsCount++;
//                    System.out.println(option);
                }
            }
            return possibleArrangementsCount;
        }

        private boolean isValidOption(String option) {
            List<String> damagedGroups = Arrays.stream(option.split("\\.")).filter(s -> s.contains("#")).toList();
            if(damagedGroups.size() != damagedSpringsGroups.size()) {
                return false;
            }
            boolean valid = true;
            for (int i = 0; i < damagedGroups.size(); i++) {
                String group = damagedGroups.get(i);
                if (group.length() != damagedSpringsGroups.get(i)) {
                    valid = false;
                    break;
                }
            }
            return valid;
        }

        String replaceUnknowns(int optionNum, long unknownsCount) {
            String binaryString = Integer.toBinaryString(optionNum);
            binaryString = String.format("%" + unknownsCount + "s", binaryString).replace(' ', '0');
            binaryString = binaryString.replaceAll("0", ".");
            binaryString = binaryString.replaceAll("1", "#");
            String replaced = this.symbolsLine;
            for(int i = 0; i < binaryString.length(); i++) {
                replaced = replaced.replaceFirst("\\?", String.valueOf(binaryString.charAt(i)));
            }
            return replaced;
        }
    }


    enum HotSpringSymbol {
        OPERATIONAL('.'),
        DAMAGED('#'),
        UNKNOWN('?');

        private char symbol;

        HotSpringSymbol(char symbol) {
            this.symbol = symbol;
        }

        static HotSpringSymbol parse(String s) {
            return EnumSet.allOf(HotSpringSymbol.class).stream()
                    .filter(p -> p.symbol == s.charAt(0))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal hot spring symbol=" + s));
        }
    }
}
