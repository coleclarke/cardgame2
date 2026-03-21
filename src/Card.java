import java.util.Objects;

public abstract class Card extends GameObject {


    public enum Value {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN,
        EIGHT, NINE, TEN, JACK, QUEEN, KING
    }


    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }

    private Value value;
    private Suit suit;

    protected Card(Value value, Suit suit) {
        super(value + " of " + suit);                     // initialise name for GameObject
        this.value = Objects.requireNonNull(value, "value");
        this.suit  = Objects.requireNonNull(suit,  "suit");
    }

    /* ----------  Parsing helpers  ---------- */
    public static Value valueFromCode(char v) {
        return switch (v) {
            case 'a' -> Value.ACE;
            case '2' -> Value.TWO;
            case '3' -> Value.THREE;
            case '4' -> Value.FOUR;
            case '5' -> Value.FIVE;
            case '6' -> Value.SIX;
            case '7' -> Value.SEVEN;
            case '8' -> Value.EIGHT;
            case '9' -> Value.NINE;
            case '0' -> Value.TEN;
            case 'j' -> Value.JACK;
            case 'q' -> Value.QUEEN;
            case 'k' -> Value.KING;
            default  -> throw new IllegalArgumentException("Bad value code: " + v);
        };
    }

    public static Suit suitFromCode(char s) {
        return switch (s) {
            case 'c' -> Suit.CLUBS;
            case 'd' -> Suit.DIAMONDS;
            case 'h' -> Suit.HEARTS;
            case 's' -> Suit.SPADES;
            default  -> throw new IllegalArgumentException("Bad suit code: " + s);
        };
    }

    /* ----------  Getters / Setters  ---------- */
    public Value getValue() { return value; }
    public Suit  getSuit()  { return suit;  }

    protected void setValue(Value value) { this.value = Objects.requireNonNull(value, "value"); }
    protected void setSuit(Suit suit)   { this.suit  = Objects.requireNonNull(suit,  "suit"); }

    /* ----------  Upgrade helpers  ---------- */
    public void updateCardValue() {
        if (this.value != Value.KING) {
            this.value = Value.values()[this.value.ordinal() + 1];
        }
    }

    public void updateCardSuit() {
        if (this.suit != Suit.SPADES) {
            this.suit = Suit.values()[this.suit.ordinal() + 1];
        }
    }

    public int compareTo(Card o) {
        return this.value.ordinal() - o.value.ordinal();
    }

    public int compareSuit(Card o) {
        return this.suit.ordinal() - o.suit.ordinal();
    }

    @Override
    public String toString() {
        return suit + " " + value;
    }
}


