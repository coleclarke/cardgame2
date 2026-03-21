import java.util.Objects;
import java.util.Random;

public class Player {
    private final String name;
    private GameObject current; // current card or die face

    public Player(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getName() { return name; }

    /** Draw a card from the deck – cast to HandCard internally. */
    public GameObject drawCard() {
        HandCard hand = (HandCard) Deck.draw();   // Deck.draw() already returns a DeckCard
        current = hand;
        return current;
    }

    /** Roll a die – the returned object is a DiceFace. */
    public DiceFace rollDieFace() {   // <-- return type changed from GameObject to DiceFace
        return new DiceFace();
    }

    /** Improve the current card (if it is a HandCard). */
    public void improveCurrentCardRandomly(Random r) {
        if (current instanceof HandCard) {
            HandCard card = (HandCard) current;
            String before = card.toString();
            // Randomly pick value or suit to upgrade
            if (r.nextBoolean()) card.updateCardValue();
            else                card.updateCardSuit();
            System.out.println("Card improvement (" + name + "): " + before + " -> " + card);
        } else {
            System.out.println("Card improvement: tie, no change");
        }
    }

    /** Return the current card to the deck. */
    public void returnCurrentCardToDeck() {
        if (current instanceof HandCard) {
            Deck.returnCard((HandCard) current);
            current = null;
        }
    }

    /** Expose the current card for the UI. */
    public GameObject getCurrentCard() { return current; }

    @Override
    public String toString() { return name; }
}
