public class ExoticPet {
    private String name;
    private int health;
    private int maxHealth; // Maximum health value of the pet
    private int lifespan;
    private String habitat;
    private String affinity;
    private int price;

    // Constructor to initialize the exotic pet
    public ExoticPet(String name, int health, int lifespan, String habitat, String affinity, int price) {
        this.name = name;
        this.health = health;
        this.maxHealth = health; // Set the initial maxHealth to the starting health value
        this.lifespan = lifespan;
        this.habitat = habitat;
        this.affinity = affinity;
        this.price = price;
    }

    // Get the pet's name
    public String getName() {
        return name;
    }

    // Get the pet's current health
    public int getHealth() {
        return health;
    }

    // Set the pet's health
    public void setHealth(int health) {
        if (health < 0) {
            this.health = 0; // Ensure health does not go below 0
        } else if (health > maxHealth) {
            this.health = maxHealth; // Ensure health does not exceed maxHealth
        } else {
            this.health = health;
        }
    }

    // Get the pet's maximum health
    public int getMaxHealth() {
        return maxHealth;
    }

    // Decrease the pet's health by a specified amount
    public void decreaseHealth(int amount) {
        if (amount > 0) {
            this.health -= amount;
            if (this.health < 0) {
                this.health = 0; // Ensure health does not go below 0
            }
            System.out.println("[DEBUG] Decreased " + name + "'s health by " + amount + ". Current health: " + health);
        }
    }

    // Decrease the pet's lifespan by 1 unit
    public void decreaseLifespan() {
        if (lifespan > 0) {
            lifespan--;
            System.out.println("[DEBUG] Decreased " + name + "'s lifespan by 1. Current lifespan: " + lifespan);
        }
    }

    // Get the pet's affinity
    public String getAffinity() {
        return affinity;
    }

    // Get the pet's lifespan
    public int getLifespan() {
        return lifespan;
    }

    // Get the pet's habitat
    public String getHabitat() {
        return habitat;
    }

    // Get the pet's price
    public int getPrice() {
        return price;
    }
}
