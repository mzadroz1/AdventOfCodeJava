package year2023.day20;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PulsePropagation {

    public static PulsesQueue pulses = new PulsesQueue();

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day20/input.txt"));

        Map<String, Module> modules = new HashMap<>();
        for (String line : lines) {
            String[] split = line.split(" -> ");
            if (split[0].charAt(0) == '%') {
                modules.put(split[0].substring(1), new FlipFlop(split[0].substring(1), List.of(split[1].split(", "))));
            } else if (split[0].charAt(0) == '&') {
                modules.put(split[0].substring(1), new Conjunction(split[0].substring(1), List.of(split[1].split(", "))));
            } else if (split[0].equals("broadcaster")) {
                modules.put(split[0], new Broadcaster(split[0], List.of(split[1].split(", "))));
            } else {
                throw new IllegalArgumentException("Not recognized module: " + line);
            }
        }
        modules.values().stream().filter(module -> module instanceof Conjunction)
                .map(module -> (Conjunction) module)
                .forEach(conjunction -> conjunction.setupInputModules(
                        modules.entrySet().stream()
                                .filter(module -> module.getValue().getDestinationModules().stream().anyMatch(name -> name.equals(conjunction.getName())))
                                .map(Map.Entry::getKey)
                                .toList()
                ));

//        for (int i = 0; i < 1000; i++) {
//            System.out.println();
//            pulses.add(new Pulse(PulseType.LOW, "button", "broadcaster"));
//            while (!pulses.isEmpty()) {
//                Pulse pulse = pulses.pop();
//                Module module = modules.get(pulse.destinationModuleName);
//                if (module != null) {
//                    module.handlePulse(pulse);
//                }
//            }
//        }

        int buttonPushesCount = 0;
        while (true) {
            buttonPushesCount++;
            pulses.add(new Pulse(PulseType.LOW, "button", "broadcaster"));
            while (!pulses.isEmpty()) {
                Pulse pulse = pulses.pop();
                Module module = modules.get(pulse.destinationModuleName);
//                if(pulse.destinationModuleName.equals("rx") && pulse.pulseType == PulseType.LOW) {
//                    System.out.println(buttonPushesCount);
//                    return;
//                }
                if(pulse.destinationModuleName.equals("ft") && pulse.pulseType == PulseType.HIGH) {
                    System.out.println("source: " + pulse.sourceModuleName + " button: " + buttonPushesCount);
//                    return;
                }
                if (module != null) {
                    module.handlePulse(pulse);
                }
            }
        }

    }

    @Getter
    static class PulsesQueue {
        private final Queue<Pulse> pulses = new ArrayDeque<>();
        private int numberOfHighPulses = 0;
        private int numberOfLowPulses = 0;

        void add(Pulse pulse) {
            pulses.add(pulse);
//            System.out.println(pulse.sourceModuleName + " -" + pulse.pulseType + "-> " + pulse.destinationModuleName);
            if (pulse.pulseType == PulseType.HIGH) {
                numberOfHighPulses++;
            } else {
                numberOfLowPulses++;
            }
        }

        Pulse pop() {
            return pulses.poll();
        }

        boolean isEmpty() {
            return pulses.isEmpty();
        }

        int getResult() {
            return numberOfHighPulses * numberOfLowPulses;
        }
    }

    @AllArgsConstructor
    static class Pulse {
        PulseType pulseType;
        String sourceModuleName;
        String destinationModuleName;
    }

    enum PulseType {
        HIGH,
        LOW
    }

    interface Module {

        String getName();

        List<String> getDestinationModules();

        void handlePulse(Pulse pulse);

    }

    static class FlipFlop implements Module {

        String name;
        FlipFlopState state = FlipFlopState.OFF;
        List<String> destinationModulesNames;

        public FlipFlop(String name, List<String> destinationModulesNames) {
            this.name = name;
            this.destinationModulesNames = destinationModulesNames;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getDestinationModules() {
            return destinationModulesNames;
        }

        @Override
        public void handlePulse(Pulse pulse) {
            if (pulse.pulseType == PulseType.LOW) {
                handleLowPulse();
            }
        }

        private void handleLowPulse() {
            if (state == FlipFlopState.ON) {
                state = FlipFlopState.OFF;
                destinationModulesNames.forEach(module -> pulses.add(new Pulse(PulseType.LOW, name, module)));
            } else {
                state = FlipFlopState.ON;
                destinationModulesNames.forEach(module -> pulses.add(new Pulse(PulseType.HIGH, name, module)));
            }
        }
    }

    enum FlipFlopState {
        ON,
        OFF
    }

    static class Conjunction implements Module {

        String name;
        List<String> destinationModulesNames;
        List<String> inputModulesNames;

        List<ConjunctionInputState> recentSignalsReceivedMemory = new ArrayList<>();

        public Conjunction(String name, List<String> destinationModulesNames) {
            this.name = name;
            this.destinationModulesNames = destinationModulesNames;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getDestinationModules() {
            return destinationModulesNames;
        }

        @Override
        public void handlePulse(Pulse pulse) {
            recentSignalsReceivedMemory.stream().filter(input -> input.inputModuleName.equals(pulse.sourceModuleName))
                    .findFirst()
                    .ifPresent(inputState -> inputState.rememberedState = pulse.pulseType);
            if (recentSignalsReceivedMemory.stream().map(ConjunctionInputState::getRememberedState).allMatch(pulseType -> pulseType == PulseType.HIGH)) {
                destinationModulesNames.forEach(module -> pulses.add(new Pulse(PulseType.LOW, name, module)));
            } else {
                destinationModulesNames.forEach(module -> pulses.add(new Pulse(PulseType.HIGH, name, module)));
            }
        }

        public void setupInputModules(List<String> inputModulesNames) {
            this.inputModulesNames = inputModulesNames;
            inputModulesNames.forEach(inputModule -> recentSignalsReceivedMemory.add(new ConjunctionInputState(inputModule, PulseType.LOW)));
        }

    }

    @Getter
    @AllArgsConstructor
    static class ConjunctionInputState {
        String inputModuleName;
        PulseType rememberedState;
    }

    @AllArgsConstructor
    static class Broadcaster implements Module {

        String name;
        List<String> destinationModulesNames;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getDestinationModules() {
            return destinationModulesNames;
        }

        @Override
        public void handlePulse(Pulse pulse) {
            destinationModulesNames.forEach(module -> pulses.add(new Pulse(pulse.pulseType, name, module)));
        }
    }

}
