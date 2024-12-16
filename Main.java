
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        PetManager.loadPets(); // Load pets from data
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Mythical Pet Universe");
        primaryStage.setScene(ScreenManager.getStartMenu());
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
