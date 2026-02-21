import org.testng.Assert;
import org.testng.annotations.Test;


public class DeckTest {
    private Deck deck;
    @Test
    public void testDeckInitialization() {
        Display.initialize();
        System.out.println(deck);
    }
}
