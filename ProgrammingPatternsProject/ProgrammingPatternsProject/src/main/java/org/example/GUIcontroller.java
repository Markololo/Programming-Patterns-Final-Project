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
    @FXML private Button deleteClientBtn;
    @FXML private Button deleteRoomBtn;
    @FXML private Button searchForClientBtn;
    @FXML private Button searchForRoomBtn;
    @FXML private Button staffLoginBtn;//
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

    public GUIcontroller() {
        dbManager = new DBManager();
        messageService = new MessageService();
    }

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

    @FXML
    public void handleSearchByRoomType() {


            try {
                String englishRoomType = roomTypeComboBox.getValue();  // Still English!

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

                List<Room> rooms = dbManager.findRoomByType(englishRoomType);

                if (rooms == null || rooms.isEmpty()) {
                    throw new IllegalArgumentException();
                }

                // Translate each room's fields before displaying
                for (Room room : rooms) {
                    room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
                    room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
                }

                tableView.getItems().addAll(rooms);

            } catch (Exception e) {
                showAlert("Error", translate("unexpectedError"));
            }
    }

    @FXML
    public void handleSignIn() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ClientView.fxml")));
        primaryStage.setTitle(translate("clientWinTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    public void handleSortByPrice() throws IOException {

        try {
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

            //Translate from the English db:
            List<Room> rooms = dbManager.findRoomLowToHighPrice();
            rooms.forEach(room ->
                    room.setIsAvailable(translate("comboBox" + room.getIsAvailable()))
            );
            rooms.forEach(room -> room.setRoomType(translate("roomTypeComboBox" + room.getRoomType())));

            tableView.getItems().setAll(rooms);

        } catch (Exception e) {
            showAlert("Error", translate("unexpectedError"));
        }
    }
    @FXML
    private void handleViewPastBookingBtn() throws IOException{
        try {
            int clientId = Integer.parseInt(clientIdField.getText());

            // Set up table columns for Booking view
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

            // Get all bookings and filter them using the client ID
            List<Booking> bookings = dbManager.selectJsonBookings();
            List<Booking> clientBookings = bookings.stream()
                    .filter(b -> b.getClientId() == clientId)
                    .toList();

            if (clientBookings.isEmpty()) {
                showAlert("Error", translate("noClientBooking") + clientId);
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(clientBookings);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid client ID format.");
        } catch (Exception e) {
            showAlert("Error", "Failed to load past bookings:\n" + e.getMessage());
        }
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
        stage.setTitle(translate("clientWinTitle"));

        //Organize GUI Components for translation:
        Button[] staffButtons = {
                addClientBtn, addRoomBtn, bookRoomBtn, checkoutClientBtn,
                clientLoginBtn, deleteClientBtn, deleteRoomBtn, searchForClientBtn,
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
     * updates the labels to conform to the user's chosen language
     * the selectedLanguage is the user's chosen language, English or French
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


    @FXML
    private void clientLanguageUpdate2()
    {
        // Get the selected language:
        selectedLanguage = languageComboBox.getValue();

        //Update window title
        Stage stage = (Stage) languageComboBox.getScene().getWindow();
        stage.setTitle(translate("clientWinTitle"));

        //Organize GUI Components for translation:
        Button[] clientButtons = {
                searchByTypeBtn, searchByPriceBtn, viewAllRoomsBtn, viewPastBookingsBtn,
                bookRoomBtn
        };

        Label[] clientLabels = {
                partySizeLabel,nameLabel,bookingStartDateLabel,idLabel, roomNumLabel,roomTypeLabel, welcomeLabel
        };

        //Update GUI elements:
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

        //Reset empty table:
        tableView.getItems().clear();
        TableColumn[] cols = {column1, column2, column3, column4, column5};
        for (TableColumn column : cols)
            column.setText("");

    }



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

    @FXML
    private void handleViewAvailableRoomsBtn(){
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

        //Translate from the English db:
        List<Room> rooms = dbManager.selectAvailableRooms();
        rooms.forEach(room -> {
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
            room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
        });

        tableView.getItems().addAll(rooms);
    }

    @FXML
    private void handleViewCurrentClientsBtn(){
        List<Client> clients = dbManager.selectCurrentClients();
        clients.forEach(room ->
                room.setIsInHotel(translate("comboBox" + room.getIsInHotel()))
        );

        clientsDisplay(clients);
    }

    @FXML
    private void handleViewAllClientsBtn(){

        //Translate true/false
        List<Client> clients = dbManager.selectJsonClients();
        clients.forEach(room ->
                room.setIsInHotel(translate("comboBox" + room.getIsInHotel()))
        );

        clientsDisplay(clients);
    }

    @FXML
    public void handleViewAllRoomsBtn() {
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

        //Translate from the English db:
        List<Room> rooms = dbManager.selectJsonRooms();
        rooms.forEach(room -> {
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
//            room.setRoomType(translate("roomTypeComboBox" + room.getRoomType()));
        });

        tableView.getItems().addAll(rooms);
    }

    @FXML
    private void handleViewAllBookingsBtn(){
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

        tableView.getItems().addAll(dbManager.selectJsonBookings());
    }

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
            room.setIsAvailable(translate("comboBox" + room.getIsAvailable()));
            tableView.getItems().add(room);
            roomNoField.clear();
        } catch (Exception e) {
            showAlert("Error", translate("searchRoomError"));
        }
    }

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

    @FXML
    private void handleDeleteClientBtn(){
        try {
            int id = Integer.parseInt(clientIdField.getText());

            dbManager.deleteRow("clients", "id", id);
            handleViewAllClientsBtn();
        } catch (Exception e) {
            showAlert("Error", translate("deleteClientError"));
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

    private String translate(String msgCategory) {
        return messageService.useLangService(selectedLanguage, msgCategory);
    }

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
//    private void translateRoomType(String type) {
//        switch (type) {
//            case "" : return messageService.useLangService(selectedLanguage, "roomTypeComboBoxBigFamily");;
//        }
//    }
}
