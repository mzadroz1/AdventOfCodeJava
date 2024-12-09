package year2023.day15;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LavaHash {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day15/input.txt"));
//        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day15/testInput.txt"));

        //part 1
        int sum = Arrays.stream(lines.get(0).split(","))
                .map(InitializationStep::new)
                .mapToInt(InitializationStep::getHash)
                .sum();

//        System.out.println(sum);
        //

        List<InitializationStep> initializationSteps = Arrays.stream(lines.get(0).split(","))
                .map(InitializationStep::new).toList();

        Map<Integer, List<InitializationStep>> hashMap = new HashMap<>();

        for (InitializationStep step : initializationSteps) {
            List<InitializationStep> box = hashMap.get(step.getHash());
            if (step.operationSymbol == OperationSymbol.ADD) {
                if (box == null) {
                    hashMap.put(step.getHash(), new ArrayList<>());
                    box = hashMap.get(step.getHash());
                }
                if (box.stream().map(InitializationStep::getLabel).anyMatch(label -> label.equals(step.label))) {
                    box.stream().filter(inBox -> inBox.label.equals(step.label)).forEach(item -> item.focalLength = step.focalLength);
                } else {
                    box.add(step);
                }
            }
            if (step.operationSymbol == OperationSymbol.REMOVE) {
                if (box != null
                       // && box.stream().map(InitializationStep::getLabel).anyMatch(label -> label.equals(step.label))
                ) {
                    for(int i = 0; i < box.size(); i++) {
                        if(box.get(i).getLabel().equals(step.label)) {
                            box.remove(i);
                        }
                    }
                }
            }
//            System.out.println("After " + step.sequence);
//            for(Integer boxNo : hashMap.keySet()) {
//                System.out.println("Box " + boxNo + ": " + hashMap.get(boxNo));
//            }
        }

        int totalFocusingPower = 0;
        for(Integer boxNo : hashMap.keySet()) {
            List<InitializationStep> lensSlots = hashMap.get(boxNo);
            for (int i = 0; i < lensSlots.size(); i++) {
                InitializationStep step = lensSlots.get(i);
                int focusingPower = boxNo + 1;
                focusingPower *= i + 1;
                focusingPower *= step.focalLength;
                totalFocusingPower += focusingPower;
            }
        }
        System.out.println(totalFocusingPower);

    }


    @Getter
    static class InitializationStep {
        String sequence;
        int hash;

        String label;
        OperationSymbol operationSymbol;

        int focalLength;

        public InitializationStep(String sequence) {
            this.sequence = sequence;
            if(sequence.contains("=")) {
                String[] split = sequence.split("=");
                this.label = split[0];
                this.operationSymbol = OperationSymbol.ADD;
                this.focalLength = Integer.parseInt(split[1]);
            }
            if(sequence.contains("-")) {
                String[] split = sequence.split("-");
                this.label = split[0];
                this.operationSymbol = OperationSymbol.REMOVE;
            }
            this.hash = hash(label);
        }

        int hash(String s) {
            int currentValue = 0;
            for(int i =0; i<s.length(); i++) {
                currentValue += s.charAt(i);
                currentValue *= 17;
                currentValue %= 256;
            }
            return currentValue;
        }

        @Override
        public String toString() {
            return label + " " + focalLength;
        }
    }

    enum OperationSymbol {
        ADD('='),
        REMOVE('-');

        private char value;

        OperationSymbol(char value) {
            this.value = value;
        }


    }
}
