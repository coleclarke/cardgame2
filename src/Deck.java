import java.util.*;

public class Deck {

    private static final List<Card> cards = new ArrayList<>();
    private static final Random rand = new Random();

    public static void init(List<String> raw) {
        cards.clear();

        for (String s : raw) {
            cards.add(Card.fromCode(s));
        }
    }

    public static Card draw() {
        if (cards.isEmpty()) throw new IllegalStateException("Deck empty");
        return cards.remove(rand.nextInt(cards.size()));
    }

    public  static void removeCard(Card c) {
        cards.remove(c);
    }

    public static int size() {
        return cards.size();
    }
}
