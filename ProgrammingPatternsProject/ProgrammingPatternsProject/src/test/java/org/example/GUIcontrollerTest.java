package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests methods from the GUIcontroller class.
 */
public class GUIcontrollerTest
{
    //the controller gui object will be tested
    private GUIcontroller controller;


    //Executed before each test method
    // initJavaFX method makes sure JavaFX platform is initialized
    //using lambda expression
    @BeforeAll
    public static void initJavaFX() throws InterruptedException
    {
        // this starts the JavaFX Platform if the gui isnt already running
        //javafx operates in a single thread  this schedule task to run on the
        //application thread
        Platform.startup(() -> {
        });
        // allow time for JavaFX platform to start
        //pause the thread for 1 second(1000 milliseconds)
        Thread.sleep(1000);
    }

    //Executed before each test method
    // setup method to initialize controller and DBManager
    @BeforeEach
    public void setUp()
    {
        // ensures all java ui controls are initialized
        Platform.runLater(() -> {
            controller = new GUIcontroller();

            // set up GUI element
            ComboBox<String> roomTypeComboBox = new ComboBox<>();
            //tableview is of type room since the method tested use rooms
            TableView<Room> tableView = new TableView<>();

            //set table columns tableview and the roomtype combobox
            controller.setRoomTypeComboBox(roomTypeComboBox);
            controller.setTableView(tableView);
            controller.setColumn1(new TableColumn<>("Room Number"));
            controller.setColumn2(new TableColumn<>("Room Type"));
            controller.setColumn3(new TableColumn<>("Price"));
            controller.setColumn4(new TableColumn<>("Availability"));
            controller.setColumn5(new TableColumn<>("Booking Date"));

            // set DBManager to overrride methods of finding room by type and price
            controller.setDbManager(new DBManager()
            {

                @Override
                public List<Room> findRoomByType(String type)
                {

                    if ("Double".equals(type))
                    {
                     //creating immutable list that cannot be change
                        // no changing elements
                        return List.of(
                                new Room(228, "Double", 150.0, "True", "2024-05-2"),
                                new Room(229, "Double", 150.0, "True", "2024-05-3")
                        );
                    }

                    return new ArrayList<>();
                }

                @Override
                public List<Room> findRoomLowToHighPrice()
                {
                    //create object or room of type
                    return List.of(
                            new Room(345, "Single", 80.0, "True", "2024-05-2"),
                            new Room(140, "Suite", 150.0, "True", "2024-05-2"));
                }
            });
        });

        // Ensure the JavaFX components are initialized before proceeding
        try
        {
            // Wait for Platform.runLater to finish
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            //method print details about an exception (error)
            e.printStackTrace();
        }
    }

//actual gui method
    @Test
    public void testHandleSearchByRoomType() throws InterruptedException
    {
        // Add items to the combo box and search inside Platform.runLater
        Platform.runLater(() -> {
            // the getitems adds the deluxe room type to the list of combo box
            controller.getRoomTypeComboBox().getItems().add("Double");

            //this selects the value double from combo box
            controller.getRoomTypeComboBox().setValue("Double");

            //call the method from gui
            controller.handleSearchByRoomType();
        });

        // Wait for the search and table update to finish
        Thread.sleep(1000);

        // verify results after UI update
        Platform.runLater(() -> {
            //get the list of rooms objects
            List<Room> rooms = controller.getTableView().getItems();

            //ensure the rooms arent null
            assertNotNull(rooms);

            //2 is expected for the size of list  and the actual is rooms.size
           assertEquals(2, rooms.size());

//get the first room from list
            Room room = rooms.get(0);

            //expected to get 228 and
            assertEquals(228, room.getRoomNum());

            //expected to get Double
            assertEquals("Double", room.getRoomType());
        });
    }
}
