import java.util.*;

public class Deck {

    private static final List<DeckCard> cards = new ArrayList<>();
    private static final Random rand = new Random();
    private static int nextDeckId = 1;

    public static void init(List<String> raw) {
        cards.clear();
        nextDeckId = 1;

        for (String s : raw) {
            if (s == null || s.length() != 2) {
                throw new IllegalArgumentException("Bad card code: " + s);
            }
            Card.Value value = Card.valueFromCode(s.charAt(0));
            Card.Suit suit = Card.suitFromCode(s.charAt(1));
            cards.add(new DeckCard(nextDeckId++, value, suit));
        }
    }

    // Draw does NOT remove from the list anymore; it flips a FLAG and returns a COPY for the player's hand.
    public static HandCard draw() {
        DeckCard chosen = drawDeckCardOrThrow();
        chosen.setRemoved(true);
        return new HandCard(chosen); // copy goes into the hand
    }

    private static DeckCard drawDeckCardOrThrow() {
        List<DeckCard> available = new ArrayList<>();
        for (DeckCard c : cards) {
            if (!c.isRemoved()) available.add(c);
        }
        if (available.isEmpty()) {
            throw new IllegalStateException("Deck empty (all cards are flagged as removed).");
        }
        System.out.println(cards);
        return available.get(rand.nextInt(available.size()));
    }

    // Return a hand card: find its source DeckCard, persist any upgrades, then flip the FLAG back.
    public static void returnCard(HandCard handCard) {
        if (handCard == null) return;

        DeckCard deckCard = findById(handCard.getSourceDeckId());
        if (deckCard == null) {
            throw new IllegalStateException("Tried to return a HandCard whose sourceDeckId does not exist: " + handCard.getSourceDeckId());
        }

        deckCard.copyFrom(handCard);     // persist any changes made while in the hand
        deckCard.setRemoved(false);      // flip the flag back: card is now available again
    }

    private static DeckCard findById(int deckId) {
        for (DeckCard c : cards) {
            if (c.getDeckId() == deckId) return c;
        }
        return null;
    }

    public static int size() {
        return cards.size();
    }

    public static int availableCount() {
        int count = 0;
        for (DeckCard c : cards) {
            if (!c.isRemoved()) count++;
        }
        return count;
    }

    public static void prntCards() {
        System.out.println(cards);
        System.out.println(cards.size());
        System.out.println("Available (not removed): " + availableCount());
    }
}
