import java.util.Objects;

public final class HandCard extends Card {
    private final int sourceDeckId;

    public HandCard(int sourceDeckId, Value value, Suit suit) {
        super(value, suit);
        this.sourceDeckId = sourceDeckId;
    }

    public HandCard(DeckCard deckCard) {
        this(deckCard.getDeckId(), deckCard.getValue(), deckCard.getSuit());
    }

    public int getSourceDeckId() { return sourceDeckId; }


    @Override
    protected int strength() {
        // Use the card’s rank as its numeric strength
        return getValue().ordinal();
    }
}
