import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FireControlSimulator extends MiniGame {
    private static final int GRID_SIZE = 5; // 5x5 grid of buildings
    private Button[][] buildingButtons = new Button[GRID_SIZE][GRID_SIZE];
    private boolean[][] onFire = new boolean[GRID_SIZE][GRID_SIZE];
    private int score = 0;
    private Random rand = new Random();
    private Timer fireSpreadTimer = new Timer();
    private ExoticPet currentPet; // Store the reference to the pet being used in the game

    // Play game scene where the pet plays
    public Scene playGame(Stage stage, ExoticPet pet) {
        this.currentPet = pet; // Store the pet object for later use

        // Introduction Scene with Center-Aligned Text
        BorderPane introRoot = new BorderPane();
        Text introText = new Text("Welcome to Fire Control Simulator!\n"
                + pet.getName() + " is now a firefighter!\n"
                + "Click on the buildings to extinguish fires.\n"
                + "Prevent the fire from spreading to save the city!");
        Button startButton = new Button("OK");

        startButton.setOnAction(e -> stage.setScene(createGameScene(stage, pet)));

        introRoot.setCenter(introText);
        BorderPane.setAlignment(introText, Pos.CENTER);
        introRoot.setBottom(startButton);
        BorderPane.setAlignment(startButton, Pos.CENTER);

        return new Scene(introRoot, 600, 400);
    }

    // Create the main game scene where the player controls fires
    private Scene createGameScene(Stage stage, ExoticPet pet) {
        BorderPane root = new BorderPane();
        Text scoreText = new Text("Score: 0");
        GridPane cityGrid = new GridPane();
        cityGrid.setAlignment(Pos.CENTER);
        cityGrid.setHgap(5);
        cityGrid.setVgap(5);

        // Initialize buttons and fire status
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Button buildingButton = new Button("Building");
                buildingButton.setPrefSize(80, 80);
                buildingButtons[i][j] = buildingButton;
                onFire[i][j] = false; // Initially, no building is on fire
                cityGrid.add(buildingButton, j, i);

                // Action when a building button is clicked
                final int row = i;
                final int col = j;
                buildingButton.setOnAction(e -> {
                    if (onFire[row][col]) {
                        extinguishFire(row, col);
                        score += (pet.getAffinity().equalsIgnoreCase("fire")) ? 20 : 10; // Fire affinity earns extra points
                        scoreText.setText("Score: " + score);

                        // If pet's affinity is water, extinguish an additional adjacent fire if possible
                        if (pet.getAffinity().equalsIgnoreCase("water")) {
                            extinguishAdjacentFire(row, col);
                        }
                    }
                    checkGameEnd(stage);
                });
            }
        }

        // Start the fire at random locations
        igniteRandomFire();

        // Layout
        root.setTop(scoreText);
        BorderPane.setAlignment(scoreText, Pos.TOP_CENTER);
        root.setCenter(cityGrid);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            fireSpreadTimer.cancel(); // Stop the fire spreading when returning to menu
            if (!areAllBuildingsOnFire()) {
                CurrencyManager.addCurrency(score); // Only add currency if the player wins
            }
            Main.getPrimaryStage().setScene(ScreenManager.getStartMenu());
        });
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        // Fire spread timer
        startFireSpreadTimer();

        return new Scene(root, 600, 600);
    }

    // Ignite random buildings at the beginning
    private void igniteRandomFire() {
        int firesToStart = 3; // Start with 3 fires
        for (int i = 0; i < firesToStart; i++) {
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);
            igniteFire(row, col);
        }
    }

    // Ignite a building (set it on fire)
    private void igniteFire(int row, int col) {
        if (!onFire[row][col]) {
            onFire[row][col] = true;
            buildingButtons[row][col].setStyle("-fx-background-color: red;"); // Red color indicates fire
        }
    }

    // Extinguish a fire in a building
    private void extinguishFire(int row, int col) {
        if (onFire[row][col]) {
            onFire[row][col] = false;
            buildingButtons[row][col].setStyle(""); // Reset to default color
            buildingButtons[row][col].setText("Safe");
        }
    }

    // Extinguish an adjacent fire if possible (for water affinity)
    private void extinguishAdjacentFire(int row, int col) {
        if (row > 0 && onFire[row - 1][col]) extinguishFire(row - 1, col); // Up
        else if (row < GRID_SIZE - 1 && onFire[row + 1][col]) extinguishFire(row + 1, col); // Down
        else if (col > 0 && onFire[row][col - 1]) extinguishFire(row, col - 1); // Left
        else if (col < GRID_SIZE - 1 && onFire[row][col + 1]) extinguishFire(row, col + 1); // Right
    }

    // Spread fire to adjacent buildings
    private void spreadFire() {
        boolean[][] newFireStatus = new boolean[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (onFire[i][j]) {
                    // Fire spreads to adjacent buildings (up, down, left, right)
                    if (i > 0 && !onFire[i - 1][j]) newFireStatus[i - 1][j] = true;
                    if (i < GRID_SIZE - 1 && !onFire[i + 1][j]) newFireStatus[i + 1][j] = true;
                    if (j > 0 && !onFire[i][j - 1]) newFireStatus[i][j - 1] = true;
                    if (j < GRID_SIZE - 1 && !onFire[i][j + 1]) newFireStatus[i][j + 1] = true;
                }
            }
        }

        // Update fire status and button styles
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (newFireStatus[i][j]) {
                    igniteFire(i, j);
                }
            }
        }

        checkGameEnd(Main.getPrimaryStage());
    }

    // Start a timer to spread fire every few seconds
    private void startFireSpreadTimer() {
        fireSpreadTimer = new Timer();
        fireSpreadTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> spreadFire());
            }
        }, 3000, 3000); // Spread fire every 3 seconds
    }

    // Check if the game is over
    private void checkGameEnd(Stage stage) {
        boolean allOnFire = areAllBuildingsOnFire();
        boolean allExtinguished = areAllBuildingsExtinguished();

        if (allOnFire) {
            fireSpreadTimer.cancel();
            if (currentPet != null) {
                currentPet.decreaseHealth(50); // Pet loses 50 health if all buildings are on fire
                System.out.println(currentPet.getName() + " lost 50 health. Current health: " + currentPet.getHealth()); // Debug print
            }
            showGameOverPopup(stage, "All buildings are on fire! You lost 50 health.");
        } else if (allExtinguished) {
            fireSpreadTimer.cancel();
            showGameOverPopup(stage, "Congratulations! You extinguished all the fires.");
        }
    }

    // Check if all buildings are on fire
    private boolean areAllBuildingsOnFire() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!onFire[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Check if all buildings are extinguished
    private boolean areAllBuildingsExtinguished() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (onFire[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Show game over popup
    private void showGameOverPopup(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (!areAllBuildingsOnFire()) {
            CurrencyManager.addCurrency(score); // Only add currency if the player wins
        }
        Main.getPrimaryStage().setScene(ScreenManager.getStartMenu());
    }

    @Override
    public int play() {
        return score;
    }
}
