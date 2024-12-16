public class CurrencyManager {
    private static int currency = 0; // Default starting coins
    private static int revivePotionCount = 0; // Number of revive potions owned
    private static int healthPotionCount = 0; // Number of health potions owned

    /**
     * Returns the current balance of the player's currency.
     * @return Current amount of currency.
     */
    public static int getCurrency() {
        return currency;
    }

    /**
     * Deducts the specified amount of currency from the player.
     * @param amount Amount to deduct.
     * @return True if the deduction was successful, false if insufficient funds.
     */
    public static boolean deductCurrency(int amount) {
        if (amount <= 0) {
            System.out.println("[ERROR] Invalid deduction amount: " + amount);
            return false;
        }
        if (currency >= amount) {
            currency -= amount;
            System.out.println("[DEBUG] Deducted " + amount + " coins. Remaining: " + currency);
            return true;
        }
        System.out.println("[DEBUG] Not enough coins. Current balance: " + currency + ". Needed: " + amount);
        return false;
    }

    /**
     * Adds the specified amount of currency to the player's balance.
     * @param amount Amount to add.
     */
    public static void addCurrency(int amount) {
        if (amount <= 0) {
            System.out.println("[ERROR] Invalid addition amount: " + amount);
            return;
        }
        currency += amount;
        System.out.println("[DEBUG] Added " + amount + " coins. Total: " + currency);
    }

    /**
     * Adds the specified number of revive potions to the inventory.
     * @param count Number of revive potions to add.
     */
    public static void addRevivePotion(int count) {
        if (count > 0) {
            revivePotionCount += count;
            System.out.println("[DEBUG] Added " + count + " revive potion(s). Total: " + revivePotionCount);
        }
    }

    /**
     * Deducts the specified number of revive potions from the inventory.
     * @param count Number of revive potions to deduct.
     */
    public static void deductRevivePotion(int count) {
        if (revivePotionCount >= count) {
            revivePotionCount -= count;
            System.out.println("[DEBUG] Deducted " + count + " revive potion(s). Remaining: " + revivePotionCount);
        } else {
            System.out.println("[ERROR] Not enough revive potions to deduct.");
        }
    }

    /**
     * Adds the specified number of health potions to the inventory.
     * @param count Number of health potions to add.
     */
    public static void addHealthPotion(int count) {
        if (count > 0) {
            healthPotionCount += count;
            System.out.println("[DEBUG] Added " + count + " health potion(s). Total: " + healthPotionCount);
        }
    }

    /**
     * Deducts the specified number of health potions from the inventory.
     * @param count Number of health potions to deduct.
     */
    public static void deductHealthPotion(int count) {
        if (healthPotionCount >= count) {
            healthPotionCount -= count;
            System.out.println("[DEBUG] Deducted " + count + " health potion(s). Remaining: " + healthPotionCount);
        } else {
            System.out.println("[ERROR] Not enough health potions to deduct.");
        }
    }

    /**
     * Returns the current number of revive potions in the inventory.
     * @return Number of revive potions.
     */
    public static int getRevivePotionCount() {
        return revivePotionCount;
    }

    /**
     * Returns the current number of health potions in the inventory.
     * @return Number of health potions.
     */
    public static int getHealthPotionCount() {
        return healthPotionCount;
    }
}