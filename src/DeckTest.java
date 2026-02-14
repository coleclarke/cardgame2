import org.testng.Assert;
import org.testng.annotations.Test;


public class DeckTest {
    private Deck deck;
    @Test
    public void testDeckInitialization() {
        //Tests the deck can actually initilize and the deck isnt empty
        Display.initialize();
        Card card = new Card();
        Assert.assertNotNull(card);
    }
    @Test
    public void testDrawReducesDeckSize() {
        // Tests the deck looses cards when you pull them
        Display.initialize();
        int before = Deck.size();
        new Card();
        int after = Deck.size();
        Assert.assertEquals(after, before - 1);
    }
    @Test
    public void testCardComparison() {
        //creates a ace and a 2 card and makes sure the ace is higher
        Card low = Card.fromCode("2c");
        Card high = Card.fromCode("ac");

        Assert.assertTrue(low.compareTo(high) < 0);
        Assert.assertTrue(high.compareTo(low) > 0);
    }
}
