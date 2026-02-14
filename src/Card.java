public class Card implements Comparable<Card> {

    enum Value {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN,
        EIGHT, NINE, TEN, JACK, QUEEN, KING
    }

    enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }

    private Value value;
    private Suit suit;

    private Card(Value v, Suit s) {
        value = v;
        suit = s;
    }

    public static Card fromCode(String code) {
        char v = code.charAt(0);
        char s = code.charAt(1);

        Value value = switch (v) {
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
            default -> throw new IllegalArgumentException();
        };

        Suit suit = switch (s) {
            case 'c' -> Suit.CLUBS;
            case 'd' -> Suit.DIAMONDS;
            case 'h' -> Suit.HEARTS;
            case 's' -> Suit.SPADES;
            default -> throw new IllegalArgumentException();
        };

        return new Card(value, suit);
    }

    public Card() {
        Card c = Deck.draw();
        this.value = c.value;
        this.suit = c.suit;
    }
    public void updateCardValue() {
        if (this.value != Value.KING) {
            this.value = Value.values()[this.value.ordinal() + 1];
            Card c = new Card(this.value, this.suit);
            Deck.removeCard(c);
        }
    }
    public void updateCardSuit() {
        if (this.suit != Suit.SPADES) {
            this.suit = Suit.values()[this.suit.ordinal() + 1];
            Card c = new Card(this.value, this.suit);
            Deck.removeCard(c);
        }
    }
    @Override
    public int compareTo(Card o) {
        return value.ordinal() - o.value.ordinal();
    }


    public int compareSuit(Card o) {
        return suit.ordinal() - o.suit.ordinal();
    }

    @Override
    public String toString() {
        return suit + " " + value;
    }


}


