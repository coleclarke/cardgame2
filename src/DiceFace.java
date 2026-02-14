public class DiceFace implements Comparable<DiceFace> {

    private final int value;
    private final Dice.Die die;

    public DiceFace() {
        Dice.Roll roll = Dice.roll();
        this.value = roll.value();
        this.die = roll.die();
    }

    public Dice.Die getDie() {
        return die;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(DiceFace o) {
        return value - o.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
