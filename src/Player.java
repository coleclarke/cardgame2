import java.util.Objects;
import java.util.Random;

public class Player {
    private final String name;

    private HandCard currentCard;

    public Player(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getName() {
        return name;
    }

    public HandCard drawCard() {
        this.currentCard = Deck.draw(); // draw flips a flag in deck + returns a COPY into the hand
        return currentCard;
    }

    public DiceFace rollDieFace() {
        return new DiceFace();
    }

    public HandCard getCurrentCard() {
        return currentCard;
    }

    public void improveCurrentCardRandomly(Random rand) {
        if (currentCard == null) return;

        if (rand.nextInt(2) == 0) currentCard.updateCardValue();
        else currentCard.updateCardSuit();
    }

    public void returnCurrentCardToDeck() {
        if (currentCard == null) return;
        Deck.returnCard(currentCard); // persists changes + flips removed flag back
        currentCard = null;
    }

    @Override
    public String toString() {
        return name;
    }
}
