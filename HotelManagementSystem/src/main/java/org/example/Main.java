package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static Connection connect() {
        String Base_Path = "jdbc:sqlite:src/main/resources/Hotel_Management_DB/";
        //jdbc:sqlite is a JDBC(java database connectivity) connection URL used to connect to a SQLite database in java
        String DB_Path = Base_Path + "hotel_management.db";

        Connection connection;
        try {
            //try to connect to the database
            connection = DriverManager.getConnection(DB_Path);
            //DriverManager is a class that knows all the registered database drivers(like SQLite, mysql,...)
            //chooses the correct one when you try to connect
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }


    public static List<Room> selectJson() {
        //create a JSON object in SQL for each row


        String sql = """
                 SELECT json_object(
                 'roomNo', RoomNum,
                 'type', RoomType,
                 'price', Price,
                 'availability',Availability,
                 'addedDate', AddedDate
                 ) AS json_result
                 FROM Rooms;
                """;
        List<Room> studentList = new ArrayList<>();
        Gson gson = new Gson(); //used to parse JSON strings into objects
        //Gson stands for google's JSON library for java'
        //it allows you to :
        //convert java objects <=> JSON strings
        //-serialize (write) : convert java objects to JSON
        //-deserialize (read) : convert JSON to java objects

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            //convert JSON string to a Student object
            while (rs.next()) {
                String jsonResults = rs.getString("json_result");
                Room student = gson.fromJson(jsonResults, Room.class);
                //it converts a JSON string like{"id":1, "name:"Alex, "age:":20} into a java object of type student
                //it is deserialization
                //the second parameter is the class type you want Gson to convert the JSON into (here Student.class)
                studentList.add(student);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return studentList;
    }


    public static void main(String[] args) {
//        try {
//            Connection conn = connect();
//            if (conn != null)
//                System.out.println("Connection to Sqlite has been established!");
//
//        } catch (Exception e) {
//            System.out.println("Failed to connect: " + e.getMessage());
//        }

        List<Room> Rooms=selectJson();
        Gson gson=new GsonBuilder().setPrettyPrinting().create();

        String prettyJson=gson.toJson(Rooms);  //convert list to JSON string with indentation and line breaks

        //GUI
        JTextArea textArea=new JTextArea(prettyJson); //JTextArea is a multi-line textbox
        textArea.setLineWrap(true);  //automatically wrap long lines
        textArea.setWrapStyleWord(true); //wrap at word boundaries (not mid-word)
        textArea.setEditable(false); //make it read-only

        //Add scrolling
        JScrollPane scrollPane=new JScrollPane(textArea); //wrap the text area in a scroll pane

        //create the swing window
        JFrame frame=new JFrame("Students in JSON");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.setSize(600,400);
        frame.setVisible(true);

    }
}
