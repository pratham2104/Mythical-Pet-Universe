public class GameEngine {
    private PetManager petManager;

    public GameEngine() {
        petManager = new PetManager();
    }

    // Handles pet adoption
    public void adoptPet(ExoticPet pet) {
        if (PetManager.isPetAdopted(pet)) {
            System.out.println("You already adopted this pet.");
        } else if (PetManager.canAdoptForFree()) { // First pet is free
            PetManager.addAdoptedPet(pet);
            PetManager.useFreeAdoption(); // Mark free adoption as used
            System.out.println("You adopted " + pet.getName() + " for free!");
        } else if (CurrencyManager.getCurrency() >= pet.getPrice()) { // Check if the player can afford the pet
            if (CurrencyManager.deductCurrency(pet.getPrice())) { // Deduct the price
                PetManager.addAdoptedPet(pet);
                System.out.println("You adopted " + pet.getName() + " for " + pet.getPrice() + " coins!");
            }
        } else {
            int deficit = pet.getPrice() - CurrencyManager.getCurrency();
            System.out.println("You don't have enough coins to adopt " + pet.getName() + ". You need " + deficit + " more coins!");
        }
    }

    // Plays a mini-game and rewards the player with coins based on the game's output
    public void playMiniGame(MiniGame game) {
        int reward = game.play();
        CurrencyManager.addCurrency(reward);
        System.out.println("You now have " + CurrencyManager.getCurrency() + " coins!");
    }

    // Ages all pets and decreases their lifespan, but does not remove them even if health reaches zero
    public void agePets() {
        // Iterate through adopted pets to age them (reduce lifespan)
        for (ExoticPet pet : PetManager.getAdoptedPets()) {
            pet.decreaseLifespan(); // Reduce lifespan to simulate aging
            if (pet.getHealth() <= 0) {
                System.out.println("Warning: " + pet.getName() + "'s health is at 0. Consider buying a health potion to restore health.");
            }
        }
    }
}
