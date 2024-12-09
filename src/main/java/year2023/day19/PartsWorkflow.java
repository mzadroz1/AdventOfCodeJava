package year2023.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PartsWorkflow {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day19/input.txt"));

        Map<String, Workflow> workflowMap = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        boolean workflowsParsed = false;
        for(String line : lines) {
            if(line.equals("")) {
                workflowsParsed = true;
                continue;
            }
            if(workflowsParsed) {
                parts.add(new Part(line));
            } else {
                Workflow workflow = new Workflow(line);
                workflowMap.put(workflow.name, workflow);
            }
        }

        List<Part> acceptedParts = new ArrayList<>();
        for(Part part : parts) {
            Workflow workflow = workflowMap.get("in");

            EvaluationResult evaluationResult = workflow.processPart(part);
            while(evaluationResult.decision == null) {
                workflow = workflowMap.get(evaluationResult.nextWorkflowName);
                evaluationResult = workflow.processPart(part);
            }
            if(evaluationResult.decision == Decision.ACCEPT) {
                acceptedParts.add(part);
            }
        }

        int sum = acceptedParts.stream().mapToInt(part -> part.partProperties.stream().mapToInt(x -> x).sum()).sum();
        System.out.println(sum);

    }

    static class Workflow {
        String name;
        List<Rule> rules = new ArrayList<>();

        public Workflow(String line) {
            String[] parts = line.split("\\{");
            this.name = parts[0];
            String[] rulesStrings = parts[1].split("}")[0].split(",");
            for(String ruleString : rulesStrings) {
                this.rules.add(new Rule(ruleString));
            }

        }

        public EvaluationResult processPart(Part part) {
            for(Rule rule : rules) {
                EvaluationResult ruleEvaluationResult = rule.evaluateRule(part);
                if(ruleEvaluationResult.conditionMet) {
                    return ruleEvaluationResult;
                }
            }
            throw new IllegalStateException();
        }

    }

    static class Rule {
        PartCategory partCategory;
        char comparisonSymbol;
        int propertyBound;
        String nextWorkflowName;

        Decision decision;

        public EvaluationResult evaluateRule(Part part) {
            if(!hasCondition()) {
                if(!makesFinalDecision()) {
                    return new EvaluationResult(nextWorkflowName);
                } else {
                    return new EvaluationResult(decision);
                }
            } else {
                int property = part.partProperties.get(partCategory.propertyIndex);
                boolean conditionResult;
                if(comparisonSymbol == '<') {
                    conditionResult = property < propertyBound;
                } else {
                    conditionResult = property > propertyBound;
                }
                if(conditionResult) {
                    if(!makesFinalDecision()) {
                        return new EvaluationResult(nextWorkflowName);
                    } else {
                        return new EvaluationResult(decision);
                    }
                } else {
                    return new EvaluationResult();
                }
            }
        }

        public Rule(String ruleString) {
            String[] split = ruleString.split(":");
            if(split.length > 1) {
                this.partCategory = PartCategory.parse(split[0].substring(0,1));
                this.comparisonSymbol = split[0].charAt(1);
                this.propertyBound = Integer.parseInt(split[0].substring(2));
                Decision ruleResult = Decision.parse(split[1]);
                if(ruleResult == null) {
                    this.nextWorkflowName = split[1];
                } else {
                    this.decision = ruleResult;
                }
            } else {
                Decision ruleResult = Decision.parse(split[0]);
                if(ruleResult == null) {
                    this.nextWorkflowName = split[0];
                } else {
                    this.decision = ruleResult;
                }
            }
        }

        public boolean hasCondition() {
            return partCategory != null;
        }

        public boolean makesFinalDecision() {
            return decision != null;
        }
    }

    static class EvaluationResult {

        private boolean conditionMet;

        private String nextWorkflowName;

        private Decision decision;

        public EvaluationResult() {
            this.conditionMet = false;
        }

        public EvaluationResult(String nextWorkflowName) {
            this.conditionMet = true;
            this.nextWorkflowName = nextWorkflowName;
        }

        public EvaluationResult(Decision decision) {
            this.conditionMet = true;
            this.decision = decision;
        }

        public boolean wasEvaluationTrue() {
            return conditionMet;
        }

        public boolean isFinalDecisionMade() {
            return decision != null;
        }

        public Decision getDecision() {
            if(!isFinalDecisionMade()) {
                throw new IllegalArgumentException("Decision not made. Get next workflow name instead.");
            }
            return decision;
        }

        public String getNextWorkflowName() {
            return nextWorkflowName;
        }
    }

    enum Decision {
        ACCEPT('A'),
        REJECT('R');

        final char symbol;

        Decision(char symbol) {
            this.symbol = symbol;
        }

        static Decision parse(String s) {
            return EnumSet.allOf(Decision.class).stream()
                    .filter(p -> p.symbol == s.charAt(0))
                    .findFirst()
                    .orElse(null);
        }
    }

    static class Part {
        int x;
        int m;
        int a;
        int s;

        List<Integer> partProperties = new ArrayList<>();

        public Part(String line) {
            String[] properties = line.split("\\{")[1].split("}")[0].split(",");
            this.x = Integer.parseInt(properties[0].split("=")[1]);
            this.m = Integer.parseInt(properties[1].split("=")[1]);
            this.a = Integer.parseInt(properties[2].split("=")[1]);
            this.s = Integer.parseInt(properties[3].split("=")[1]);
            partProperties.add(x);
            partProperties.add(m);
            partProperties.add(a);
            partProperties.add(s);
        }
    }

    enum PartCategory {
        X("x", 0),
        M("m", 1),
        A("a", 2),
        S("s", 3);

        private final String symbol;
        private final int propertyIndex;

        PartCategory(String symbol, int propertyIndex) {
            this.symbol = symbol;
            this.propertyIndex = propertyIndex;
        }

        static PartCategory parse(String s) {
            return EnumSet.allOf(PartCategory.class).stream()
                    .filter(p -> p.symbol.equals(s))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal symbol=" + s));
        }
    }
}
