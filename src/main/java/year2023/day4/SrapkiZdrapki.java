package year2023.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SrapkiZdrapki {

    public static void main(String[] args) throws IOException {
        List<String> cards = Files.readAllLines(Path.of("src/main/resources/year2023/input.txt/input.txt"));

        Map<Integer, List<Card>> srapki = new HashMap<>();
        for (String card : cards) {
            Card srapka = new Card(card);
            srapki.put(srapka.id, List.of(srapka));
        }
        for (Integer i : srapki.keySet()) {
            for (Card card : srapki.get(i)) {
                for (int j = i + 1; j <= i + card.cardValue; j++) {
                    List<Card> followingCards = srapki.get(j);
                    Card nextCard = followingCards.get(0);
                    Card newCard = new Card(nextCard.id, nextCard.winningNumbers,
                            nextCard.yourNumbers, nextCard.cardValue);
                    List<Card> newList = new ArrayList<>(followingCards);
                    newList.add(newCard);
                    srapki.put(j, newList);
                }
            }
            System.out.println("Card " + i + "processed.");
        }

        Integer srapkiCount = 0;

        for(Integer i : srapki.keySet()) {
            srapkiCount += srapki.get(i).size();
        }

        System.out.println(srapkiCount);

//        System.out.println(srapki);
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


}
