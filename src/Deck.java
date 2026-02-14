import java.util.*;

public class Deck {

    private static final List<Card> cards = new ArrayList<>();
    private static final Random rand = new Random();

    public static void init(List<String> raw) {
        // emptys deck
        cards.clear();
        // adds cards to deck using the codes
        for (String s : raw) {
            cards.add(Card.fromCode(s));
        }
    }

    public static Card draw() {
        //gives card while taking it away from deck
        if (cards.isEmpty()) throw new IllegalStateException("Deck empty");
        return cards.remove(rand.nextInt(cards.size()));
    }

    // Put a card back into the deck after the round (winner may be upgraded)
    public static void returnCard(Card c) {
        cards.add(c);
    }

    public static int size() {
        //used in tests
        return cards.size();
    }

    public static void prntCards() {
        System.out.println(cards);
        System.out.println(cards.size());
    }
}
