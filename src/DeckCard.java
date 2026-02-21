import java.util.Objects;

public final class DeckCard extends Card {
    private final int deckId;
    private boolean removed; // FLAG: true means "currently out of the deck"

    public DeckCard(int deckId, Value value, Suit suit) {
        super(value, suit);
        this.deckId = deckId;
        this.removed = false;
    }

    public int getDeckId() {
        return deckId;
    }

    public boolean isRemoved() {
        return removed;
    }

    // Deck controls this flag; keep it package-private to discourage random outside toggling
    void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void copyFrom(HandCard handCard) {
        Objects.requireNonNull(handCard, "handCard");
        setValue(handCard.getValue());
        setSuit(handCard.getSuit());
    }
}
