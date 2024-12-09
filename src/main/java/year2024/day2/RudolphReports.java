package year2024.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RudolphReports {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day2/input.txt"));
        List<Report> reports = new ArrayList<>();
        int safeReportsCount = 0;
        for (String line : lines) {
            Report report = new Report(line);
            reports.add(report);
            if(report.isSafe()) {
                safeReportsCount++;
            }
        }
        System.out.println(safeReportsCount);

        int safeReportsWithDampener = 0;
        for (Report report : reports) {
            boolean isSafe = report.isSafe();
            for(int i = 0; i < report.levels.size(); i++) {
                if(!isSafe) {
                    Report reportWithRemovedLevel = new Report(report, i);
                    isSafe = reportWithRemovedLevel.isSafe();
                }
            }
            if(isSafe) {
                safeReportsWithDampener++;
            }
        }
        System.out.println(safeReportsWithDampener);

    }

    static class Report {
        List<Integer> levels;

        public Report(String line) {
            this.levels = Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
        }

        public Report(Report originalReport, int levelToRemove) {
            this.levels = new ArrayList<>();
            this.levels.addAll(originalReport.levels);
            this.levels.remove(levelToRemove);
        }

        public boolean isSafe() {
            boolean isDecreasing;
            isDecreasing = levels.get(0) > levels.get(1);
            for (int i = 1; i < levels.size(); i++) {
                if(isDecreasing) {
                    if(levels.get(i) > levels.get(i - 1)) {
                        return false;
                    }
                } else {
                    if(levels.get(i) < levels.get(i - 1)) {
                        return false;
                    }
                }
                int levelsDifference = Math.abs(levels.get(i) - levels.get(i - 1));
                if(levelsDifference < 1 || levelsDifference > 3) {
                    return false;
                }
            }
            return true;
        }
    }
}
