public class DiceFace implements Comparable<DiceFace> {

    private final int value;
    // The specific Die object from the shared pool that was used for this roll
    private final Dice.Die die;

    public DiceFace() {
        // Roll once from the shared pool
        Dice.Roll roll = Dice.roll();

        // Save the rolled number
        this.value = roll.value();

        // Save which die was used
        this.die = roll.die();
    }

    // Lets Dice.improveWinner(...) know which die to upgrade
    public Dice.Die getDie() {
        return die;
    }

    @Override
    public int compareTo(DiceFace o) {
        // Positive means "this roll is higher", negative means lower, 0 means tie
        return value - o.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
