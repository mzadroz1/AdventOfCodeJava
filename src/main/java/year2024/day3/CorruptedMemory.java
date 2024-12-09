package year2024.day3;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CorruptedMemory {
    public static final String MUL_COMMAND_REGEX = "mul\\(\\d+,\\d+\\)";

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day3/input.txt"));
        StringBuilder wholeMemory = new StringBuilder();
        for (String line : lines) {
            wholeMemory.append(line);
        }
        //part 1
        Integer multiplicationsSum = matchCommands(MUL_COMMAND_REGEX, wholeMemory)
                .map(MatchResult::group)
                .map(MultiplicationCommand::new)
                .map(MultiplicationCommand::getResult)
                .reduce(Integer::sum)
                .orElseThrow(() -> new RuntimeException("No match found"));
        System.out.println(multiplicationsSum);

        //part 2
        List<MultiplicationCommandWithPlacement> commands = matchCommands(MUL_COMMAND_REGEX, wholeMemory)
                .map(matchResult -> new MultiplicationCommandWithPlacement(matchResult.group(), matchResult.start()))
                .toList();
        List<Instruction> instructions = new ArrayList<>(
                matchInstructions("do\\(\\)", wholeMemory)
        );
        instructions.addAll(
                matchInstructions("don't\\(\\)", wholeMemory)
        );

        var commandsMap = commands.stream()
                .collect(Collectors.toMap(
                        MultiplicationCommandWithPlacement::getStartIndex,
                        Function.identity()
                ));

        var instructionsMap = instructions.stream()
                .collect(Collectors.toMap(
                        Instruction::getStartIndex,
                        Function.identity()
                ));

        int highestIndex = Math.max(
                commandsMap.keySet().stream().max(Integer::compareTo).orElse(0),
                instructionsMap.keySet().stream().max(Integer::compareTo).orElse(0)
        );
        boolean isMultiplicationEnabled = true;
        Integer sum = 0;
        for(int i = 0; i <= highestIndex; i++) {
            var command = commandsMap.get(i);
            if(command != null && isMultiplicationEnabled) {
                sum += command.getResult();
            }
            var instruction = instructionsMap.get(i);
            if(instruction != null) {
                isMultiplicationEnabled = instruction.enablesMultiplication;
            }
        }
        System.out.println(sum);
    }

    private static Stream<MatchResult> matchCommands(String regex, StringBuilder wholeMemory) {
        return Pattern.compile(regex)
                .matcher(wholeMemory.toString())
                .results();
    }

    private static List<Instruction> matchInstructions(String regex, StringBuilder wholeMemory) {
        return matchCommands(regex, wholeMemory)
                .map(matchResult -> new Instruction(matchResult.start(), matchResult.group()))
                .toList();
    }

    @NoArgsConstructor
    static class MultiplicationCommand {
        int leftOperand;
        int rightOperand;

        MultiplicationCommand(String command) {
            this.leftOperand = Integer.parseInt(command.split(",")[0].split("\\(")[1]);
            this.rightOperand = Integer.parseInt(command.split(",")[1].split("\\)")[0]);
        }

        public Integer getResult() {
            return leftOperand * rightOperand;
        }
    }

    @Getter
    static class MultiplicationCommandWithPlacement extends MultiplicationCommand {
        int startIndex;

        public MultiplicationCommandWithPlacement(String command, int startIndex) {
            super(command);
            this.startIndex = startIndex;
        }
    }

    @Getter
    static class Instruction {
        int startIndex;
        boolean enablesMultiplication;

        public Instruction(int startIndex, String command) {
            this.startIndex = startIndex;
            this.enablesMultiplication = command.equals("do()");
        }
    }
}
