package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIcontrollerStaff {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Button clientLoginBtn;
    @FXML
    private Button staffLoginBtn;

    private String selectedLanguage = "english"; //Default
    ClientController clientController;
    StaffController staffController;

    public GUIcontrollerStaff() {
        clientController = new ClientController();
        staffController = new StaffController();
    }

    @FXML
    public void initialize() {
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue("English"); //default
        System.out.println("Empty staff window initialized.");
    }

    @FXML
    public void handleStaffLoginButton() throws IOException {
        //1) close previous window:
        Stage prevStage = (Stage) staffLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        primaryStage.setTitle("Staff Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    public void handleClientLoginButton() throws IOException {
        //1) close previous window:
        Stage prevStage = (Stage) clientLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ClientWindow.fxml")));
        primaryStage.setTitle("Client Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * To display popup messages to inform the user about something.
     * @param type The type of the message, like "Error", "Warning", etc.
     * @param msg The message to display
     */
    private void showAlert(String type, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    /**
     * updates the labels to conform to the user's chosen language
     * the selectedLanguage is the user's chosen language, English or French
     */
    @FXML
    private void languageUpdate() {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();
        MessageService messageService = new MessageService();
        welcomeLabel.setText(messageService.useLangService(selectedLanguage, "welcomeLabel"));
    }
}
