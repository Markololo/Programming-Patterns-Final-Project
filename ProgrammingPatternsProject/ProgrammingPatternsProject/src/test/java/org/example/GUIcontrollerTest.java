package org.example;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GUIcontrollerTest {

    private GUIcontroller controller;

    @BeforeEach
    void setUp() {
        controller = new GUIcontroller();

        // Setup UI components
        controller.setRoomTypeComboBox(new ComboBox<>());
        controller.setTableView(new TableView<>());
        controller.setColumn1(new TableColumn<>());
        controller.setColumn2(new TableColumn<>());
        controller.setColumn3(new TableColumn<>());
        controller.setColumn4(new TableColumn<>());
        controller.setColumn5(new TableColumn<>());



        controller.setDbManager(new DBManager()
        {
            @Override
            public Room findRoomByType(String type) {
                if (type.equals("Deluxe")) {
                    return new Room(228, "Double", 150.0, "True", "2024-5-2");
                }
                return null;
            }

            @Override
            public java.util.List<Room> findRoomLowToHighPrice() {
                return java.util.List.of(
                        new Room(345, "Single", 80.0, "True", "2024-05-2"),
                        new Room(140, "Suite", 150.0, "True", "2024-05-2"));
            }
        });
    }







}