package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * Connect to JDBC driver
     * @return jdbc connection
     */
    public static Connection connect() {
//        String Base_Path = "jdbc:sqlite:src/main/resources/database";//java db connectivity(jdbc)
//        //jdbc:sqlite is a jdbc connection URL to connect to a SQLite database in java
//        //after the '.' is where you want your db to be(.src/...). But the '.' didn't work, so we removed it.
//        //We made the database folder before
//        String DB_Path = Base_Path + "data.db";//Making our database file
//
//        Connection connection;
//        try {
//            //try to connect to the db:
//            connection = DriverManager.getConnection(DB_Path);
//            //DriverManager's a class that knows all the registered db drivers like SQLite, mysql, ...
//            //and chooses the current one when you try to connect.
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return connection;
        String Base_Path = "jdbc:mysql://localhost:3306/HotelDB?useSSL=false&serverTimezone=UTC";//java db connectivity(jdbc)
        String user = "markololo2468@gmail.com";
        String password = "Patterns#1";

        try {
            return DriverManager.getConnection(Base_Path, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL", e);
        }
    }

    /**
     * To create a table in the database
     */
    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS students
                (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER
                );
                """;
        //In java, triple quotes are called text blocks, so that we don't write long strings like "abc" + "..." + ...

        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Table Created Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a column to a table
     */
    public static void addColumn(String columnName, String columnType, String tableName) {
        String sql = "ALTER TABLE "+tableName+ " ADD COLUMN " + columnName + " " + columnType;

        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Column " + columnName + " added in table " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops a table from the db
     * @param tableName table to remove
     */
    public static void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Dropped Table " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a row to the students table
     */
    public static void insertStudentRecord(String name, int age) {
        String sql = "INSERT INTO students(name, age) VALUES(?,?)";
        //This is an SQL query with placeholders instead of inserting raw values directly.
        //This is for security. The '?' are parameter markers that'll be safely filled later.
        //This helps prevent SQL injection attacks and make the code cleaner.

        try {
            Connection con = connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, name); //Set student name in the placeholder
            preparedStatement.setInt(2, age); //Set student age in the placeholder
            preparedStatement.executeUpdate();
            System.out.println("Row Added");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    public static void updateStudent(int id, String name, int age) {
        String sql = "UPDATE students SET name=?, age=? WHERE id=?";

        try {
            Connection con = connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, name); //Set student name in the placeholder
            preparedStatement.setInt(2, age); //Set student age in the placeholder
            preparedStatement.setInt(3, id); //Set student age in the placeholder
            int rowsUpdated = preparedStatement.executeUpdate(); //Returns the number of rows affected

            if (rowsUpdated > 0)
                System.out.println("An existing student was updated successfully");
            else
                System.out.println("No student with the provided ID exists.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a row
     */
    public static void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";

        try {
            Connection con = connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0)
                System.out.println("Student with ID "+id+" removed successfully");
            else
                System.out.println("No student with the provided ID exists.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * You can query all data in plain text and display them instead of using execute or execute update.
     * Here executeQuery is used. The result's stored in a ResultSet object,
     * and we can use getters such as getInt, getString, etc. to retrieve each field from it.
     */
    public static String selectPlainText() {
        String sql = "SELECT * FROM students";
        StringBuilder builder = new StringBuilder();
        //StringBuilder class is to append/build strings efficiently

        try {
            Connection con = connect();
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");

                builder.append(String.format("ID: %d, Name: %s, Age: %d%n", id, name, age));//For new line, we use '\n' or %n

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return builder.toString(); //return the plain text from the table
    }

    /**
     * Retrieves student sata amd returns it as a list of student objects.
     */
    public static List<Student> selectJson() {
        //Create a JSON object in sql for each row:
        String sql = """
                SELECT json_object(
                'id', id,
                'name', name,
                'age', age
                ) AS json_result
                FROM students;
                """;
        List<Student> studentList = new ArrayList<>();
        Gson gson = new Gson();//Gson class is to parse JSON strings into objects (Gson = Google's json library for java)
        //Allows you to convert java objects to JSON strings, vice-versa.
        //serialize (write): converts java object to JSON
        //deserialize(read): converts JSON to java objects

        try {
            Connection con = connect();
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch

            while (rs.next()) {
                String jsonResult = rs.getString("json_result");
                Student student = gson.fromJson(jsonResult, Student.class);
                //converts json string to java object like {"id":1, "name":Mey, "age":17}
                //It's deserialization
                //The second parameter is the class you want to convert the json into.
                studentList.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return studentList;
    }

    public static void main(String[] args) {
        //Test the connection jdbc connect()
//        try {
//            Connection con = connect();
//            if (con != null) {
//                System.out.println("...Successfully connected to mySQL!");
//            }
//        } catch (Exception e ) {
//            System.out.println("Connection Failure... :(");
//            System.out.println(e.getMessage());
//        }
        connect();
        //createTable();
        //addColumn("email", "TEXT", "students");
        //dropTable("students");
        //insertStudentRecord("Alex", 25);//id is 1 by default I think
        //insertStudentRecord("Alex", 19);//id is 2
        //updateStudent(2, "Alex", 17);// -> An existing student was updated successfully
        //updateStudent(2, "Alex", 17);
        //deleteStudent(2);
        //insertStudentRecord("Martin", 19);
        //updateStudent(3, "Mark", 18);
        //System.out.println(selectPlainText());//Plain text Format

        //JSON-GUI
        //Convert list of students to a nicely formatted JSON string(PrettyPrinting)
        //GSON Builder let us customize JSON and is a helper class used to customize how Gson behaves
        //setPrettyPrinting() tells GSON "when converting objects to JSON, make it easy to read using indentation and line breaks"
        //.create() finalizes the configuration and returns a JSON object that you can use.
       // Gson gson = new GsonBuilder().setPrettyPrinting().create();

//        List<Student> students = selectJson();//JSON Format
//        //System.out.println(students);
//        String prettyJson = gson.toJson(students);
//
//        //Create textArea
//        JTextArea textArea = new JTextArea(prettyJson);
//        textArea.setLineWrap(true); //Automatically wraps lines
//        textArea.setWrapStyleWord(true); //To wrap at word boundaries, not in the middle of the word
//        textArea.setEditable(false); //Make it read-only
//        //Add scrolling:
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        //Create the swing window (JFrame):
//        JFrame frame = new JFrame("Students in JSON");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(scrollPane);
//        frame.setSize(600,200);
//        frame.setVisible(true);
    }
}
