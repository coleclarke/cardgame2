import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Display {

    public static void initialize() {

        List<String> rawCards = new ArrayList<>();
        List<String> diceDefs = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("src/maindeck.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) continue;

                line = line.trim();
                if (line.isEmpty()) continue;

                int i = 0;
                while (i < line.length()) {
                    char ch = line.charAt(i);

                    if (ch == '#') {
                        i++;
                        StringBuilder die = new StringBuilder();
                        while (i < line.length() && line.charAt(i) != '#') {
                            die.append(line.charAt(i++));
                        }
                        diceDefs.add(die.toString());

                        if (i < line.length() && line.charAt(i) == '#') i++;
                        continue;
                    }


                    if (i + 1 < line.length()) {
                        char v = line.charAt(i);
                        char s = line.charAt(i + 1);

                        boolean valueOk = (v == 'a' || v == '2' || v == '3' || v == '4' || v == '5' || v == '6'
                                || v == '7' || v == '8' || v == '9' || v == '0' || v == 'j' || v == 'q' || v == 'k');
                        boolean suitOk = (s == 'c' || s == 'd' || s == 'h' || s == 's');

                        if (valueOk && suitOk) {
                            rawCards.add("" + v + s);
                            i += 2;
                            continue;
                        }
                    }


                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("maindeck.txt not found");
            System.exit(1);
        }

        Deck.init(rawCards);

        Dice.resetPool();
        for (String d : diceDefs) {
            Dice.init(d);
        }
    }

    public static void main(String[] args) {
        initialize();
        Random rand = new Random();

        controller gfx = new controller();
        gfx.initialize("Dice Game");

        System.out.println("Welcome to the Card / Dice Battler!");
        System.out.println("Click the window, then press Y to continue, N to quit, R to restart.");

        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");

        while (true) {
            HandCard c1 = p1.drawCard();
            HandCard c2 = p2.drawCard();

            DiceFace d1 = p1.rollDieFace();
            DiceFace d2 = p2.rollDieFace();

            gfx.render(p1, p2, List.of(d1, d2));

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

            p1.returnCurrentCardToDeck();
            p2.returnCurrentCardToDeck();

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

            char cmd = gfx.waitForCommand(Set.of('y', 'n', 'r', 't'));
            if (cmd == 'r') {
                initialize();
                System.out.println("Restarted!\n\n\n");
            } else if (cmd == 't') {
                Deck.prntCards();
            } else if (cmd != 'y') {
                break;
            }
        }
    }

    // old way of comparing cards
    private static String compare(Card a, Card b, String player1, String player2) {
        if (a.compareTo(b) < 0) {
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