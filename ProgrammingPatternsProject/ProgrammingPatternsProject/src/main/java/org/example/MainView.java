package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * It is the View class of the MVC architecture. It takes care of displaying the GUI window.
 */
public class MainView extends Application {

    /**
     * To initiate the GUI window by calling MainView.fxml
     * @param primaryStage the stage of the sindow
     * @throws Exception If an error occurs while trying to open the window
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent appWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(new Scene(appWindow));
        primaryStage.show();
    }

    /**
     * Launches the GUI window.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
