package year2023.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Srapki {

    public static void main(String[] args) throws IOException {
        List<String> cards = Files.readAllLines(Path.of("src/main/resources/year2023/input.txt/input.txt"));

        Integer valueSum = 0;
        for(String card : cards) {
//            int id = Integer.parseInt(card.split(": ")[0].split(" ")[1]);
            List<Integer> winningNumbers = parseNumbersList(card, 0);
            List<Integer> yourNumbers = parseNumbersList(card, 1);
            Integer cardValue = 0;
            for(Integer yourNumber : yourNumbers) {
                if(winningNumbers.contains(yourNumber)) {
                    if(cardValue == 0) {
                        cardValue = 1;
                    }
                    else {
                        cardValue *= 2;
                    }
                }
            }
            valueSum += cardValue;
        }
        System.out.println(valueSum);
    }

    private static List<Integer> parseNumbersList(String card, int winOrYour) {
        List<Integer> winningNumbers = Stream.of(card.split(": ")[1]
                .split(" \\| ")[winOrYour].split(" "))
                .filter(str -> !str.isBlank() && !str.isEmpty())
                .map(Integer::parseInt).toList();
        return winningNumbers;
    }


}
