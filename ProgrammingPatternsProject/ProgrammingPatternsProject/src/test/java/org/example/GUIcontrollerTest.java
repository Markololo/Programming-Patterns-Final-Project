package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests methods from the GUIcontroller class.
 */
public class GUIcontrollerTest {
    private GUIcontroller controller;


    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        // This starts the JavaFX Platform
        Platform.startup(() -> {
        });
        Thread.sleep(500); // Give time to finish startup
    }

    @BeforeEach
    public void setUp() throws InterruptedException {
        Platform.runLater(() -> {
            controller = new GUIcontroller();

            // Set up GUI elements
            ComboBox<String> roomTypeComboBox = new ComboBox<>();
            TableView<Room> tableView = new TableView<>();
            controller.setRoomTypeComboBox(roomTypeComboBox);
            controller.setTableView(tableView);
            controller.setColumn1(new TableColumn<>("Room Number"));
            controller.setColumn2(new TableColumn<>("Room Type"));
            controller.setColumn3(new TableColumn<>("Price"));
            controller.setColumn4(new TableColumn<>("Availability"));
            controller.setColumn5(new TableColumn<>("Booking Date"));

            // Set mock DBManager
            controller.setDbManager(new DBManager() {
                @Override
                public List<Room> findRoomByType(String type) {
                    if ("Deluxe".equals(type)) {
                        return List.of(
                                new Room(228, "Double", 150.0, "True", "2024-05-2"),
                                new Room(229, "Double", 150.0, "True", "2024-05-3")
                        );
                    }
                    return new ArrayList<>();
                }

                @Override
                public List<Room> findRoomLowToHighPrice() {
                    return List.of(
                            new Room(345, "Single", 80.0, "True", "2024-05-2"),
                            new Room(140, "Suite", 150.0, "True", "2024-05-2"));
                }
            });
        });
// Wait for the JavaFX setup to complete
        Thread.sleep(500);
    }

    @Test
    public void testHandleSearchByRoomType() throws InterruptedException {
        Platform.runLater(() -> {
            controller.getRoomTypeComboBox().getItems().add("Deluxe");
            controller.getRoomTypeComboBox().setValue("Deluxe");
            controller.handleSearchByRoomType();
        });
// Wait for handleSearchByRoomType to finish
        Thread.sleep(500);

        List<Room> rooms = controller.getTableView().getItems();
        assertEquals(2, rooms.size());
        Room room = rooms.get(0);
        assertEquals(228, room.getRoomNum());
        assertEquals("Double", room.getRoomType());
    }
}





