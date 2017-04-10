package TilesGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View/MainLayout.fxml"));
        primaryStage.setTitle("Puzzle");
        primaryStage.setScene(new Scene(root, 720 , 1080));
        primaryStage.setMinHeight(1080);
        primaryStage.setMinWidth(720);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
