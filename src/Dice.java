import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dice {

    private static final int[] SIDE_TRACK = {2, 4, 6, 8, 12, 20};

    // Hard limit so the pool can't grow forever
    private static final int POOL_CAP = 100;

    // Shared pool: BOTH players roll from this same list of dice
    private static final List<Die> pool = new ArrayList<>();

    private static final Random rand = new Random();

    // Add this method to access dice pool from outside
    public static List<Die> getPool() {
        return new ArrayList<>(pool);
    }

    // Clears the pool
    public static void resetPool() {
        pool.clear();
    }

    // Adds die definition into the pool
    public static void init(String faces) {
        if (faces == null || faces.isEmpty()) return;

        // Stop adding dice once we hit the max pool size
        if (pool.size() >= POOL_CAP) return;


        int sides;
        int declaredSides = Character.digit(faces.charAt(0), 10);
        if (declaredSides > 0) {
            sides = declaredSides;
        } else {
            // Fallback/legacy interpretation: number of sides comes from length
            sides = faces.length();
        }

        // Create the die (it will snap sides to SIDE_TRACK inside the constructor)
        pool.add(new Die(sides));
    }

    // A single die in the pool (it can evolve over time)
    public static final class Die {
        // Index into SIDE_TRACK (0 means d2, 1 means d4, etc.)
        private int sideTrackIndex;

        public int bonus;          // permanent +1 per win

        private Die(int sides) {
            // Convert sides
            this.sideTrackIndex = indexForSides(sides);
            this.bonus = 0;
        }

        public int getSides() {
            return SIDE_TRACK[sideTrackIndex];
        }


        private int roll() {
            // Roll a number from 1..sides, then add the permanent bonus
            return rand.nextInt(1, getSides() + 1) + bonus; // 1..sides + bonus
        }

        private void addBonus(int delta) {
            bonus += delta;
        }

        private void shiftSides(int step) {
            sideTrackIndex = Math.max(0, Math.min(SIDE_TRACK.length - 1, sideTrackIndex + step));
        }

        private static int indexForSides(int sides) {
            for (int i = 0; i < SIDE_TRACK.length; i++) {
                if (SIDE_TRACK[i] == sides) return i;
            }
            return 1; // default to d4
        }
    }

    // Result of a roll: we return BOTH the rolled value and which die was used
    // (so we can upgrade the correct die after a win)
    public static final class Roll {
        private final Die die;
        private final int value;

        private Roll(Die die, int value) {
            this.die = die;
            this.value = value;
        }

        public Die die() {
            return die;
        }

        public int value() {
            return value;
        }
    }

    // Picks a random die from the pool and rolls it
    public static Roll roll() {
        if (pool.isEmpty()) {
            // This means maindeck.txt didn't load any dice
            throw new IllegalStateException("Dice pool is empty. Did you load maindeck.txt dice with Dice.init(...) ?");
        }

        // Choose a random die from the shared pool
        Die die = pool.get(rand.nextInt(pool.size()));

        // Roll it and package the result
        return new Roll(die, die.roll());
    }

    // Applies ALL winner upgrades to the die that was used for the winning roll
    public static void improveWinner(DiceFace winner) {
        if (winner == null || winner.getDie() == null) return;

        Die die = winner.getDie();

        int oldBonus = die.bonus;
        int oldSides = die.getSides();
        int oldPoolSize = pool.size();

        // winner's die gets +1 permanently
        die.addBonus(1);
        System.out.println("Die improvement: bonus " + oldBonus + " -> " + die.bonus);

        // add a new d4 into the pool
        if (oneIn(100)) {
            if (pool.size() < POOL_CAP) {
                pool.add(new Die(4));
                System.out.println("Die improvement: added a new d4 to the pool (" + oldPoolSize + " -> " + pool.size() + ")");
            } else {
                System.out.println("Die improvement: tried to add a d4, but pool is capped at " + POOL_CAP);
            }
        }

        // try to increase die size with possible backfire
        if (oneIn(10)) {
            boolean backfire = oneIn(10);
            die.shiftSides(backfire ? -1 : +1);

            int newSides = die.getSides();
            if (newSides != oldSides) {
                System.out.println("Die improvement: sides " + oldSides + " -> " + newSides + (backfire ? " (backfire)" : ""));
            } else {
                System.out.println("Die improvement: sides stayed at " + oldSides + (backfire ? " (backfire)" : ""));
            }
        }
    }

    public static int getPoolSize() {
        return pool.size();
    }

    private static boolean oneIn(int n) {
        return rand.nextInt(n) == 0;
    }
}

