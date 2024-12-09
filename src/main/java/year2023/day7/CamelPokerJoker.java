package year2023.day7;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class CamelPokerJoker {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day7/input.txt"));

        List<CardsHand> cardsHands = new ArrayList<>(lines.stream().map(CardsHand::new).toList());

        cardsHands.sort(CardsHand::compare);

        for(CardsHand hand : cardsHands) {
            System.out.println(hand);
        }

        BigInteger totalWinnings = BigInteger.ZERO;
        for (int i = 0; i < cardsHands.size(); i++) {
            totalWinnings = totalWinnings.add(BigInteger.valueOf((long) cardsHands.get(i).bid * (i+1)));
        }
        System.out.println(totalWinnings);
    }

    static class CardsHand {
        Integer bid;

        List<Card> cards = new ArrayList<>();

        HandType handType;

        public CardsHand(String line) {
            String[] parts = line.split(" ");
            for (String cardLabel : parts[0].split("")) {
                cards.add(Card.parseCard(cardLabel));
            }
            this.bid = Integer.parseInt(parts[1]);
            List<Card> jokersReplaced = replaceJokers();
            this.handType = HandType.evaluate(jokersReplaced);
        }

        private List<Card> replaceJokers() {
            List<Card> newHand;
            List<Card> bestNewHand = this.cards;
            HandType bestHandType = HandType.HIGH_CARD;
            for(Card card : EnumSet.allOf(Card.class)) {
                newHand = new ArrayList<>(this.cards);
                Collections.replaceAll(newHand, Card.J, card);
                HandType evaluate = HandType.evaluate(newHand);
                if(evaluate.isWorthMoreThan(bestHandType)) {
                    bestNewHand = newHand;
                    bestHandType = evaluate;
                }
            }
            return bestNewHand;
        }

        static int compare(CardsHand a, CardsHand b) {
            if(a.handType.isWorthMoreThan(b.handType)) {
                return 1;
            }
            if(b.handType.isWorthMoreThan(a.handType)) {
                return -1;
            }
            for(int i = 0; i < a.cards.size(); i++) {
                if(a.cards.get(i).isWorthMoreThan(b.cards.get(i))) {
                    return 1;
                }
                if(b.cards.get(i).isWorthMoreThan(a.cards.get(i))) {
                    return -1;
                }
            }
            return 0;
        }

        @Override
        public String toString() {
            return "CardsHand{" +
                    "bid=" + bid +
                    ", cards=" + cards +
                    ", handType=" + handType +
                    '}';
        }
    }

    enum HandType {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIRS(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        final int typeValue;

        HandType(int typeValue) {
            this.typeValue = typeValue;
        }

        static HandType evaluate(List<Card> cards) {
            List<CardGroup> groupsInHand = new ArrayList<>(cards.stream().collect(groupingBy(Function.identity(), Collectors.counting())).entrySet()
                    .stream().map(entry -> new CardGroup(entry.getKey(), entry.getValue())).toList());
            groupsInHand.sort(Comparator.comparing(a -> a.numberOfCards, Comparator.reverseOrder()));
            if (groupsInHand.size() == 1) {
                return FIVE_OF_A_KIND;
            }
            if(groupsInHand.size() == 2 && groupsInHand.get(0).numberOfCards == 4) {
                return FOUR_OF_A_KIND;
            }
            if(groupsInHand.size() == 2 && groupsInHand.get(0).numberOfCards == 3) {
                return FULL_HOUSE;
            }
            if(groupsInHand.size() == 3 && groupsInHand.get(0).numberOfCards == 3) {
                return THREE_OF_A_KIND;
            }
            if(groupsInHand.size() == 3 && groupsInHand.get(0).numberOfCards == 2) {
                return TWO_PAIRS;
            }
            if(groupsInHand.size() == 4) {
                return ONE_PAIR;
            }
            if (groupsInHand.size() == 5) {
                return HIGH_CARD;
            }
            throw new IllegalStateException("BOOM");
        }

        boolean isWorthMoreThan(HandType otherHandType) {
            return this.typeValue > otherHandType.typeValue;
        }

        record CardGroup(Card card, Long numberOfCards) { }
    }

    enum Card {
        A("A", 13),
        K("K", 12),
        Q("Q", 11),
        J("J", 0),
        T("T", 9),
        NINE("9", 8),
        EIGHT("8", 7),
        SEVEN("7", 6),
        SIX("6", 5),
        FIVE("5", 4),
        FOUR("4", 3),
        THREE("3", 2),
        TWO("2", 1);

        final String label;
        final int cardValue;

        Card(String label, int cardValue) {
            this.label = label;
            this.cardValue = cardValue;
        }

        static Card parseCard(String label) {
            return EnumSet.allOf(Card.class).stream()
                    .filter(cardLabel -> cardLabel.label.equals(label))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal card label=" + label));
        }

        boolean isWorthMoreThan(Card otherCard) {
            return this.cardValue > otherCard.cardValue;
        }

    }
}
