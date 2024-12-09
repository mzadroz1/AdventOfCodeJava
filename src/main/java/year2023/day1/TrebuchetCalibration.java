package year2023.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrebuchetCalibration {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day1/input1.txt"));
        System.out.println(lines);
        Pattern pattern = Pattern.compile("([0-9])");
//        Matcher matcher = pattern.matcher("five3onelxjninenine45");
//        if(matcher.find()){
//            System.out.println(matcher.group());
//        }
        Map<String, String> digitsLiterals = Map.of(
                "one", "1",
                "two", "2",
                "three", "3",
                "four", "4",
                "five", "5",
                "six", "6",
                "seven", "7",
                "eight", "8",
                "nine", "9"
        );

//        Integer sum = lines.stream()
//                .map(line -> {
//                    Matcher matcher = pattern.matcher(line);
//                    matcher.find();
//                    for (int i = 3; i < line.length(); i++) {
//                        for (String key : digitsLiterals.keySet()) {
//                            String temp = line.substring(0, i);
//                            String temp1 = temp.replaceAll(key, digitsLiterals.get(key));
//                            if(!temp1.equals(temp)) {
//                                line = new StringBuilder(temp).append(line.substring(i, line.length())).toString();
//                                break;
//                            }
//                        }
//                    }
//                    String firstDigit = matcher.group();
//                    Matcher matcherLast = pattern.matcher(new StringBuilder(line).reverse().toString());
//                    matcherLast.find();
//                    String lastDigit = matcherLast.group();
//                    return Integer.parseInt(firstDigit) * 10 + Integer.parseInt(lastDigit);
//                }).reduce(0, Integer::sum);
//        System.out.println(sum);
        int sum = 0;
        for(String line : lines) {
            Integer firstDigit = null;
            Integer lastDigit = null;
            boolean digitFound = false;
            for(int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if(Character.isDigit(c)) {
                    int digitValue = Integer.parseInt(String.valueOf(c));
                    if(!digitFound) {
                       firstDigit = digitValue;
                       digitFound = true;
                    }
                    lastDigit = digitValue;
                }
                else {
                    for(String key : digitsLiterals.keySet()) {
                        if(i + key.length() <= line.length()) {
                            String substring = line.substring(i, i + key.length());
                            if(substring.equals(key)){
                                int digitValue = Integer.parseInt(digitsLiterals.get(key));
                                if(!digitFound) {
                                    firstDigit = digitValue;
                                    digitFound = true;
                                }
                                lastDigit = digitValue;
                                break;
                            }
                        }
                    }
                }
            }
            sum += firstDigit * 10 + lastDigit;
        }
        System.out.println(sum);
    }
}
