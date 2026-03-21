public class DiceFace extends GameObject {

    private final int value;          // rolled number
    private final Dice.Die die;       // the die that produced this roll

    public DiceFace() {
        super("Dice Face"); // generic name; can be overridden if desired
        Dice.Roll roll = Dice.roll();
        this.value = roll.value();
        this.die   = roll.die();
    }

    public Dice.Die getDie() { return die; }

    /** Strength is simply the rolled number. */
    @Override
    protected int strength() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
