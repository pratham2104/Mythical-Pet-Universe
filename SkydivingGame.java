import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;

public class SkydivingGame extends MiniGame {
    private int score = 0;
    private int round = 1;
    private final int totalRounds = 5;
    private Random rand = new Random();

    // Play game scene where the pet plays
    public Scene playGame(Stage stage, ExoticPet pet) {
        // Introduction Scene with Center-Aligned Text
        BorderPane introRoot = new BorderPane();
        Text introText = new Text("Welcome to Skydiving Game!\n"
                + pet.getName() + " is going skydiving!\n"
                + "Try your luck out.\n"
                + "Help " + pet.getName() + " avoid obstacles as they fall by going left or right.\n"
                + "Click 'OK' to start.");
        Button startButton = new Button("OK");

        // Wrap introText in a VBox for center alignment
        VBox introBox = new VBox(20, introText);
        introBox.setAlignment(Pos.CENTER);

        startButton.setOnAction(e -> stage.setScene(createGameScene(stage, pet)));

        introRoot.setCenter(introBox);  // Set introBox in the center of the BorderPane
        introRoot.setBottom(startButton);
        BorderPane.setAlignment(startButton, Pos.CENTER);

        return new Scene(introRoot, 600, 400);
    }

    // Create the main game scene where the pet can dodge obstacles
    private Scene createGameScene(Stage stage, ExoticPet pet) {
        BorderPane root = new BorderPane();
        Text instructionText = new Text(pet.getName() + " is trying to avoid obstacles while skydiving!");
        Text roundText = new Text("Round 1: Get ready to help " + pet.getName() + " dodge!");
        Text feedbackText = new Text(); // Text for feedback right below the round
        Text totalScoreText = new Text("Total Score: 0"); // Text showing the total score at the top right

        // Buttons for player choices
        Button leftButton = new Button("Go Left");
        Button rightButton = new Button("Go Right");

        // Action when Left button is pressed
        leftButton.setOnAction(e -> handlePlayerChoice(stage, pet, "L", roundText, feedbackText, totalScoreText));

        // Action when Right button is pressed
        rightButton.setOnAction(e -> handlePlayerChoice(stage, pet, "R", roundText, feedbackText, totalScoreText));

        // Layout for buttons
        HBox buttonBox = new HBox(20, leftButton, rightButton);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER); // Align buttons in the center

        // Round information including feedback
        VBox roundInfoBox = new VBox(10, roundText, feedbackText);
        roundInfoBox.setAlignment(Pos.CENTER); // Align round info in the center

        // Set components in the layout
        root.setTop(totalScoreText); // Show the total score at the top
        BorderPane.setAlignment(totalScoreText, Pos.TOP_RIGHT);
        root.setCenter(roundInfoBox);
        root.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        return new Scene(root, 600, 400);
    }

    // Handles pet choice and updates the game state
    private void handlePlayerChoice(Stage stage, ExoticPet pet, String playerChoice, Text roundText, Text feedbackText, Text totalScoreText) {
        if (round > totalRounds) {
            return;
        }

        // Randomly decide where the obstacle appears
        String obstacleSide = rand.nextBoolean() ? "left" : "right";
        String correctChoice = obstacleSide.equals("left") ? "R" : "L";

        // Check if pet dodged correctly
        if (playerChoice.equals(correctChoice)) {
            int reward = pet.getAffinity().equalsIgnoreCase("wind") ? 50 : 10; // Wind pets get 50 coins per dodge
            score += reward;
            feedbackText.setText(pet.getName() + " dodged the obstacle! +" + reward + " coins.");
        } else {
            feedbackText.setText(pet.getName() + " hit an obstacle from the " + obstacleSide + " side! No coins this round.");
            pet.decreaseHealth(10); // Pet loses 10 health points if it hits an obstacle
            if (pet.getHealth() <= 0) {
                feedbackText.setText(feedbackText.getText() + "\n" + pet.getName() + " has no health left!");
            }
        }

        // Update the total score text
        totalScoreText.setText("Total Score: " + score);

        // Move to the next round or end the game
        round++;
        if (round > totalRounds) {
            endGame(stage, pet);
        } else {
            Platform.runLater(() -> roundText.setText("Round " + round + ": An obstacle is coming! Help " + pet.getName() + " dodge it!"));
        }
    }

    // Ends the game and returns to the main menu
    private void endGame(Stage stage, ExoticPet pet) {
        BorderPane root = new BorderPane();
        Text finalScoreText = new Text(pet.getName() + "'s skydiving is complete! " + pet.getName() + " earned a total of " + score + " coins.");

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            CurrencyManager.addCurrency(score);
            Main.getPrimaryStage().setScene(ScreenManager.getStartMenu());
        });

        root.setCenter(finalScoreText);
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        Scene endScene = new Scene(root, 600, 400);
        stage.setScene(endScene);
    }

    @Override
    public int play() {
        return score;
    }
}
