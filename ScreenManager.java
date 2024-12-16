import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenManager {

    private static final GameEngine engine = new GameEngine(); // Use a single instance of GameEngine
    private static ExoticPet currentPet = null; // Track the currently selected pet

    // Start Menu scene
    public static Scene getStartMenu() {
        BorderPane root = new BorderPane();
        Text title = new Text("Welcome to Mythical Pet Universe");
        Button adoptPetButton = new Button("Adopt a Pet");
        Button playGameButton = new Button("Play Minigame");
        Button bagButton = new Button("BAG"); // Bag button to show inventory (coins and potions)
        Button selectPetButton = new Button("Select Pets"); // Button for managing owned pets
        Button shopButton = new Button("Shop"); // Button for accessing the shop

        adoptPetButton.setOnAction(e -> Main.getPrimaryStage().setScene(getAdoptionScreen())); // Navigate to adoption screen
        playGameButton.setOnAction(e -> {
            if (currentPet == null) {
                showTemporaryPopup("You must adopt a pet and select it before playing any mini-games.");
            } else if (currentPet.getHealth() <= 0) {
                showTemporaryPopup("Your pet's health is 0. Use a revive potion before playing.");
            } else {
                Main.getPrimaryStage().setScene(getMinigameScreen()); // Navigate to mini-game screen if a pet is selected and has health
            }
        });
        bagButton.setOnAction(e -> Main.getPrimaryStage().setScene(getBagScreen())); // Navigate to bag screen
        selectPetButton.setOnAction(e -> {
            if (PetManager.getAdoptedPets().isEmpty()) {
                showTemporaryPopup("You need at least 1 pet to use this feature!");
            } else {
                Main.getPrimaryStage().setScene(getSelectPetScreen()); // Navigate to select pets screen
            }
        });
        shopButton.setOnAction(e -> Main.getPrimaryStage().setScene(getShopScreen())); // Navigate to shop screen

        root.setTop(title); // Place the title at the top
        root.setLeft(adoptPetButton); // Place the adopt button on the left
        root.setCenter(playGameButton); // Place the play game button in the center
        root.setRight(bagButton); // Place the bag button on the right
        root.setBottom(new VBox(10, selectPetButton, shopButton)); // Stack the select pet and shop buttons at the bottom

        return new Scene(root, 480, 270); // Return the start menu scene
    }

    // Adoption screen scene
    public static Scene getAdoptionScreen() {
        BorderPane root = new BorderPane();

        Text title = new Text("Adopt a Pet!");
        ListView<String> petListView = new ListView<>();
        Button adoptButton = new Button("Adopt Selected Pet");
        Button backButton = new Button("Back to Menu");

        // Populate the list with available pets, including health, affinity, and price if the free adoption has been used
        if (PetManager.canAdoptForFree()) {
            PetManager.getAllPets().forEach(pet -> petListView.getItems().add(
                    pet.getName() + " (Health: " + pet.getHealth() + ", Affinity: " + pet.getAffinity() + ", Lifespan: " + pet.getLifespan() + ")"));
        } else {
            PetManager.getAllPets().forEach(pet -> petListView.getItems().add(
                    pet.getName() + " (Health: " + pet.getHealth() + ", Affinity: " + pet.getAffinity() + ", Lifespan: " + pet.getLifespan() + ", Cost: " + pet.getPrice() + " coins)"));
        }

        adoptButton.setOnAction(e -> {
            int selectedIndex = petListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                ExoticPet selectedPet = PetManager.getAllPets().get(selectedIndex); // Get the selected pet

                if (!PetManager.isPetAdopted(selectedPet)) {
                    if (PetManager.canAdoptForFree()) {
                        // First pet is free
                        engine.adoptPet(selectedPet);
                        petListView.getItems().remove(selectedIndex); // Update ListView
                        PetManager.removePet(selectedPet); // Remove from available pets
                        if (currentPet == null) {
                            currentPet = selectedPet; // Set the first adopted pet as the default pet for playing
                        }
                    } else if (CurrencyManager.getCurrency() >= selectedPet.getPrice()) {
                        // Deduct coins and adopt
                        engine.adoptPet(selectedPet);
                        petListView.getItems().remove(selectedIndex); // Update ListView
                        PetManager.removePet(selectedPet); // Remove from available pets
                    } else {
                        int deficit = selectedPet.getPrice() - CurrencyManager.getCurrency();
                        showTemporaryPopup("You don't have enough coins to adopt " + selectedPet.getName() + ". You need " + deficit + " more coins!");
                    }
                } else {
                    showTemporaryPopup("This pet is already adopted!");
                }
            } else {
                showTemporaryPopup("No pet selected to adopt.");
            }
        });

        backButton.setOnAction(e -> Main.getPrimaryStage().setScene(getStartMenu())); // Navigate back to the main menu

        VBox buttonBox = new VBox(10, adoptButton, backButton); // Stack buttons vertically with spacing
        root.setTop(title);
        root.setCenter(petListView);
        root.setBottom(buttonBox);

        return new Scene(root, 480, 270); // Return the adoption screen scene
    }


    // Mini-game screen scene
    public static Scene getMinigameScreen() {
        BorderPane root = new BorderPane();
        Text title = new Text("Play a Minigame!");
        Button skydivingButton = new Button("Play Skydiving");
        Button fireControlButton = new Button("Play Fire Control Simulator");
        Button backButton = new Button("Back");

        // Ensure a pet is selected before playing
        if (currentPet == null || currentPet.getHealth() <= 0) {
            showTemporaryPopup("You must adopt a healthy pet and select it before playing any mini-games."); // If no pet selected or pet health is zero, show error
            return getStartMenu(); // Return to start menu
        }

        skydivingButton.setOnAction(e -> {
            SkydivingGame game = new SkydivingGame(); // Create a new instance of the skydiving mini-game
            Scene gameScene = game.playGame(Main.getPrimaryStage(), currentPet); // Get the scene for skydiving mini-game with the selected pet
            Main.getPrimaryStage().setScene(gameScene); // Set the scene to start the game
        });

        fireControlButton.setOnAction(e -> {
            FireControlSimulator game = new FireControlSimulator(); // Create a new instance of the fire control mini-game
            Scene gameScene = game.playGame(Main.getPrimaryStage(), currentPet); // Get the scene for fire control mini-game with the selected pet
            Main.getPrimaryStage().setScene(gameScene); // Set the scene to start the game
        });

        backButton.setOnAction(e -> Main.getPrimaryStage().setScene(getStartMenu())); // Navigate back to the main menu

        VBox gameButtons = new VBox(10, skydivingButton, fireControlButton, backButton); // Stack game buttons vertically with spacing
        root.setTop(title);
        root.setCenter(gameButtons);

        return new Scene(root, 480, 270); // Return the mini-game screen scene
    }
    
    // Select pets screen scene for managing owned pets
    public static Scene getSelectPetScreen() {
        BorderPane root = new BorderPane();

        Text title = new Text("Manage Your Pets!");
        ListView<String> ownedPetsListView = new ListView<>();
        Button selectPetButton = new Button("Select This Pet to Play");
        Button sellPetButton = new Button("Sell Selected Pet");
        Button usePotionButton = new Button("Use Potion"); // Button for using potions
        Button backButton = new Button("Back to Menu");

        // Populate the list with adopted pets showing details: name, affinity, health, and selling price
        PetManager.getAdoptedPets().forEach(pet -> ownedPetsListView.getItems().add(
                pet.getName() + " (Affinity: " + pet.getAffinity() + ", Health: " + pet.getHealth() + ", Selling Price: " + (pet.getPrice() / 2) + " coins)"));

        selectPetButton.setOnAction(e -> {
            int selectedIndex = ownedPetsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                ExoticPet selectedPet = PetManager.getAdoptedPets().get(selectedIndex); // Get the selected pet
                currentPet = selectedPet; // Update the current selected pet
                showTemporaryPopup("You have selected " + selectedPet.getName() + " to play with.");
            } else {
                showTemporaryPopup("No pet selected.");
            }
        });

        sellPetButton.setOnAction(e -> {
            int selectedIndex = ownedPetsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                ExoticPet selectedPet = PetManager.getAdoptedPets().get(selectedIndex); // Get the selected pet
                int sellPrice = selectedPet.getPrice() / 2; // Sell at half the original price
                CurrencyManager.addCurrency(sellPrice);
                PetManager.removeAdoptedPet(selectedPet); // Remove from adopted pets
                ownedPetsListView.getItems().remove(selectedIndex); // Update ListView
                showTemporaryPopup("You sold " + selectedPet.getName() + " for " + sellPrice + " coins.");

                // If the sold pet is the current pet, reset currentPet
                if (selectedPet.equals(currentPet)) {
                    currentPet = null; // No pet is selected for playing
                }
            } else {
                showTemporaryPopup("No pet selected to sell.");
            }
        });

        usePotionButton.setOnAction(e -> {
            int selectedIndex = ownedPetsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                ExoticPet selectedPet = PetManager.getAdoptedPets().get(selectedIndex); // Get the selected pet
                if (selectedPet.getHealth() <= 0 && CurrencyManager.getRevivePotionCount() > 0) {
                    selectedPet.setHealth(50); // Revive the pet with a set amount of health
                    CurrencyManager.deductRevivePotion(1); // Use one revive potion
                    showTemporaryPopup("You revived " + selectedPet.getName() + " using a revive potion!");
                } else if (selectedPet.getHealth() < selectedPet.getMaxHealth() && CurrencyManager.getHealthPotionCount() > 0) {
                    selectedPet.setHealth(selectedPet.getMaxHealth()); // Fully heal the pet
                    CurrencyManager.deductHealthPotion(1); // Use one health potion
                    showTemporaryPopup("You healed " + selectedPet.getName() + " to full health using a health potion!");
                } else {
                    showTemporaryPopup("Cannot use a potion on the selected pet.");
                }
            } else {
                showTemporaryPopup("No pet selected to use potion.");
            }
        });

        backButton.setOnAction(e -> Main.getPrimaryStage().setScene(getStartMenu())); // Navigate back to the main menu

        VBox buttonBox = new VBox(10, selectPetButton, sellPetButton, usePotionButton, backButton); // Stack buttons vertically with spacing
        root.setTop(title);
        root.setCenter(ownedPetsListView);
        root.setBottom(buttonBox);

        return new Scene(root, 480, 270); // Return the select pets screen scene
    }

    // Bag screen to display coins and potion count
    public static Scene getBagScreen() {
        BorderPane root = new BorderPane();
        Text title = new Text("Your Bag");
        Text coinsText = new Text("Coins: " + CurrencyManager.getCurrency());
        Text revivePotionsText = new Text("Revive Potions: " + CurrencyManager.getRevivePotionCount());
        Text healthPotionsText = new Text("Health Potions: " + CurrencyManager.getHealthPotionCount());
        Button backButton = new Button("Back to Menu");

        backButton.setOnAction(e -> Main.getPrimaryStage().setScene(getStartMenu())); // Navigate back to the main menu

        VBox infoBox = new VBox(10, coinsText, revivePotionsText, healthPotionsText, backButton); // Stack information and back button vertically
        root.setTop(title);
        root.setCenter(infoBox);

        return new Scene(root, 480, 270); // Return the bag screen scene
    }

    // Shop screen scene for purchasing items
    public static Scene getShopScreen() {
        BorderPane root = new BorderPane();
        Text title = new Text("Welcome to the Shop!");
        Button revivePotionButton = new Button("Buy Revive Potion (50 coins)");
        Button healthPotionButton = new Button("Buy Health Potion (200 coins)");
        Button backButton = new Button("Back to Menu");

        revivePotionButton.setOnAction(e -> {
            if (CurrencyManager.getCurrency() >= 50) {
                CurrencyManager.deductCurrency(50);
                CurrencyManager.addRevivePotion(1);
                showTemporaryPopup("You bought a Revive Potion for 50 coins! Use it wisely.");
            } else {
                showTemporaryPopup("You do not have enough coins to buy a Revive Potion.");
            }
        });

        healthPotionButton.setOnAction(e -> {
            if (CurrencyManager.getCurrency() >= 200) {
                CurrencyManager.deductCurrency(200);
                CurrencyManager.addHealthPotion(1);
                showTemporaryPopup("You bought a Health Potion for 200 coins! Use it wisely.");
            } else {
                showTemporaryPopup("You do not have enough coins to buy a Health Potion.");
            }
        });

        backButton.setOnAction(e -> Main.getPrimaryStage().setScene(getStartMenu())); // Navigate back to the main menu

        VBox buttonBox = new VBox(10, revivePotionButton, healthPotionButton, backButton); // Stack buttons vertically with spacing
        root.setTop(title);
        root.setCenter(buttonBox);

        return new Scene(root, 480, 270); // Return the shop screen scene
    }

    // Show temporary pop-up with a given message for 3 seconds and print to console
    private static void showTemporaryPopup(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        System.out.println(message); // Also print message to console
        alert.show();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(alert::close);
            }
        }, 3000); // 3 seconds
    }

	public static Object getCurrentPet() {
		return currentPet;
	}
}
