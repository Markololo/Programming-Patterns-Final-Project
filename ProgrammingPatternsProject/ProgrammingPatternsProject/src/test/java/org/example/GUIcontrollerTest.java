package org.example;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            public List<Room>findRoomByType(String type) {

                    List<Room> rooms = new ArrayList<>();
                    if (type.equals("Deluxe")) {
                        rooms.add(new Room(228, "Double", 150.0, "True", "2024-5-2"));
                        rooms.add(new Room(229, "Double", 150.0, "True", "2024-5-3"));
                    }
                    return rooms;
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