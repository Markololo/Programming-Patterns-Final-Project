package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent appWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(new Scene(appWindow));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
