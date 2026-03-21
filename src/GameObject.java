

import java.util.Objects;


public abstract class GameObject implements Comparable<GameObject> {

    private final String id;   // optional unique identifier
    private final String name; // human‑readable label

    protected GameObject(String name) {
        this.name = Objects.requireNonNull(name, "name");
        this.id   = java.util.UUID.randomUUID().toString();
    }

    public String getName()   { return name; }
    public String getId()     { return id;   }

    protected abstract int strength();

    @Override
    public int compareTo(GameObject o) {
        return Integer.compare(this.strength(), o.strength());
    }

    @Override
    public String toString() {
        return name + " (" + strength() + ")";
    }
    public String typeCode() {
        return getClass().getSimpleName().substring(0, 1).toUpperCase();
    }
}
