import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Display {

    public static void initialize() {
        // Load cards and dice from maindeck.txt I know you wanted 4 dice and 52 cards but this allowes more customization and idk what the point of maindeck.txt is otherwise
        List<String> rawCards = new ArrayList<>();
        List<String> diceDefs = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("src/maindeck.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                int i = 0;
                while (i < line.length()) {
                    if (line.charAt(i) == '#') {
                        i++;
                        StringBuilder die = new StringBuilder();
                        while (i < line.length() && line.charAt(i) != '#') {
                            die.append(line.charAt(i++));
                        }
                        diceDefs.add(die.toString());
                    } else {
                        rawCards.add(line.substring(i, i + 2));
                        i += 2;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("maindeck.txt not found");
            System.exit(1);
        }
        // pushes the cards into the deck
        Deck.init(rawCards);
        // resets the dice pool incase of restart and pushes dice
        Dice.resetPool();
        for (String d : diceDefs) {
            Dice.init(d);
        }
    }

    static void main() {
        initialize();
        Scanner in = new Scanner(System.in);
        Random rand = new Random();



        System.out.println("Welcome to the Card / Dice Battler!");

        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");

        while (true) {
            // each player draws (HandCard copy; deck uses a removed-flag)
            HandCard c1 = p1.drawCard();
            HandCard c2 = p2.drawCard();

            System.out.println();
            System.out.println(c1 + " (" + p1.getName() + ")");
            System.out.println(c2 + " (" + p2.getName() + ")");
            System.out.println(compare(c1, c2, p1.getName(), p2.getName()));

            Player cardWinner = null;
            if (c1.compareTo(c2) > 0 || (c1.compareTo(c2) == 0 && c1.compareSuit(c2) > 0)) {
                cardWinner = p1;
            } else if (c2.compareTo(c1) > 0 || (c2.compareTo(c1) == 0 && c2.compareSuit(c1) > 0)) {
                cardWinner = p2;
            }

            if (cardWinner != null) {
                String before = cardWinner.getCurrentCard().toString();
                cardWinner.improveCurrentCardRandomly(rand);
                System.out.println("Card improvement (" + cardWinner.getName() + "): " + before + " -> " + cardWinner.getCurrentCard());
            } else {
                System.out.println("Card improvement: tie, no change");
            }

            // Return cards back to deck (this flips the removed flag back AND persists upgrades)
            p1.returnCurrentCardToDeck();
            p2.returnCurrentCardToDeck();

            DiceFace d1 = p1.rollDieFace();
            DiceFace d2 = p2.rollDieFace();

            System.out.println(d1 + " (" + p1.getName() + ")");
            System.out.println(d2 + " (" + p2.getName() + ")");

            if (d1.compareTo(d2) > 0) {
                System.out.println(p1.getName() + "'s die face is higher.");
                Dice.improveWinner(d1);
            } else if (d1.compareTo(d2) < 0) {
                System.out.println(p2.getName() + "'s die face is higher.");
                Dice.improveWinner(d2);
            } else {
                System.out.println("Tie!");
            }

            System.out.println("Dice pool size: " + Dice.getPoolSize());
            System.out.print("Continue? (y/n) Or Restart (r): ");
            String temp = in.nextLine();
            if (temp.equals("r")) {
                initialize();
                System.out.println("Restarted!\n\n\n");
            } else if (temp.equals("t")) {
                Deck.prntCards();
            } else if (!temp.equals("y")) break;
        }
    }



    // old way of comparing cards too lazy to move into main class or make the dice like this
    private static String compare(Card a, Card b, String player1, String player2) {
        if (a.compareTo(b) < 0){
            return player2 + "'s card is higher";
        }
        if (a.compareTo(b) > 0) {
            return player1 + "'s card is higher";
        }
        if (a.compareSuit(b) > 0) {
            return player1 + "'s card is higher";
        }
        if (a.compareSuit(b) < 0) {
            return player2 + "'s card is higher";
        }
        return "Tie";
    }
}
