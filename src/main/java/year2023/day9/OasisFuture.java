package year2023.day9;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

public class OasisFuture {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day9/input.txt"));
        List<Sequence> sequences = lines.stream().map(Sequence::new).toList();
        System.out.println(sequences.stream().mapToInt(Sequence::getExtrapolatedValue).sum());
        System.out.println(sequences.stream().mapToInt(Sequence::getPreviousExtrapolatedValue).sum());
    }

    @Data
    static class Sequence {
        List<Integer> values;
        Stack<List<Integer>> stackOfDifferences;

        int extrapolatedValue;
        int previousExtrapolatedValue;

        public Sequence(String line) {
            this.values = Stream.of(line.split(" ")).map(Integer::parseInt).toList();
            this.stackOfDifferences = stackOfDifferences();
            this.extrapolatedValue = extrapolate();
            this.previousExtrapolatedValue = extrapolatePrevious();
        }

        private int extrapolatePrevious() {
            stackOfDifferences.pop();
            Integer substract = 0;
            while(!stackOfDifferences.isEmpty()) {
                List<Integer> popped = stackOfDifferences.pop();
                substract = popped.get(0) - substract;
            }
            return substract;
        }

        public Stack<List<Integer>> stackOfDifferences() {
            Stack<List<Integer>> stackOfDifferences = new Stack<>();
            stackOfDifferences.add(values);
            List<Integer> differences = new ArrayList<>(values);
            while(!containsOnlyZeros(differences)) {
                List<Integer> newDifferences = new ArrayList<>();
                for (int i = 0; i < differences.size() - 1; i++) {
                    newDifferences.add(differences.get(i+1) - differences.get(i));
                }
                stackOfDifferences.add(newDifferences);
                differences = newDifferences;
            }
            return stackOfDifferences;
        }

        Integer extrapolate() {
            Stack<List<Integer>> stackOfDifferences = new Stack<>();
            stackOfDifferences.addAll(this.stackOfDifferences);
            stackOfDifferences.pop();
            Integer add = 0;
            while (!stackOfDifferences.isEmpty()) {
                List<Integer> popped = stackOfDifferences.pop();
                add = popped.get(popped.size() - 1) + add;
            }
            return add;
        }

        private boolean containsOnlyZeros(List<Integer> list) {
            return !list.isEmpty() && list.stream().noneMatch(val -> val != 0);
        }
    }

}
