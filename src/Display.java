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
            // each player draws
            Card c1 = p1.drawCard();
            Card c2 = p2.drawCard();

            System.out.println();
            System.out.println(c1 + " (" + p1.getName() + ")");
            System.out.println(c2 + " (" + p2.getName() + ")");
            System.out.println(compare(c1, c2, p1.getName(), p2.getName()));

            // Determine card winner
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

            // Return cards back to deck (after upgrades)
            p1.returnCurrentCardToDeck();
            p2.returnCurrentCardToDeck();

            //
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



/*

    class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            Font oldFont = g2.getFont();
            String[] tempCard;
            // Cards
            if(playerCard.isLessthan(houseCard)) {
                g2.drawRect(50, 50, 100, 150);
                g2.setColor(Color.green);
                g2.drawRect(200, 50, 100, 150);
                g2.setColor(Color.black);
            }
            else if(houseCard.isLessthan(playerCard)) {
                g2.setColor(Color.green);
                g2.drawRect(50, 50, 100, 150);
                g2.setColor(Color.black);
                g2.drawRect(200, 50, 100, 150);
            }
            else {
                g2.drawRect(50, 50, 100, 150);
                g2.drawRect(200, 50, 100, 150);
            }


            tempCard = playerCard.toString().split(" ");
            if(tempCard[0].equals("Hearts") || tempCard[0].equals("Diamonds")) {
                g2.setColor(Color.RED);
                g2.drawString("Player Card", 60, 45);
                g2.setFont(oldFont.deriveFont(24f));
                if(tempCard[0].equals("Hearts")){

                g2.drawString("♡", 53, 68);
                g2.drawString("♡", 133, 198);
                }
                else{
                g2.drawString("♢", 53, 68);
                g2.drawString("♢", 133, 198);
                }
                if(tempCard[1].equals("KING")||tempCard[1].equals("QUEEN")||tempCard[1].equals("JACK")||tempCard[1].equals("ACE")) {
                    g2.setFont(oldFont);
                    g2.drawString(tempCard[1], 70, 63);
                    if(tempCard[1].equals("QUEEN")) {
                        g2.drawString(tempCard[1], 90, 193);
                    }
                    else if(tempCard[1].equals("ACE")) {
                        g2.drawString(tempCard[1], 105, 193);
                    }
                    else{
                        g2.drawString(tempCard[1], 100, 193);
                    }

                }
                else {
                    g2.setFont(oldFont.deriveFont(16f));
                    g2.drawString(" "+stringToNumber(tempCard[1]), 67, 65);
                    g2.drawString(" "+stringToNumber(tempCard[1]), 118, 195);
                    g2.setFont(oldFont);
                }

                g2.setColor(Color.BLACK);
            }
            else {
                g2.drawString("Player Card", 60, 45);
                g2.setFont(oldFont.deriveFont(24f));
                if(tempCard[0].equals("Clubs")){

                    g2.drawString("♧", 53, 68);
                    g2.drawString("♧", 133, 198);
                }
                else{
                    g2.drawString("♤", 53, 68);
                    g2.drawString("♤", 133, 198);
                }
                if(tempCard[1].equals("KING")||tempCard[1].equals("QUEEN")||tempCard[1].equals("JACK")||tempCard[1].equals("ACE")) {
                    g2.setFont(oldFont);
                    g2.drawString(tempCard[1], 70, 63);
                    if(tempCard[1].equals("QUEEN")) {
                        g2.drawString(tempCard[1], 90, 193);
                    }
                    else if(tempCard[1].equals("ACE")) {
                        g2.drawString(tempCard[1], 105, 193);
                    }
                    else{
                        g2.drawString(tempCard[1], 100, 193);
                    }

                }
                else {
                    g2.setFont(oldFont.deriveFont(16f));
                    g2.drawString(" "+stringToNumber(tempCard[1]), 67, 65);
                    g2.drawString(" "+stringToNumber(tempCard[1]), 118, 195);
                    g2.setFont(oldFont);
                }
            }

            tempCard = houseCard.toString().split(" ");

            if(tempCard[0].equals("Hearts") || tempCard[0].equals("Diamonds")) {
                g2.setColor(Color.RED);
                g2.drawString("House Card", 215, 45);
                g2.setFont(oldFont.deriveFont(24f));
                if(tempCard[0].equals("Hearts")){

                    g2.drawString("♡", 53+150, 68);
                    g2.drawString("♡", 133+150, 198);
                }
                else{
                    g2.drawString("♢", 53+150, 68);
                    g2.drawString("♢", 133+150, 198);
                }
                if(tempCard[1].equals("KING")||tempCard[1].equals("QUEEN")||tempCard[1].equals("JACK")||tempCard[1].equals("ACE")) {
                    g2.setFont(oldFont);
                    g2.drawString(tempCard[1], 70+150, 63);
                    if(tempCard[1].equals("QUEEN")) {
                        g2.drawString(tempCard[1], 90+150, 193);
                    }
                    else if(tempCard[1].equals("ACE")) {
                        g2.drawString(tempCard[1], 105+150, 193);
                    }
                    else{
                        g2.drawString(tempCard[1], 100+150, 193);
                    }

                }
                else {
                    g2.setFont(oldFont.deriveFont(16f));
                    g2.drawString(" "+stringToNumber(tempCard[1]), 67+150, 65);
                    g2.drawString(" "+stringToNumber(tempCard[1]), 118+150, 195);
                    g2.setFont(oldFont);
                }

                g2.setColor(Color.BLACK);
            }
            else {
                g2.drawString("House Card", 215, 45);
                g2.setFont(oldFont.deriveFont(24f));
                if(tempCard[0].equals("Clubs")){

                    g2.drawString("♧", 53+150, 68);
                    g2.drawString("♧", 133+150, 198);
                }
                else{
                    g2.drawString("♤", 53+150, 68);
                    g2.drawString("♤", 133+150, 198);
                }
                if(tempCard[1].equals("KING")||tempCard[1].equals("QUEEN")||tempCard[1].equals("JACK")||tempCard[1].equals("ACE")) {
                    g2.setFont(oldFont);
                    g2.drawString(tempCard[1], 70+150, 63);
                    if(tempCard[1].equals("QUEEN")) {
                        g2.drawString(tempCard[1], 90+150, 193);
                    }
                    else if(tempCard[1].equals("ACE")) {
                        g2.drawString(tempCard[1], 105+150, 193);
                    }
                    else{
                        g2.drawString(tempCard[1], 100+150, 193);
                    }

                }
                else {
                    g2.setFont(oldFont.deriveFont(16f));
                    g2.drawString(" "+stringToNumber(tempCard[1]), 67+150, 65);
                    g2.drawString(" "+stringToNumber(tempCard[1]), 118+150, 195);
                    g2.setFont(oldFont);
                }
            }
            // Dice
            g2.drawRect(50, 250, 60, 60);
            g2.drawRect(200, 250, 60, 60);

            g2.drawString("Player Dice: " + playerDice, 50, 235);
            g2.drawString("House Dice: " + houseDice, 200, 235);
        }
    }


}
*/
