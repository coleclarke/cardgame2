import java.util.Objects;
import java.util.Random;

public class Player {
    private final String name;

    private Card currentCard;
    private DiceFace currentDiceFace;

    public Player(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getName() {
        return name;
    }

    public Card drawCard() {
        this.currentCard = new Card(); // Card() draws from Deck.draw()
        return currentCard;
    }

    public DiceFace rollDieFace() {
        this.currentDiceFace = new DiceFace(); // DiceFace() rolls from shared Dice pool
        return currentDiceFace;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public DiceFace getCurrentDiceFace() {
        return currentDiceFace;
    }

    public void improveCurrentCardRandomly(Random rand) {
        if (currentCard == null) return;

        // winner's card randomly increases rank by 1 OR suit by 1
        if (rand.nextInt(2) == 0) currentCard.updateCardValue();
        else currentCard.updateCardSuit();
    }

    public void returnCurrentCardToDeck() {
        if (currentCard == null) return;
        Deck.returnCard(currentCard);
        currentCard = null;
    }

    @Override
    public String toString() {
        return name;
    }
}
