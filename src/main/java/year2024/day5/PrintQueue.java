package year2024.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrintQueue {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day5/input.txt"));
        boolean areAllRulesParsed = false;
        List<PrintingRule> printingRules = new ArrayList<>();
        List<SafetyManualUpdate> updates = new ArrayList<>();
        for (String line : lines) {
            if(line.isEmpty()) {
                areAllRulesParsed = true;
                continue;
            }
            if(areAllRulesParsed) {
                updates.add(new SafetyManualUpdate(line));
            } else {
                printingRules.add(new PrintingRule(line));
            }
        }
        int sumOfMiddlePages = 0;
        for(SafetyManualUpdate safetyManualUpdate: updates) {
            if(safetyManualUpdate.isUpdateCorrect(printingRules)) {
                sumOfMiddlePages += safetyManualUpdate.getMiddlePage();
            }
        }
        System.out.println(sumOfMiddlePages);

        int sumOfMiddlePagesInCorrectedUpdates = 0;
        for(SafetyManualUpdate safetyManualUpdate: updates) {
            if(!safetyManualUpdate.isUpdateCorrect(printingRules)) {
                while(!safetyManualUpdate.isUpdateCorrect(printingRules)) {
                    PrintingRule firstNotMetRule = safetyManualUpdate.findFirstNotMetRule(printingRules);
                    safetyManualUpdate.reorderIncorrectPages(firstNotMetRule);
                }
                sumOfMiddlePagesInCorrectedUpdates += safetyManualUpdate.getMiddlePage();
            }
        }
        System.out.println(sumOfMiddlePagesInCorrectedUpdates);
    }

    static class PrintingRule {
        int previousPage;
        int nextPage;

        public PrintingRule(String line) {
            String[] parts = line.split("\\|");
            previousPage = Integer.parseInt(parts[0]);
            nextPage = Integer.parseInt(parts[1]);
        }

        public boolean isApplicable(SafetyManualUpdate safetyManualUpdate) {
            return safetyManualUpdate.pagesToPrint.containsAll(List.of(previousPage, nextPage));
        }

        public boolean isRuleMet(SafetyManualUpdate safetyManualUpdate) {
            return safetyManualUpdate.pagesToPrint.indexOf(previousPage) < safetyManualUpdate.pagesToPrint.indexOf(nextPage);
        }
    }

    static class SafetyManualUpdate {
        List<Integer> pagesToPrint;

        public SafetyManualUpdate(String line) {
            this.pagesToPrint = Arrays.stream(line.split(","))
                    .map(Integer::parseInt)
                    .toList();
        }

        public boolean isUpdateCorrect(List<PrintingRule> printingRules) {
            return printingRules.stream()
                    .filter(rule -> rule.isApplicable(this))
                    .allMatch(rule -> rule.isRuleMet(this));
        }

        public int getMiddlePage() {
            return pagesToPrint.get(Math.floorDiv(pagesToPrint.size(), 2));
        }

        public PrintingRule findFirstNotMetRule(List<PrintingRule> printingRules) {
            return printingRules.stream()
                    .filter(rule -> rule.isApplicable(this))
                    .filter(rule -> !rule.isRuleMet(this))
                    .findFirst()
                    .orElse(null);
        }

        public void reorderIncorrectPages(PrintingRule rule) {
            List<Integer> newPagesToPrint = new ArrayList<>(this.pagesToPrint);
            int previousPageIndex = pagesToPrint.indexOf(rule.previousPage);
            int nextPageIndex = pagesToPrint.indexOf(rule.nextPage);
            newPagesToPrint.set(previousPageIndex, rule.nextPage);
            newPagesToPrint.set(nextPageIndex, rule.previousPage);
            this.pagesToPrint = newPagesToPrint;
        }
    }
}
