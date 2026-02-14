import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dice {

    private static final int[] SIDE_TRACK = {2, 4, 6, 8, 12, 20};
    private static final int POOL_CAP = 100;

    private static final List<Die> pool = new ArrayList<>();
    private static final Random rand = new Random();

    // Clears the pool so it can be rebuilt from maindeck.txt
    public static void resetPool() {
        pool.clear();
    }

    /**
     * Pool comes from maindeck.txt:
     * Each #...# definition adds one die, using the length as "sides"
     * (snapped to 2->4->6->8->12->20).
     */
    public static void init(String faces) {
        if (faces == null || faces.isEmpty()) return;
        if (pool.size() >= POOL_CAP) return;

        int sides = faces.length();
        pool.add(new Die(sides));
    }

    public static final class Die {
        private int sideTrackIndex; // index into SIDE_TRACK
        private int bonus;          // permanent +1 per win

        private Die(int sides) {
            this.sideTrackIndex = indexForSides(sides);
            this.bonus = 0;
        }

        public int getSides() {
            return SIDE_TRACK[sideTrackIndex];
        }

        public int getBonus() {
            return bonus;
        }

        private int roll() {
            return rand.nextInt(1, getSides() + 1) + bonus; // 1..sides + bonus
        }

        private void addBonus(int delta) {
            bonus += delta;
        }

        private void shiftSides(int step) {
            int next = Math.max(0, Math.min(SIDE_TRACK.length - 1, sideTrackIndex + step));
            sideTrackIndex = next;
        }

        private static int indexForSides(int sides) {
            for (int i = 0; i < SIDE_TRACK.length; i++) {
                if (SIDE_TRACK[i] == sides) return i;
            }
            return 1; // default to d4
        }
    }

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

    public static Roll roll() {
        if (pool.isEmpty()) {
            throw new IllegalStateException("Dice pool is empty. Did you load maindeck.txt dice with Dice.init(...) ?");
        }
        Die die = pool.get(rand.nextInt(pool.size()));
        return new Roll(die, die.roll());
    }

    public static void improveWinner(DiceFace winner) {
        if (winner == null || winner.getDie() == null) return;

        Die die = winner.getDie();

        die.addBonus(1);

        if (oneIn(100)) {
            if (pool.size() < POOL_CAP) {
                pool.add(new Die(4));
            }
        }

        if (oneIn(10)) {
            boolean backfire = oneIn(10);
            die.shiftSides(backfire ? -1 : +1);
        }
    }

    public static int getPoolSize() {
        return pool.size();
    }

    private static boolean oneIn(int n) {
        return rand.nextInt(n) == 0;
    }
}

