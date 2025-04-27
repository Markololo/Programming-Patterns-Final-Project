package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class GUIcontroller {
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> languageComboBox;

    private String selectedLanguage = "english"; //Default
    ClientController clientController;
    StaffController staffController;

    public GUIcontroller() {
        clientController = new ClientController();
        staffController = new StaffController();
    }

    @FXML
    public void initialize() {
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue("English"); //default
        System.out.println("Empty window initialized.");
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
