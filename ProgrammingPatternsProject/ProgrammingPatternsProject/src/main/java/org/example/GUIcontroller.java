package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIcontroller {
    @FXML
    private Button viewAllBookingsBtn;
    @FXML
    private Button viewAllClientsBtn;
    @FXML
    private Button viewAllRoomsBtn;
    @FXML
    private Button viewAvailableRoomsBtn;
    @FXML
    private Button addClientBtn;
    @FXML
    private Button viewCurrentClientsBtn;
    @FXML
    private Button searchForClientBtn;
    @FXML
    private Button checkoutClientBtn;
    @FXML
    private Button deleteClientBtn;
    @FXML
    private Button addRoomBtn;
    @FXML
    private Button searchForRoomBtn;
    @FXML
    private Button deleteRoomBtn;
    @FXML
    private TextField clientIdField;
    @FXML
    private TextField clientNameField;
    @FXML
    private TextField clientContactField;
    @FXML
    private TextField numOfMembersField;
    @FXML
    private TextField roomNoField;
    @FXML
    private TextField roomTypeField;
    @FXML
    private TextField roomPriceField;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private ComboBox<Boolean> availabilityComboBox;
    @FXML
    private Button clientLoginBtn;
    @FXML
    private Button staffLoginBtn;
    @FXML
    private Label displayTableLabel;
    @FXML
    private TableView tableView;//I left the data type ambiguous, so that we can change it dynamically.

    private String selectedLanguage = "english"; //Default
    ClientDBController clientController;
    StaffDBController staffDBController;
    MessageService messageService;

    public GUIcontroller() {
        clientController = new ClientDBController();
        staffDBController = new StaffDBController();
        messageService = new MessageService();
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
        primaryStage.setTitle(messageService.useLangService(selectedLanguage, "staffWinTitle"));
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
        primaryStage.setTitle(messageService.useLangService(selectedLanguage, "clientWinTitle"));
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
    private void staffLanguageUpdate() {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        //Update window title
        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(messageService.useLangService(selectedLanguage, "clientWinTitle"));

        //Update GUI elements
        //**Maybe we can use a foreach loop instead**
        welcomeLabel.setText(messageService.useLangService(selectedLanguage, "welcomeLabel"));
        clientLoginBtn.setText(messageService.useLangService(selectedLanguage, "clientLoginBtn"));
    }

    /**
     * updates the labels to conform to the user's chosen language
     * the selectedLanguage is the user's chosen language, English or French
     */
    @FXML
    private void clientLanguageUpdate() {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(messageService.useLangService(selectedLanguage, "clientWinTitle"));

        //**Maybe we can use a foreach loop instead**
        welcomeLabel.setText(messageService.useLangService(selectedLanguage, "welcomeLabel"));
        staffLoginBtn.setText(messageService.useLangService(selectedLanguage, "staffLoginBtn"));
    }

    @FXML
    public void handleViewAllRoomsBtn() {

    }

    @FXML
    private void handleViewAllBookingsBtn(){

    }

    @FXML
    private void handleViewAllClientsBtn(){

    }

    @FXML
    private void handleViewAvailableRoomsBtn(){

    }

    @FXML
    private void handleAddClientBtn(){

    }

    @FXML
    private void handleViewCurrentClientsBtn(){

    }

    @FXML
    private void handleSearchForClientBtn(){

    }

    @FXML
    private void handleCheckoutClientBtn(){

    }

    @FXML
    private void handleDeleteClientBtn(){

    }

    @FXML
    private void handleAddRoomBtn(){

    }

    @FXML
    private void handleSearchForRoomBtn(){

    }

    @FXML
    private void handleDeleteRoomBtn(){

    }
}
