import java.util.Objects;

public final class HandCard extends Card {
    private final int sourceDeckId;

    public HandCard(int sourceDeckId, Value value, Suit suit) {
        super(value, suit);
        this.sourceDeckId = sourceDeckId;
    }

    // Copy constructor from a DeckCard (this is the "copy goes into the hand" rule)
    public HandCard(DeckCard deckCard) {
        this(deckCard.getDeckId(), deckCard.getValue(), deckCard.getSuit());
    }

    public int getSourceDeckId() {
        return sourceDeckId;
    }
}
