package year2024.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocationsLists {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day1/input.txt"));
        List<Integer> leftList = new ArrayList<>();
        List<Integer> rightList = new ArrayList<>();
        for(String line : lines) {
            String[] parts = line.split(" {3}");
            leftList.add(Integer.parseInt(parts[0]));
            rightList.add(Integer.parseInt(parts[1]));
        }
        rightList.sort(Comparator.naturalOrder());
        leftList.sort(Comparator.naturalOrder());
        int totalDistance = 0;
        for(int i = 0; i < leftList.size(); i++) {
            totalDistance += Math.abs(leftList.get(i) - rightList.get(i));
        }
        System.out.println(totalDistance);

        Map<Integer, Long> map = rightList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        int similarityScore = 0;
        for(Integer elementInLeftList: leftList) {
            Integer elementCountInRightList = Optional.ofNullable(map.get(elementInLeftList)).orElse(0L).intValue();
            similarityScore += elementInLeftList * elementCountInRightList;
        }
        System.out.println(similarityScore);
    }

}
