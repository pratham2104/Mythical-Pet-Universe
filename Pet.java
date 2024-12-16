
public class Pet {
    private String name;
    private int health;
    private int lifespan;

    public Pet(String name, int health, int lifespan) {
        this.name = name;
        this.health = health;
        this.lifespan = lifespan;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void reduceHealth(int amount) {
        health = Math.max(0, health - amount);
    }

    public int getLifespan() {
        return lifespan;
    }

    public void age() {
        lifespan--;
    }
}
