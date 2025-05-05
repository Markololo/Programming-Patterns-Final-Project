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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Language interface with th getMessage method for the factory programming pattern.
 */
interface Language {
    /**
     * Gets the message category for translation
     * @param msgCategory the category of the message to use Resource Bundle
     * @return the string that the category represents
     */
    String getMessage(String msgCategory);
    String formatDate(LocalDate date);
}

/**
 * Class for the factory programming pattern. It implements the Language interface
 * and defines its getMessage method for the French language.
 */
//Concrete Classes
class FrenchLang implements Language{
    Locale francelocale=Locale.of("fr","FR");

    /**
     * Gets the message category for translation
     * @param msgCategory the category of the message to use Resource Bundle in French.
     * @return the string that the category represents
     */
    public String getMessage(String msgCategory) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", francelocale);
        return messages.getString(msgCategory);
    }
    @Override
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRANCE);
        return date.format(formatter);
    }
}

/**
 * Class that implements the Language interface for the factory programming pattern.
 * It defines the getMessage method to adapt to the English language.
 */
class EnglishLang implements Language{
    Locale uslocale=Locale.of("en","US");

    /**
     * Gets the message category for translation
     * @param msgCategory the category of the message to use Resource Bundle in English.
     * @return the string that the category represents
     */
    public String getMessage(String msgCategory) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", uslocale);
        return messages.getString(msgCategory);
    }
    @Override
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
        return date.format(formatter);
    }
}

/**
 * A factory class to find out which class that implements the Language class to use.
 * It helps the MessageService class.
 */
class MessageFactory {
    /**
     * Returns an object of a class that implements the Language interface
     * that is of the specified/input type.
     * @param type The input type that is the chosen language that is represented by a class
     * @return an object of a class that implements the Language interface that is of the same input type (language).
     */
    public static Language returnLang(String type) {
        if (type.equalsIgnoreCase("french") || type.equalsIgnoreCase("Français"))
            return new FrenchLang();
        else if (type.equalsIgnoreCase("english"))
            return new EnglishLang();
        throw new IllegalArgumentException("Unknown Language Type: " + type);
    }
}

/**
 * A service class to translate messages into the desired language.
 */
public class MessageService{
    /**
     * Used to translate a message into the chosen language.
     * @param type the language of choice
     * @param msgCategory the message category in the Resource Bundle messages file
     * @return the string input that is represented by the category, translated into the chose language (type).
     */
    public String useLangService(String type, String msgCategory) {
        Language lang = MessageFactory.returnLang(type);
        return lang.getMessage(msgCategory);
    }

    /**
     * Used to format a LocalDate in the chosen language.
     * @param type the language of choice ("english" or "french")
     * @param date the LocalDate to be formatted
     * @return the formatted date string
     */
    public String useDateLangService(String type, LocalDate date) {
        Language lang = MessageFactory.returnLang(type);
        return lang.formatDate(date);
    }
}

