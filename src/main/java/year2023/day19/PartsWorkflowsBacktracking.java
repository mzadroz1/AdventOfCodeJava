package year2023.day19;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PartsWorkflowsBacktracking {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day19/input.txt"));

        List<Workflow> workflows = new ArrayList<>();
        for (String line : lines) {
            if (line.equals("")) {
                break;
            }
            Workflow workflow = new Workflow(line);
            workflows.add(workflow);
        }

        BigInteger sum = BigInteger.ZERO;

        for(Workflow workflow : workflows) {
            List<Rule> rules = workflow.rules;
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                if(rule.decision == Decision.ACCEPT) {
                    System.out.println("A found: " + workflow.name + " rule " + i);
                    PartPropertiesRequirements reqs = new PartPropertiesRequirements();
                    reqs.requirements.addAll(workflow.requirementsToReachDecision(i));
                    Workflow previousWorkflow = workflow;
                    while(!previousWorkflow.name.equals("in")) {
                        Workflow finalPreviousWorkflow = previousWorkflow;
                        previousWorkflow = workflows.stream()
                                .filter(wf -> wf.nextWorkflows().contains(finalPreviousWorkflow.name))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No previous workflow found for " + finalPreviousWorkflow.name));
                        System.out.println("previous Workflow:" + previousWorkflow.name);
                        reqs.requirements.addAll(previousWorkflow.requirementsToProceedToWorkflow(finalPreviousWorkflow.name));
                    }
                    reqs.requirements.forEach(System.out::println);
                    System.out.println(reqs.boundaries());
                    System.out.println(reqs.boundaries().numberOfDistinctCombinations());
                    sum = sum.add(reqs.boundaries().numberOfDistinctCombinations());

//                    combinations.addAll(reqs.numberOfDistinctCombinations().distinctCombinations());
                }
            }
        }

        System.out.println(sum);

    }

    static class PartPropertiesRequirements {
        List<Condition> requirements = new ArrayList<>();

        PartsBoundaries boundaries() {

            List<Integer> minimumValues = new ArrayList<>(List.of(1, 1, 1, 1));
            List<Integer> maximumValues = new ArrayList<>(List.of(4000, 4000, 4000, 4000));

            for(Condition req : requirements) {
                PartCategory partCategory = req.partCategory;
                Integer minimumPart = minimumValues.get(partCategory.propertyIndex);
                Integer maximumPart = maximumValues.get(partCategory.propertyIndex);
                if(req.comparisonSymbol.equals(">")) {
                    if(req.propertyBound > minimumPart) {
                        minimumValues.set(partCategory.propertyIndex, req.propertyBound + 1);
                    }
                }
                if(req.comparisonSymbol.equals(">=")) {
                    if(req.propertyBound > minimumPart) {
                        minimumValues.set(partCategory.propertyIndex, req.propertyBound);
                    }
                }
                if(req.comparisonSymbol.equals("<")) {
                    if(req.propertyBound < maximumPart) {
                        maximumValues.set(partCategory.propertyIndex, req.propertyBound - 1);
                    }
                }
                if(req.comparisonSymbol.equals("<=")) {
                    if(req.propertyBound < maximumPart) {
                        maximumValues.set(partCategory.propertyIndex, req.propertyBound);
                    }
                }

            }
            return new PartsBoundaries(minimumValues, maximumValues);
        }
    }

    @AllArgsConstructor
    static class PartsBoundaries {
        List<Integer> minimumValues;
        List<Integer> maximumValues;

        BigInteger numberOfDistinctCombinations() {
            BigInteger result = BigInteger.ONE;
            for(int i = 0; i <= 3; i++) {
                int factor = maximumValues.get(i) - minimumValues.get(i) + 1;
                if(factor <= 0) {
                    return BigInteger.ZERO;
                }
                result = result.multiply(BigInteger.valueOf(factor));
            }
            return result;
        }

        Set<Combination> distinctCombinations() {
            Set<Combination> result = new HashSet<>();
            for(int x = minimumValues.get(0); x <= maximumValues.get(0); x++) {
                for(int m = minimumValues.get(1); m <= maximumValues.get(1); m++) {
                    for(int a = minimumValues.get(2); a <= maximumValues.get(2); a++) {
                        for(int s = minimumValues.get(3); s <= maximumValues.get(3); s++) {
                            result.add(new Combination(x,m,a,s));
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "x ∈ < " + minimumValues.get(0) + " , " + maximumValues.get(0) + " >, " +
                    "m ∈ < " + minimumValues.get(1) + " , " + maximumValues.get(1) + " >, " +
                    "a ∈ < " + minimumValues.get(2) + " , " + maximumValues.get(2) + " >, " +
                    "s ∈ < " + minimumValues.get(3) + " , " + maximumValues.get(3) + " >";
        }
    }

    @AllArgsConstructor
    static class Combination {
        int x;
        int m;
        int a;
        int s;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Combination that = (Combination) o;
            return x == that.x && m == that.m && a == that.a && s == that.s;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, m, a, s);
        }
    }

    static class Workflow {
        String name;
        List<Rule> rules = new ArrayList<>();

        public Workflow(String line) {
            String[] parts = line.split("\\{");
            this.name = parts[0];
            String[] rulesStrings = parts[1].split("}")[0].split(",");
            for (String ruleString : rulesStrings) {
                this.rules.add(new Rule(ruleString));
            }
        }

        public List<String> nextWorkflows() {
            return rules.stream().map(rule -> rule.nextWorkflowName)
                    .filter(Objects::nonNull)
                    .toList();
        }

        public List<Condition> requirementsToProceedToWorkflow(String workflowName) {
            List<Condition> conditions = new ArrayList<>();
            for(Rule rule : rules) {
                if(Objects.equals(rule.nextWorkflowName, workflowName)) {
                    if(rule.hasCondition()) {
                        conditions.add(rule.condition);
                    }
                    break;
                }
                if(rule.hasCondition()) {
                    conditions.add(rule.condition.invertCondition());
                }
            }
            return conditions;
        }

        public List<Condition> requirementsToReachDecision(int ruleIndex) {
            if(rules.get(ruleIndex).decision != Decision.ACCEPT) {
                throw new IllegalArgumentException("Rule with idx=" + ruleIndex + "is not ACCEPT");
            }
            List<Condition> conditions = new ArrayList<>();
            for (int i = 0; i <= ruleIndex; i++) {
                Rule rule = rules.get(i);
                if (i == ruleIndex && rule.hasCondition()) {
                    conditions.add(rule.condition);
                    break;
                }
                if(rule.hasCondition()) {
                    conditions.add(rule.condition.invertCondition());
                }
            }
            return conditions;
        }

        public EvaluationResult processPart(Part part) {
            for (Rule rule : rules) {
                EvaluationResult ruleEvaluationResult = rule.evaluateRule(part);
                if (ruleEvaluationResult.conditionMet) {
                    return ruleEvaluationResult;
                }
            }
            throw new IllegalStateException();
        }
    }

    static class Rule {

        Condition condition;
        String nextWorkflowName;

        Decision decision;

        public EvaluationResult evaluateRule(Part part) {
            if (!hasCondition()) {
                if (makesFinalDecision()) {
                    return new EvaluationResult(decision);
                } else {
                    return new EvaluationResult(nextWorkflowName);
                }
            } else {
                int property = part.partProperties.get(condition.partCategory.propertyIndex);
                boolean conditionResult;
                if (condition.comparisonSymbol.equals("<")) {
                    conditionResult = property < condition.propertyBound;
                } else {
                    conditionResult = property > condition.propertyBound;
                }
                if (conditionResult) {
                    if (makesFinalDecision()) {
                        return new EvaluationResult(decision);
                    } else {
                        return new EvaluationResult(nextWorkflowName);
                    }
                } else {
                    return new EvaluationResult();
                }
            }
        }

        public Rule(String ruleString) {
            String[] split = ruleString.split(":");
            if (split.length > 1) {
                Condition ruleCondition = new Condition();
                ruleCondition.partCategory = PartCategory.parse(split[0].substring(0, 1));
                ruleCondition.comparisonSymbol = split[0].substring(1,2);
                ruleCondition.propertyBound = Integer.parseInt(split[0].substring(2));
                this.condition = ruleCondition;
                Decision ruleResult = Decision.parse(split[1]);
                if (ruleResult == null) {
                    this.nextWorkflowName = split[1];
                } else {
                    this.decision = ruleResult;
                }
            } else {
                Decision ruleResult = Decision.parse(split[0]);
                if (ruleResult == null) {
                    this.nextWorkflowName = split[0];
                } else {
                    this.decision = ruleResult;
                }
            }
        }

        public boolean hasCondition() {
            return condition != null;
        }

        public boolean makesFinalDecision() {
            return decision != null;
        }
    }

    static class Condition {
        PartCategory partCategory;
        String comparisonSymbol;
        int propertyBound;

        public Condition invertCondition() {
            Condition inverted = new Condition();
            inverted.partCategory = this.partCategory;
            if(this.comparisonSymbol.equals("<")) {
                inverted.comparisonSymbol = ">=";
            } else {
                inverted.comparisonSymbol = "<=";
            }
            inverted.propertyBound = this.propertyBound;
            return inverted;
        }

        @Override
        public String toString() {
            return partCategory +
                    " " + comparisonSymbol +
                    " " + propertyBound;
        }
    }

    static class EvaluationResult {

        boolean conditionMet;

        String nextWorkflowName;

        Decision decision;

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
