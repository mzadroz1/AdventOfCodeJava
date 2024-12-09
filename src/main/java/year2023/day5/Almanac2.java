package year2023.day5;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Almanac2 {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day5/input.txt"));

        List<SeedsRange> seedsRanges = parseSeeds(lines);

        List<GardenMap> gardenMaps = new ArrayList<>();

        for (int i = 2; i < lines.size(); i++) {
            List<String> mapInput = new ArrayList<>();
            do {
                mapInput.add(lines.get(i));
                i++;
            } while (!Objects.equals(lines.get(i), ""));
            gardenMaps.add(new GardenMap(mapInput));
        }
        int counter = 0;
        BigInteger lowestLocationId = BigInteger.valueOf(1000000000000000L);
        for (SeedsRange seedsRange : seedsRanges) {
            for(var i = seedsRange.rangeStart; i.compareTo(seedsRange.rangeStart.add(seedsRange.rangeLength)) < 0; i = i.add(BigInteger.ONE)) {
                BigInteger sourceId = i;
                for (GardenMap gardenMap : gardenMaps) {
                    sourceId = gardenMap.evaluateMapping(sourceId);
                }
                if (sourceId.compareTo(lowestLocationId) < 0) {
                    lowestLocationId = sourceId;
                }
                counter++;
                if(counter == 1000000) {
                    System.out.println(seedsRange.rangeStart + ": million seeds evaluated, " + seedsRange.rangeStart.add(seedsRange.rangeLength).subtract(i) + " to go");
                    counter = 0;
                }
            }
        }
        //too high: 28580590
        System.out.println(lowestLocationId);
    }

    private static List<SeedsRange> parseSeeds(List<String> lines) {
        String[] numbers = lines.get(0).split(": ")[1].split(" ");
        List<SeedsRange> seedsRanges = new ArrayList<>();
        for (int i = 0; i < numbers.length; i += 2) {
            BigInteger startRange = BigInteger.valueOf(Long.parseLong(numbers[i]));
            BigInteger rangeLength = BigInteger.valueOf(Long.parseLong(numbers[i + 1]));
            seedsRanges.add(new SeedsRange(startRange, rangeLength));
        }
        return seedsRanges;
    }

    @AllArgsConstructor
    static class SeedsRange {

        BigInteger rangeStart;
        BigInteger rangeLength;
    }

    static class GardenMap {
        Category sourceCategory;
        Category destinationCategory;

        List<MappingRule> mappingRules = new ArrayList<>();

        public GardenMap(List<String> input) {
            String[] categoriesNames = input.get(0).split("-to-");
            sourceCategory = Category.parseCategory(categoriesNames[0]);
            destinationCategory = Category.parseCategory(categoriesNames[1].split(" ")[0]);
            for (int i = 1; i < input.size(); i++) {
                List<BigInteger> parameters = Stream.of(input.get(i).split(" "))
                        .map(parameter -> BigInteger.valueOf(Long.parseLong(parameter)))
                        .toList();
                mappingRules.add(new MappingRule(parameters));
            }
        }

        public BigInteger evaluateMapping(BigInteger sourceId) {
            for (int i = 0; i < mappingRules.size(); i++) {
                MappingRule mappingRule = mappingRules.get(i);
                if (sourceId.compareTo(mappingRule.sourceRangeStart) >= 0
                        && sourceId.compareTo(mappingRule.sourceRangeStart.add(mappingRule.rangeLength)) < 0) {
                    return mappingRule.destinationRangeStart.add(sourceId.subtract(mappingRule.sourceRangeStart));
                }
            }
            return sourceId;
        }

        @AllArgsConstructor
        static class MappingRule {
            BigInteger destinationRangeStart;
            BigInteger sourceRangeStart;
            BigInteger rangeLength;

            public MappingRule(List<BigInteger> parameters) {
                this(parameters.get(0), parameters.get(1), parameters.get(2));
            }
        }
    }

    enum Category {
        SEED("seed"),
        SOIL("soil"),
        FERTILIZER("fertilizer"),
        WATER("water"),
        LIGHT("light"),
        TEMPERATURE("temperature"),
        HUMIDITY("humidity"),
        LOCATION("location");

        private String categoryName;

        Category(String categoryName) {
            this.categoryName = categoryName;
        }

        static Category parseCategory(String name) {
            return EnumSet.allOf(Category.class).stream()
                    .filter(category -> category.categoryName.equals(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal category name=" + name));
        }
    }
}
