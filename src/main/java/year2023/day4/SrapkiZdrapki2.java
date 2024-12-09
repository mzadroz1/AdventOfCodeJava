package year2023.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SrapkiZdrapki2 {

    public static void main(String[] args) throws IOException {
        List<String> cards = Files.readAllLines(Path.of("src/main/resources/year2023/input.txt/input.txt"));

        List<StosSrapek> srapki = new ArrayList<>();
        for (String card : cards) {
            Card srapka = new Card(card);
            srapki.add(new StosSrapek(srapka.id, 1, srapka.cardValue));
        }
        for (int i = 0; i < srapki.size(); i++) {
            for (int j = i + 1; j <= i + srapki.get(i).cardValue; j++) {
                srapki.get(j).srapkiCount += srapki.get(i).srapkiCount;
            }
        }

        Integer srapkiCount = 0;

        for (StosSrapek stosSrapek : srapki) {
            srapkiCount += stosSrapek.srapkiCount;
        }

        System.out.println(srapkiCount);
    }

    static class Card {
        Integer id;
        List<Integer> winningNumbers;
        List<Integer> yourNumbers;

        Integer cardValue;

        public Card(String cardLine) {
            String[] temp = cardLine.split(": ")[0].split(" ");
            id = Integer.parseInt(temp[temp.length - 1]);
            winningNumbers = parseNumbersList(cardLine, 0);
            yourNumbers = parseNumbersList(cardLine, 1);
            cardValue = calculateCardValue();
        }

        public Card(Integer id, List<Integer> winningNumbers, List<Integer> yourNumbers, Integer cardValue) {
            this.id = id;
            this.winningNumbers = winningNumbers;
            this.yourNumbers = yourNumbers;
            this.cardValue = cardValue;
        }

        private static List<Integer> parseNumbersList(String card, int winOrYour) {
            return Stream.of(card.split(": ")[1]
                            .split(" \\| ")[winOrYour].split(" "))
                    .filter(str -> !str.isBlank() && !str.isEmpty())
                    .map(Integer::parseInt).toList();
        }

        Integer calculateCardValue() {
            Integer cardValue = 0;
            for (Integer yourNumber : yourNumbers) {
                if (winningNumbers.contains(yourNumber)) {
                    cardValue++;
                }
            }
            return cardValue;
        }

        @Override
        public String toString() {
            return "Card{" +
                    "id=" + id +
                    ", winningNumbers=" + winningNumbers +
                    ", yourNumbers=" + yourNumbers +
                    ", cardValue=" + cardValue +
                    '}';
        }


    }

    static class StosSrapek {
        Integer id;
        Integer srapkiCount;
        Integer cardValue;

        public StosSrapek(Integer id, Integer srapkiCount, Integer cardValue) {
            this.id = id;
            this.srapkiCount = srapkiCount;
            this.cardValue = cardValue;
        }
    }


}
