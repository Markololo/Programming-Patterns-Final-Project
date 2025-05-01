package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GUIcontroller {
    @FXML
    private Button bookRoomBtn;
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
    private TextField roomNoField;;
    @FXML
    private TextField roomPriceField;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private ComboBox<String> isInHotelComboBox;
    @FXML
    private ComboBox<String> roomTypeComboBox;
    @FXML
    private ComboBox<String> availabilityComboBox;
    @FXML
    private Button clientLoginBtn;
    @FXML
    private Button staffLoginBtn;
    @FXML
    private Label displayTableLabel;
    @FXML
    private Label isInHotelLabel;
    @FXML
    private DatePicker bookingStartDatePicker;
    @FXML
    private TableColumn column1;
    @FXML
    private TableColumn column2;
    @FXML
    private TableColumn column3;
    @FXML
    private TableColumn column4;
    @FXML
    private TableColumn column5;
    @FXML
    private TableView tableView;//I left the data type ambiguous, so that we can change it dynamically.
    @FXML
    private AnchorPane mainViewAnchorPane;

    private String selectedLanguage = "english"; //Default
    MessageService messageService;
    private DBManager dbManager;
    private boolean isInMainView = true;

    public GUIcontroller() {
        dbManager = new DBManager();
        messageService = new MessageService();
    }

//    @FXML
//    public void initialize() {
//        if (isInMainView) {
//            System.out.println("In Main View");
//            initializeStaffView();
//        } else
//            System.out.println("Not In Main View.");
//    }
//
//    public void initializeClientView() {
//        System.out.println("Client View.");
//    }
//
//    public void initializeStaffView() { //MainView.fxml
//        languageComboBox.getItems().addAll("English", "French");
//        languageComboBox.setValue("English"); //default
//        isInHotelComboBox.getItems().addAll("True", "False");
//        isInHotelComboBox.setValue("True"); //default
//        roomTypeComboBox.getItems().addAll("Single", "Double", "Twin", "Queen", "Suite");//Capacity: 1, 2, 2, 2, 4
//        roomTypeComboBox.setValue("Single"); //default
//        availabilityComboBox.getItems().addAll("True", "False");
//        availabilityComboBox.setValue("True"); //default
//        //I did not know how to do the following block of code to restrict DatePicker values, so I referenced online sources:
//        bookingStartDatePicker.setDayCellFactory(picker -> new DateCell() {
//            @Override
//            public void updateItem(LocalDate item, boolean empty) {
//                super.updateItem(item, empty);
//                setDisable(item.isBefore(LocalDate.now()) || item.isAfter(LocalDate.now().plusDays(60)));
//            }
//        });
//    }

    @FXML
    public void initialize() {
//        if (isInMainView) {
//            System.out.println("In Main View");
//        } else {
//            System.out.println("Not In Main View.");
//        }
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue("English"); //default
        isInHotelComboBox.getItems().addAll("True", "False");
        isInHotelComboBox.setValue("True"); //default
        roomTypeComboBox.getItems().addAll("Single", "Double", "Twin", "Queen", "Suite");//Capacity: 1, 2, 2, 2, 4
        roomTypeComboBox.setValue("Single"); //default
        availabilityComboBox.getItems().addAll("True", "False");
        availabilityComboBox.setValue("True"); //default
        //I did not know how to do the following block of code to restrict DatePicker values, so I referenced online sources:
        bookingStartDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(LocalDate.now()) || item.isAfter(LocalDate.now().plusDays(60)));
            }
        });
    }

//    public void initializeClientView() {
//        System.out.println("Client.");
//    }
//
//    public void initializeStaffView() {
//        System.out.println("Staff.");
//        languageComboBox.getItems().addAll("English", "French");
//        languageComboBox.setValue("English"); //default
//        isInHotelComboBox.getItems().addAll("True", "False");
//        isInHotelComboBox.setValue("True"); //default
//        roomTypeComboBox.getItems().addAll("Single", "Double", "Twin", "Queen", "Suite");//Capacity: 1, 2, 2, 2, 4
//        roomTypeComboBox.setValue("Single"); //default
//        availabilityComboBox.getItems().addAll("True", "False");
//        availabilityComboBox.setValue("True"); //default
//        //I did not know how to do the following block of code to restrict DatePicker values, so I referenced online sources:
//        bookingStartDatePicker.setDayCellFactory(picker -> new DateCell() {
//            @Override
//            public void updateItem(LocalDate item, boolean empty) {
//                super.updateItem(item, empty);
//                setDisable(item.isBefore(LocalDate.now()) || item.isAfter(LocalDate.now().plusDays(60)));
//            }
//        });
//    }

    @FXML
    public void handleStaffLoginButton() throws IOException {
        //1) close previous window:
        Stage prevStage = (Stage) staffLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        isInMainView = true;
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        primaryStage.setTitle(messageService.useLangService(selectedLanguage, "staffWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
//        initializeStaffView();
    }

    @FXML
    public void handleClientLoginButton() throws IOException {
        isInMainView = false;

        //1) close previous window:
        Stage prevStage = (Stage) clientLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        isInMainView = false;
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ClientWindow.fxml")));
        primaryStage.setTitle(messageService.useLangService(selectedLanguage, "clientWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        isInMainView = false;
//        initializeClientView();
    }
//            languageComboBox.getItems().addAll("English", "French");
//            languageComboBox.setValue("English"); //default
//            isInHotelComboBox.getItems().addAll("True", "False");
//            isInHotelComboBox.setValue("True"); //default
//            roomTypeComboBox.getItems().addAll("Single", "Double", "Twin", "Queen", "Suite");//Capacity: 1, 2, 2, 2, 4
//            roomTypeComboBox.setValue("Single"); //default
//            availabilityComboBox.getItems().addAll("True", "False");
//            availabilityComboBox.setValue("True"); //default
//            //I did not know how to do the following block of code to restrict DatePicker values, so I referenced online sources:
//            bookingStartDatePicker.setDayCellFactory(picker -> new DateCell() {
//                @Override
//                public void updateItem(LocalDate item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setDisable(item.isBefore(LocalDate.now()) || item.isAfter(LocalDate.now().plusDays(60)));
//                }
//            });

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
    private void handleViewAvailableRoomsBtn(){
        tableView.getItems().clear();

        column1.setText("Room Num.");
        column1.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column2.setText("Room Type");
        column2.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        column3.setText("Price Per Night ($)");
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));

        column4.setText("Available");
        column4.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));

        column5.setText("Added Date");
        column5.setCellValueFactory(new PropertyValueFactory<>("addedDate"));

        tableView.getItems().addAll(dbManager.selectAvailableRooms());
    }

    @FXML
    private void handleViewCurrentClientsBtn(){
        tableView.getItems().clear();

        column1.setText("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        column2.setText("Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        column3.setText("Contact");
        column3.setCellValueFactory(new PropertyValueFactory<>("contact"));

        column4.setText("Party Size");
        column4.setCellValueFactory(new PropertyValueFactory<>("numOfMembers"));

        column5.setText("Is In Hotel");
        column5.setCellValueFactory(new PropertyValueFactory<>("isInHotel"));

        tableView.getItems().addAll(dbManager.selectCurrentClients());
    }

    @FXML
    private void handleCheckoutClientBtn(){

    }

    @FXML
    private void handleBookRoomBtn() {
        try {
            int clientId = Integer.parseInt(clientIdField.getText());
            int roomNum = Integer.parseInt(roomNoField.getText());
            LocalDate startDate = bookingStartDatePicker.getValue();

            String conditionAndAction = dbManager.insertBookingRecord(clientId, roomNum, startDate);

            if (!conditionAndAction.isEmpty()) {
                throw new IllegalArgumentException(conditionAndAction);
            }
            handleViewAllBookingsBtn();
        } catch (Exception e) {
            showAlert("Error", "A problem occurred during booking request:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateRoomBtn() {

    }
    @FXML
    private void handleViewAllClientsBtn(){
        tableView.getItems().clear();

        column1.setText("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        column2.setText("Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        column3.setText("Contact");
        column3.setCellValueFactory(new PropertyValueFactory<>("contact"));

        column4.setText("Party Size");
        column4.setCellValueFactory(new PropertyValueFactory<>("numOfMembers"));

        column5.setText("Is In Hotel");
        column5.setCellValueFactory(new PropertyValueFactory<>("isInHotel"));

        tableView.getItems().addAll(dbManager.selectJsonClients());
    }

    @FXML
    private void handleAddClientBtn(){
        try {
            String name = clientNameField.getText();
            String contact = clientContactField.getText();
            int numOfMembers = Integer.parseInt(numOfMembersField.getText());
            String isInHotel = isInHotelComboBox.getValue();

            boolean conditionAndAction = dbManager.insertClientRecord(name, contact, numOfMembers, isInHotel);
            if (!conditionAndAction)//If failed to add client
            {
                throw new IllegalArgumentException();
            } else {
                clientNameField.clear();
                clientContactField.clear();
                numOfMembersField.clear();
                isInHotelComboBox.setValue("True");
                handleViewAllClientsBtn();
            }
        } catch (IllegalArgumentException e) {
            showAlert( "Error", "Please Enter The Correct Data Types In The Fields.");
        }
    }

    @FXML
    public void handleViewAllRoomsBtn() {
        tableView.getItems().clear();

        column1.setText("Room Num.");
        column1.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column2.setText("Room Type");
        column2.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        column3.setText("Price Per Night ($)");
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));

        column4.setText("Available");
        column4.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));

        column5.setText("Added Date");
        column5.setCellValueFactory(new PropertyValueFactory<>("addedDate"));

        tableView.getItems().addAll(dbManager.selectJsonRooms());
    }

    @FXML
    private void handleViewAllBookingsBtn(){
        tableView.getItems().clear();

        column1.setText("Booking Num.");
        column1.setCellValueFactory(new PropertyValueFactory<>("bookingNum"));

        column2.setText("Client ID");
        column2.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        column3.setText("Room Num.");
        column3.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column4.setText("Start Date");
        column4.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        column5.setText("End Date");
        column5.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        tableView.getItems().addAll(dbManager.selectJsonBookings());
    }

    @FXML
    private void handleDeleteClientBtn(){
        int id = Integer.parseInt(clientIdField.getText());

        dbManager.deleteRow("clients", "id", id);
        handleViewAllClientsBtn();
    }

    @FXML
    private void handleDeleteRoomBtn(){
        int id = Integer.parseInt(roomNoField.getText());

        dbManager.deleteRow("rooms", "roomNum", id);
        handleViewAllClientsBtn();
    }

    @FXML
    private void handleAddRoomBtn(){
        try {
            int roomNum = Integer.parseInt(roomNoField.getText());
            String roomType = roomTypeComboBox.getValue();
            double price = Double.parseDouble(roomPriceField.getText());
            String isAvailable = availabilityComboBox.getValue();

            boolean conditionAndAction = dbManager.insertRoomRecord(roomNum, roomType, price, isAvailable);
            if (!conditionAndAction)//If failed to add client
            {
                throw new IllegalArgumentException();
            } else {
                roomNoField.clear();
                roomPriceField.clear();
                isInHotelComboBox.setValue("True");//back to default
                availabilityComboBox.setValue("True");//back to default
                handleViewAllRoomsBtn();
            }
        } catch (IllegalArgumentException e) {
            showAlert( "Error", "Please Enter The Correct Data Types In The Fields.");
        }
    }

    @FXML
    private void handleSearchForClientBtn(){
        tableView.getItems().clear();

        int id = Integer.parseInt(clientIdField.getText());

        column1.setText("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        column2.setText("Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        column3.setText("Contact");
        column3.setCellValueFactory(new PropertyValueFactory<>("contact"));

        column4.setText("Party Size");
        column4.setCellValueFactory(new PropertyValueFactory<>("numOfMembers"));

        column5.setText("Is In Hotel");
        column5.setCellValueFactory(new PropertyValueFactory<>("isInHotel"));

        try {
            Client client = dbManager.findClient(id);
            if (client == null) {
                throw new IllegalArgumentException();
            }
            tableView.getItems().add(client);
            clientIdField.clear();
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Can't find the client you're looking for.");
        }
    }

    @FXML
    private void handleSearchForRoomBtn(){
        try {

        int roomNum = Integer.parseInt(roomNoField.getText());

        tableView.getItems().clear();

        column1.setText("Room Num.");
        column1.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column2.setText("Room Type");
        column2.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        column3.setText("Price Per Night ($)");
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));

        column4.setText("Available");
        column4.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));

        column5.setText("Added Date");
        column5.setCellValueFactory(new PropertyValueFactory<>("addedDate"));


            Room room = dbManager.findRoom(roomNum);
            if (room == null) {
                throw new IllegalArgumentException();
            }
            tableView.getItems().add(room);
            roomNoField.clear();
        } catch (Exception e) {
            showAlert("Error", "Can't find the room you're looking for!\nMake sure it exists.");
        }
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
}
