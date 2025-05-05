/*
// -------------------------------------------------------
Final Project
Written by: Mariam Salim #6229528 and Meerab Khan #2388387

Mariam's Part:
-Helped with Deliverable 1 and made a GitHub repository
-Made db file with Meerab
-Made the Singleton for the database (db)
-Made the Factory for language switching
-Made the model classes' content
-Made most of the DBManager's methods (tables and methods related to the database)
-Made the Staff Window(layout, controls, methods,
 translation and handling error messages with a popup depending on the methods' return from DBManager)
-Made the "Resource Bundle 'messages'" with translations
-Made the window switching from staff window to client window.
-Made the final report


Meerab's part:
-Most of Deliverable 1
-Made db file with Mariam
-Created model classes' structure (initiate java classes)
-Made the sign in window and its controls and connected it to the client window
-Added controls to the client window and added some GUI methods
-Made Junit testing
-Made login table with client ID and password (TODO)
-Handled login feature (TODO: to complete)
-Made the final report

// For "Programming Patterns" Section 2- Winter 2025
// --------------------------------------------------------
- General explanation:
The Hotel Management System is a small application that allows hotel staff (receptionists and managers)
and guests to interact with the system. Receptionists can add new room details, check room availability,
books rooms, check guests in and out, and view booking records. Guests can search for available rooms based
on type (single, double, suite, etc.) or price, book a room, and view their booking history.
 */

package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is the GUI controller for the JavaFX view MainView.java .
 * It calls all the GUI components from the .fxml files to handle event actions and all the
 * user interface functions.
 */
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class GUIcontroller {
    // Buttons
    @FXML private Button addClientBtn;
    @FXML private Button addRoomBtn;
    @FXML private Button bookRoomBtn;
    @FXML private Button checkoutClientBtn;
    @FXML private Button clientLoginBtn;
//    @FXML private Button deleteClientBtn;
    @FXML private Button deleteRoomBtn;
    @FXML private Button searchForClientBtn;
    @FXML private Button searchForRoomBtn;
    @FXML private Button staffLoginBtn;
    @FXML private Button updateRoomBtn;
    @FXML private Button viewAllBookingsBtn;
    @FXML private Button viewAllClientsBtn;
    @FXML private Button viewAllRoomsBtn;
    @FXML private Button viewAvailableRoomsBtn;
    @FXML private Button viewCurrentClientsBtn;

    // TextFields
    @FXML private TextField bookingNumField;
    @FXML private TextField clientContactField;
    @FXML private TextField clientIdField;
    @FXML private TextField clientNameField;
    @FXML private TextField numOfMembersField;
    @FXML private TextField roomNoField;
    @FXML private TextField roomPriceField;

    // Labels
    @FXML private Label bookingNumLabel;
    @FXML private Label bookingStartDateLabel;
    @FXML private Label clientContactLabel;
    @FXML private Label clientIdLabel;
    @FXML private Label clientNameLabel;
//    @FXML private Label displayTableLabel;
    @FXML private Label isInHotelLabel;
    @FXML private Label numOfMembersLabel;
    @FXML private Label roomAvailabilityLabel;
    @FXML private Label roomNumLabel;
    @FXML private Label roomPriceLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label welcomeLabel;

    // ComboBoxes
    @FXML private ComboBox<String> availabilityComboBox;
    @FXML private ComboBox<String> isInHotelComboBox;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private ComboBox<String> roomTypeComboBox;

    // DatePicker
    @FXML private DatePicker bookingStartDatePicker;

    // TableColumns
    @FXML private TableColumn column1;
    @FXML private TableColumn column2;
    @FXML private TableColumn column3;
    @FXML private TableColumn column4;
    @FXML private TableColumn column5;

    // TableView


    @FXML private TableView tableView; // I left the data type ambiguous, so that we can change it dynamically.

    private String selectedLanguage = "english"; //Default
    MessageService messageService;
    private DBManager dbManager;

    @FXML
    private Button clientSignInBtn;
    @FXML
    private Label userIDLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button searchByTypeBtn;

    @FXML
    private Button viewPastBookingsBtn;
    @FXML
    private Label partySizeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Button searchByPriceBtn;

    /**
     * The constructor of the GUI controller.
     * It makes an object of DBManager to access its methods to interact with the database.
     */
    public GUIcontroller() {
        dbManager = new DBManager();
        messageService = new MessageService();
    }


    /**
     * It initializes everything for the main window (the staff's window) to open.
     * Everything is in English at first, but the user can change the language.
     */
    @FXML
    public void initialize() {
        languageComboBox.getItems().addAll("English", "Français");
        languageComboBox.setValue("English"); //default
        isInHotelComboBox.getItems().addAll(translate("comboBoxTrue"), translate("comboBoxFalse"));
        isInHotelComboBox.setValue(translate("comboBoxTrue")); //default
        roomTypeComboBox.getItems().addAll(
                translate("roomTypeComboBoxSingle"),
                translate("roomTypeComboBoxDouble"),
                translate("roomTypeComboBoxTwin"),
                translate("roomTypeComboBoxQueen"),
                translate("roomTypeComboBoxSuite"),
                translate("roomTypeComboBoxBig_Family"));//Capacity: 1, 2, 2, 2, 4
        roomTypeComboBox.setValue(translate("roomTypeComboBoxSingle")); //default
        availabilityComboBox.getItems().addAll(translate("comboBoxTrue"), translate("comboBoxFalse"));
        availabilityComboBox.setValue(translate("comboBoxTrue")); //default
        //I did not know how to do the following block of code to restrict DatePicker values, so I referenced online sources:
        bookingStartDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(LocalDate.now()) || item.isAfter(LocalDate.now().plusDays(60)));
            }
        });
    }

    /**
     * It handles switching from the client window to the staff window.
     * @throws IOException error for failing to open the new window.
     */
    @FXML
    public void handleStaffLoginButton() throws IOException {
        //1) close previous window:
        Stage prevStage = (Stage) staffLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        primaryStage.setTitle(translate("staffWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * It handles switching from the staff window to the client window.
     * @throws IOException error for failing to open the new window.
     */
    @FXML
    public void handleClientLoginButton() throws IOException {
        //1) close previous window:
        Stage prevStage = (Stage) clientLoginBtn.getScene().getWindow();
        prevStage.close();
        //2) open new window for the client:
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ClientWindow.fxml")));
        primaryStage.setTitle(translate("clientWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Handles the event where the button to search room
     * by type is pressed to view all the rooms that have the same type the user wants.
     */
    @FXML
    public void handleSearchByRoomType() {
        try
        {
            // get selected room type
            String selected = roomTypeComboBox.getValue();

            //if we don't have a value for room type
            if (selected == null || selected.isEmpty())
            {
                showAlert("Error", translate("noRoomTypeSelected"));
                return;
            }

            //console display
//            System.out.println("Selected room type: " + selected);

            // convert selected room type back to English for the db
            String englishRoomType = messageService.useLangService("english", "roomTypeComboBox" + selected);

            //after translated it displays in english first
//            System.out.println("Translated to English: " + englishRoomType);

            // fetch rooms
            List<Room> rooms = dbManager.findRoomByType(englishRoomType);

            //check if the room is empty
            if (rooms == null || rooms.isEmpty())
            {
                showAlert("Error", translate("noRoomsFound"));
                return;
            }

            // update column headers
//            column1.setText(translate("roomNum"));
//            column1.setCellValueFactory(new PropertyValueFactory<>("roomNum"));
//
//            column2.setText(translate("roomType"));
//            column2.setCellValueFactory(new PropertyValueFactory<>("roomType"));
//
//            column3.setText(translate("pricePerNight"));
//            column3.setCellValueFactory(new PropertyValueFactory<>("price"));
//
//            column4.setText(translate("available"));
//            column4.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));
//
//            column5.setText(translate("addedDate"));
//            column5.setCellValueFactory(new PropertyValueFactory<>("addedDate"));

            // translate fields for display
            for (Room room : rooms)
            {
                //to display the combo boxes translating them for each room since it cant have multiple type choice
                room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
                room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
            }
//          //clear the table after display each time
//            tableView.getItems().clear();
//
//            //after displaying add items to table
//            tableView.getItems().addAll(rooms);
            roomsDisplay(rooms);
        }
        catch (Exception e)
        {
            showAlert("Error", translate("unexpectedError"));
        }
    }

    /**
     * handles the sign in of the client to go from staff to client window when the button is pressed.
     * @throws IOException
     */
    @FXML
    public void handleSignIn() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ClientView.fxml")));
        primaryStage.setTitle(translate("clientWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    /**
     * When the associated button is pressed, the rooms will be sorted by price.
     */
    @FXML
    public void handleSortByPrice() {
        try
        {
            //translate from the English db:
            List<Room> rooms = dbManager.findRoomLowToHighPrice();
            rooms.forEach(room ->
                    room.setIsAvailable(translate("comboBox" + room.getIsAvailable()))
            );
            rooms.forEach(room -> room.setRoomType(translate("roomTypeComboBox" + room.getRoomType())));

            roomsDisplay(rooms);

        } catch (Exception e) {
            showAlert("Error", translate("unexpectedError"));
        }
    }

    /**
     * Views all the bookings the client has made when the associated button is pressed.
     */
    @FXML
    private void handleViewPastBookingBtn() throws IOException{
        try {
            int clientId = Integer.parseInt(clientIdField.getText());

//            // Set up table columns for Booking view
//            column1.setText(translate("bookingNumCol"));
//            column1.setCellValueFactory(new PropertyValueFactory<>("bookingNum"));
//
//            column2.setText(translate("clientIdCol"));
//            column2.setCellValueFactory(new PropertyValueFactory<>("clientId"));
//
//            column3.setText(translate("roomNum"));
//            column3.setCellValueFactory(new PropertyValueFactory<>("roomNum"));
//
//            column4.setText(translate("startDateCol"));
//            column4.setCellValueFactory(new PropertyValueFactory<>("startDate"));
//
//            column5.setText(translate("endDateCol"));
//            column5.setCellValueFactory(new PropertyValueFactory<>("endDate"));

            // Get all bookings and filter them using the client ID
            List<Booking> bookings = dbManager.selectJsonBookings();
            List<Booking> clientBookings = bookings.stream()
                    .filter(b -> b.getClientId() == clientId)
                    .toList();

            if (clientBookings.isEmpty()) {
                showAlert("Error", translate("noClientBooking") + clientId);
            }

//            tableView.getItems().clear();
//            tableView.getItems().addAll(clientBookings);
            bookingsDisplay(clientBookings);

        } catch (Exception e) {
            showAlert("Error",translate( "cannotFindBookings"));
        }
    }

    /**
     * Updates the GUI controls to conform to the user's chosen language for the staff window.
     *  The selectedLanguage is the user's chosen language, English or French.
     */
    @FXML
    private void staffLanguageUpdate() {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        //Update window title
        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(translate("clientWinTitle"));

        //Organize GUI Components for translation:
        Button[] staffButtons = {
                addClientBtn, addRoomBtn, bookRoomBtn, checkoutClientBtn,
                clientLoginBtn, deleteRoomBtn, searchForClientBtn,
                searchForRoomBtn, updateRoomBtn, viewAllBookingsBtn,
                viewAllClientsBtn, viewAllRoomsBtn, viewAvailableRoomsBtn, viewCurrentClientsBtn
        };

        Label[] staffLabels = {
                bookingNumLabel, bookingStartDateLabel, clientContactLabel, clientIdLabel,
                clientNameLabel, isInHotelLabel, numOfMembersLabel,
                roomAvailabilityLabel, roomNumLabel, roomPriceLabel, roomTypeLabel, welcomeLabel
        };

        //Update GUI elements:
        for (Button button : staffButtons) {
                button.setText(translate(button.getId()));
        }

        for (Label label : staffLabels) {
            label.setText(translate(label.getId()));
        }

        isInHotelComboBox.getItems().setAll(translate("comboBoxTrue"), translate("comboBoxFalse"));

        roomTypeComboBox.getItems().setAll(
                translate("roomTypeComboBoxSingle"),
                translate("roomTypeComboBoxDouble"),
                translate("roomTypeComboBoxTwin"),
                translate("roomTypeComboBoxQueen"),
                translate("roomTypeComboBoxSuite"),
                translate("roomTypeComboBoxBig_Family")
        );

        availabilityComboBox.getItems().setAll(translate("comboBoxTrue"), translate("comboBoxFalse"));

        //Reset empty table:
        tableView.getItems().clear();
        TableColumn[] cols = {column1, column2, column3, column4, column5};
        for (TableColumn column : cols)
            column.setText("");
    }

    /**
     * Updates the GUI controls to conform to the user's chosen language.
     * The selectedLanguage is the user's chosen language, English or French.
     */
    @FXML
    private void clientLanguageUpdate() {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(translate("clientWinTitle"));
        Button[] clientButtons = {clientSignInBtn,staffLoginBtn
        };
        Label[] clientLabels = {
                userIDLabel ,passwordLabel, welcomeLabel
        };

        //Update GUI elements:
        for (Button button : clientButtons) {
            button.setText(translate(button.getId()));
        }

        for (Label label : clientLabels) {
            label.setText(translate(label.getId()));
        }
    }

    /**
     * Updates the GUI controls to conform to the user's chosen language for the client window.
     * The selectedLanguage is the user's chosen language, English or French.
     */
    @FXML
    private void clientLanguageUpdate2()
    {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        // Update window title
        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(translate("clientWinTitle"));

        // Organize GUI Components for translation:
        Button[] clientButtons = {
                searchByTypeBtn, searchByPriceBtn, viewAllRoomsBtn, viewPastBookingsBtn,
                bookRoomBtn
        };

        Label[] clientLabels = {
                partySizeLabel, nameLabel, bookingStartDateLabel, idLabel, roomNumLabel, roomTypeLabel, welcomeLabel
        };

        // Update GUI elements:
        for (Button button : clientButtons) {
            button.setText(translate(button.getId()));
        }

        for (Label label : clientLabels) {
            label.setText(translate(label.getId()));
        }

        roomTypeComboBox.getItems().setAll(
                translate("roomTypeComboBoxSingle"),
                translate("roomTypeComboBoxDouble"),
                translate("roomTypeComboBoxTwin"),
                translate("roomTypeComboBoxQueen"),
                translate("roomTypeComboBoxSuite"),
                translate("roomTypeComboBoxBig_Family")
        );

        tableView.getItems().clear();
        TableColumn[] cols = {column1, column2, column3, column4, column5};
        for (TableColumn column : cols)
            column.setText("");


    }

    /**
     * When the associated button is pressed, the client will check out from the room using the booking number.
     */
    @FXML
    private void handleCheckoutClientBtn(){
        try {
            int bookingNum = Integer.parseInt(bookingNumField.getText());

            String actionAndResult = dbManager.completeBooking(bookingNum);

            if (!actionAndResult.isEmpty())
                throw new IllegalArgumentException(actionAndResult);
            handleViewAllBookingsBtn();
        } catch (Exception e) {
            if (!e.getMessage().isEmpty())
                showAlert("Error", translate("checkoutError"));
            else
                showAlert("Error", translate(e.getMessage()));
        }
    }
    /**
     * When the associated button is pressed, a room that matches the provided
     * room number will be booked for the client
     * with the id that is provided in the clientId text field as of the date that is selected in the date picker.
     */
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
            if (!e.getMessage().isEmpty())
                showAlert("Error", translate("bookError"));
            else
                showAlert("Error", translate(e.getMessage()));
        }
    }
    /**
     * When the associated button is pressed, the room will be updated according to fields for price and availability of the room.
     */
    @FXML
    private void handleUpdateRoomBtn() {
        try {
            int roomNum = Integer.parseInt(roomNoField.getText());
            //db is in english, so translate availability:
            String isAvailable = messageService.useLangService("english", "comboBox" + availabilityComboBox.getValue());
            double price;
            Room room = dbManager.findRoom(roomNum);

            if (room == null) {
                showAlert("Error", translate("searchRoomError"));
                return;
            }

            if (roomPriceField.getText().isEmpty()) {//price not updated
                price = room.getPrice();//keep price
            } else {
                price = Double.parseDouble(roomPriceField.getText());
            }

            String actionAndResult = dbManager.updateRoom(roomNum, price, isAvailable);

            if (!actionAndResult.isEmpty())
                throw new IllegalArgumentException(actionAndResult);
            handleSearchForRoomBtn();

            //Reset GUI components:
            roomNoField.clear();
            roomPriceField.clear();
            availabilityComboBox.setValue(translate("comboBoxTrue"));
        } catch (IllegalArgumentException e) {
            showAlert("Error", translate("updateRoomError"));
        }
    }
    /**
     * When the associated button is pressed, a client is added with the information provided in the fields.
     */
    @FXML
    private void handleAddClientBtn(){
        try {
            String name = clientNameField.getText();
            String contact = clientContactField.getText();
            int numOfMembers = Integer.parseInt(numOfMembersField.getText());
            String isInHotel = messageService.useLangService("english", "comboBox" + isInHotelComboBox.getValue());

            boolean conditionAndAction = dbManager.insertClientRecord(name, contact, numOfMembers, isInHotel);
            if (!conditionAndAction)//If failed to add client
            {
                throw new IllegalArgumentException();
            } else {
                clientNameField.clear();
                clientContactField.clear();
                numOfMembersField.clear();
                isInHotelComboBox.setValue(translate("comboBoxTrue"));
                handleViewAllClientsBtn();
            }
        } catch (IllegalArgumentException e) {
            showAlert( "Error", translate("dataTypeError"));
        }
    }
    /**
     * When the associated button is pressed, all the available rooms in the database will be displayed in the table view.
     */
    @FXML
    private void handleViewAvailableRoomsBtn(){

        //Translate from the English db:
        List<Room> rooms = dbManager.selectAvailableRooms();
        rooms.forEach(room -> {
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
            room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
        });

        roomsDisplay(rooms);
    }
    /**
     * When the associated button is pressed, all the clients that are in the hotel will be displayed in the table view.
     */
    @FXML
    private void handleViewCurrentClientsBtn(){
        List<Client> clients = dbManager.selectCurrentClients();
        clients.forEach(room ->
                room.setIsInHotel(translate("comboBox" + room.getIsInHotel()))
        );

        clientsDisplay(clients);
    }
    /**
     * When the associated button is pressed, all the clients in the database will be displayed in the table view.
     */
    @FXML
    private void handleViewAllClientsBtn(){

        //Translate true/false
        List<Client> clients = dbManager.selectJsonClients();
        clients.forEach(room ->
                room.setIsInHotel(translate("comboBox" + room.getIsInHotel()))
        );

        clientsDisplay(clients);
    }
    /**
     * When the associated button is pressed, all the rooms in the database will be displayed in the table view.
     */
    @FXML
    public void handleViewAllRoomsBtn() {

        //Translate from the English db:
        List<Room> rooms = dbManager.selectJsonRooms();
        rooms.forEach(room -> {
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
            room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
        });
        roomsDisplay(rooms);
    }

    /**
     * When the associated button is pressed, all the bookings in the database will be displayed in the table view.
     */
    @FXML
    private void handleViewAllBookingsBtn(){
//        tableView.getItems().clear();
//
//        column1.setText(translate("bookingNumCol"));
//        column1.setCellValueFactory(new PropertyValueFactory<>("bookingNum"));
//
//        column2.setText(translate("clientIdCol"));
//        column2.setCellValueFactory(new PropertyValueFactory<>("clientId"));
//
//        column3.setText(translate("roomNum"));
//        column3.setCellValueFactory(new PropertyValueFactory<>("roomNum"));
//
//        column4.setText(translate("startDateCol"));
//        column4.setCellValueFactory(new PropertyValueFactory<>("startDate"));
//
//        column5.setText(translate("endDateCol"));
//        column5.setCellValueFactory(new PropertyValueFactory<>("endDate"));
//
//        List<Booking> bookings = dbManager.selectJsonBookings();
//
//        bookings.forEach(booking -> {
//            booking.setStartDate(messageService.useDateLangService(selectedLanguage, LocalDate.parse(booking.getStartDate())));
//            booking.setEndDate(messageService.useDateLangService(selectedLanguage, LocalDate.parse(booking.getEndDate())));
//        });
//
//        tableView.getItems().addAll(bookings);
        bookingsDisplay(dbManager.selectJsonBookings());
    }

    /**
     * When the associated button is pressed, a room is added with the provided information in the fields.
     */
    @FXML
    private void handleAddRoomBtn(){
        try {
            int roomNum = Integer.parseInt(roomNoField.getText());
            String roomType = messageService.useLangService("english", "roomTypeComboBox" + roomTypeComboBox.getValue());
            double price = Double.parseDouble(roomPriceField.getText());
            String isAvailable = messageService.useLangService("english", "comboBox" + availabilityComboBox.getValue());//db is in English

            boolean conditionAndAction = dbManager.insertRoomRecord(roomNum, roomType, price, isAvailable);
            if (!conditionAndAction)//If failed to add client
            {
                throw new IllegalArgumentException();
            } else {
                roomNoField.clear();
                roomPriceField.clear();
                isInHotelComboBox.setValue(translate("comboBoxTrue"));//back to default
                availabilityComboBox.setValue(translate("comboBoxTrue"));//back to default
                handleViewAllRoomsBtn();
            }
        } catch (IllegalArgumentException e) {
            showAlert( "Error", translate("dataTypeError"));
        }
    }

    /**
     * When the associated button is pressed, the client with the ID provided in the field will be displayed.
     */
    @FXML
    private void handleSearchForClientBtn(){
        try {
            int id = Integer.parseInt(clientIdField.getText());

            Client client = dbManager.findClient(id);

            if (client == null) {
                throw new IllegalArgumentException();
            }
            List<Client> c = new ArrayList<>();
            c.add(client);

            clientsDisplay(c);
            clientIdField.clear();
        } catch (IllegalArgumentException e) {
            showAlert("Error", translate("searchClientError"));
        }
    }

    /**
     * When the associated button is pressed, the room matching the room number provided in the field will be displayed.
     */
    @FXML
    private void handleSearchForRoomBtn(){
        try {
            int roomNum = Integer.parseInt(roomNoField.getText());

            Room room = dbManager.findRoom(roomNum);
            if (room == null) {
                throw new IllegalArgumentException();
            }
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
            List<Room> rooms = new ArrayList<>();
            rooms.add(room);
            roomsDisplay(rooms);
            roomNoField.clear();
        } catch (Exception e) {
            showAlert("Error", translate("searchRoomError"));
        }
    }

    /**
     * When the associated button is pressed, the room matching the
     * room number provided in the field will be removed from the database.
     */
    @FXML
    private void handleDeleteRoomBtn(){
        try {
            int id = Integer.parseInt(roomNoField.getText());

            dbManager.deleteRow("rooms", "roomNum", id);
            handleViewAllRoomsBtn();
        } catch (Exception e) {
            showAlert("Error", translate("deleteRoomError"));
        }
    }

    /**
     * When the associated button is pressed, the client matching the
     * client ID provided in the field will be removed from the database.
     */
//    @FXML
//    private void handleDeleteClientBtn(){
//        try {
//            int id = Integer.parseInt(clientIdField.getText());
//
//            dbManager.deleteRow("clients", "id", id);
//            handleViewAllClientsBtn();
//        } catch (Exception e) {
//            showAlert("Error", translate("deleteClientError"));
//        }
//    }    Removed the method because it does not make sense to remove a client.
//         He is just not in the hotel anymore, but we cannot erase that he was once a client.

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
     * A helper method to translate strings into the chosen language of the user.
     * @param msgCategory the message category to look for translation
     * @return a translated string
     */
    private String translate(String msgCategory) {
        return messageService.useLangService(selectedLanguage, msgCategory);
    }

    /**
     * helper method to display a list of clients in the table view.
     * @param clients the list of clients to display.
     */
    private void clientsDisplay(List<Client> clients) {
        tableView.getItems().clear();

        column1.setText(translate("clientIdCol"));
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        column2.setText(translate("nameCol"));
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        column3.setText("Contact");
        column3.setCellValueFactory(new PropertyValueFactory<>("contact"));

        column4.setText(translate("size"));
        column4.setCellValueFactory(new PropertyValueFactory<>("numOfMembers"));

        column5.setText(translate("inHotel"));
        column5.setCellValueFactory(new PropertyValueFactory<>("isInHotel"));

        tableView.getItems().addAll(clients);
    }

    private void roomsDisplay(List<Room> rooms) {
        tableView.getItems().clear();

        column1.setText(translate("roomNum"));
        column1.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column2.setText(translate("roomType"));
        column2.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        column3.setText((translate("pricePerNight")));
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));

        column4.setText((translate("available")));
        column4.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));

        column5.setText(translate("addedDate"));
        column5.setCellValueFactory(new PropertyValueFactory<>("addedDate"));

        rooms.forEach(room -> room.setAddedDate(messageService.useDateLangService(selectedLanguage, LocalDate.parse(room.getAddedDate()))));

        tableView.getItems().addAll(rooms);
    }

    private void bookingsDisplay(List<Booking> bookings) {
        tableView.getItems().clear();

        column1.setText(translate("bookingNumCol"));
        column1.setCellValueFactory(new PropertyValueFactory<>("bookingNum"));

        column2.setText(translate("clientIdCol"));
        column2.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        column3.setText(translate("roomNum"));
        column3.setCellValueFactory(new PropertyValueFactory<>("roomNum"));

        column4.setText(translate("startDateCol"));
        column4.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        column5.setText(translate("endDateCol"));
        column5.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        bookings.forEach(booking -> {
            booking.setStartDate(messageService.useDateLangService(selectedLanguage, LocalDate.parse(booking.getStartDate())));
            if (booking.getEndDate() !=null && !booking.getEndDate().isEmpty())
                booking.setEndDate(messageService.useDateLangService(selectedLanguage, LocalDate.parse(booking.getEndDate())));
        });

        tableView.getItems().addAll(bookings);
    }
}