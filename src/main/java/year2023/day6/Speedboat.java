package year2023.day6;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Speedboat {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day6/input.txt"));
        List<Integer> times = parseTime(lines);
        List<BigInteger> distances = parseDistance(lines);
        List<Race> races = new ArrayList<>();
        int winningWaysMultiplied = 1;
        for(int i = 0; i < times.size(); i++) {
            Race race = new Race(times.get(i), distances.get(i));
            races.add(race);
            winningWaysMultiplied *= race.winningChargeTimes.size();
        }
        System.out.println(winningWaysMultiplied);

    }

    private static List<Integer> parseTime(List<String> lines) {
        return Stream.of(lines.get(0).split(":")[1].split(" "))
                .filter(str -> !str.isEmpty() && !str.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    private static List<BigInteger> parseDistance(List<String> lines) {
        return Stream.of(lines.get(1).split(":")[1].split(" "))
                .filter(str -> !str.isEmpty() && !str.isBlank())
                .map(str -> BigInteger.valueOf(Long.parseLong(str)))
                .toList();
    }

    static class Race {
        Integer maxTime;
        BigInteger currentRecord;
        List<BigInteger> winningChargeTimes;

        public Race(Integer maxTime, BigInteger currentRecord) {
            this.maxTime = maxTime;
            this.currentRecord = currentRecord;
            this.winningChargeTimes = winningChargeTimes();
        }

        List<BigInteger> winningChargeTimes() {
            List<BigInteger> result = new ArrayList<>();
            for(BigInteger speed = BigInteger.ZERO;
                speed.compareTo(BigInteger.valueOf(maxTime)) <= 0;
                speed = speed.add(BigInteger.ONE)) {
                BigInteger distance = (BigInteger.valueOf(maxTime).subtract(speed)).multiply(speed);
                if(distance.compareTo(currentRecord) > 0) {
                    result.add(distance);
                }
            }
            return result;
        }
    }
}
