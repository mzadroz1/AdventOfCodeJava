package year2023.day12;


import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class HotSpringsUnfolded {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day12/testInput.txt"));

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
            List<HotSpringSymbol> unfoldedSegment = new ArrayList<>(symbols);
            unfoldedSegment.add(HotSpringSymbol.UNKNOWN);
            int count = (int) (possibleArrangementsInSegment(symbols) * Math.pow(possibleArrangementsInSegment(unfoldedSegment), 4));
            System.out.println(count);
            unfoldedSegment = new ArrayList<>(List.of(HotSpringSymbol.UNKNOWN));
            unfoldedSegment.addAll(symbols);
            count = (int) (possibleArrangementsInSegment(symbols) * Math.pow(possibleArrangementsInSegment(unfoldedSegment), 4));
            System.out.println(count);
            System.out.println();
            return count;
        }
//        int allPossibleArrangementsCount() {
//            List<HotSpringSymbol> unfoldedSegment = new ArrayList<>(symbols);
//            unfoldedSegment.add(HotSpringSymbol.UNKNOWN);
//            unfoldedSegment.addAll(symbols);
//            List<Integer> damagedSpringsGroupsUnfolded = new ArrayList<>(damagedSpringsGroups);
//            damagedSpringsGroupsUnfolded.addAll(damagedSpringsGroups);
//            damagedSpringsGroups = damagedSpringsGroupsUnfolded;
////            int count = (int) (possibleArrangementsInSegment(symbols) * Math.pow(possibleArrangementsInSegment(unfoldedSegment), 4));
//            int count = possibleArrangementsInSegment(unfoldedSegment);
//            System.out.println(count);
//            return count;
//        }

        private int possibleArrangementsInSegment(List<HotSpringSymbol> symbols) {
            int possibleArrangementsCount = 0;
            long unknownsCount = symbols.stream().filter(hotSpringSymbol -> hotSpringSymbol == HotSpringSymbol.UNKNOWN).count();
            long optionsCount = (long) Math.pow(2, unknownsCount);
            for(int i = 0; i < optionsCount; i++) {
                String option = replaceUnknowns(i, unknownsCount, symbols);
                if(isValidOption(option)) {
                    possibleArrangementsCount++;
                }
            }
//            System.out.println(possibleArrangementsCount);
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

        String replaceUnknowns(int optionNum, long unknownsCount, List<HotSpringSymbol> symbols) {
            String binaryString = Integer.toBinaryString(optionNum);
            binaryString = String.format("%" + unknownsCount + "s", binaryString).replace(' ', '0');
            binaryString = binaryString.replaceAll("0", ".");
            binaryString = binaryString.replaceAll("1", "#");
            String replaced = HotSpringSymbol.symbolsLine(symbols);
            for(int i = 0; i < binaryString.length(); i++) {
                replaced = replaced.replaceFirst("\\?", String.valueOf(binaryString.charAt(i)));
            }
            return replaced;
        }
    }

    @Getter
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

        static String symbolsLine(List<HotSpringSymbol> symbols) {
            StringBuilder sb = new StringBuilder();
            for (HotSpringSymbol hotSpringSymbol : symbols) {
                sb.append(hotSpringSymbol.symbol);
            }
            return sb.toString();
        }
    }
}
