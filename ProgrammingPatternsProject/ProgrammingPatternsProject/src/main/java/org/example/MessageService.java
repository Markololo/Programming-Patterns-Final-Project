package org.example;
import java.util.Locale;
import java.util.ResourceBundle;

interface Language {
    public String getMessage(String msgCategory);
}

//Concrete Classes
class FrenchLang implements Language{
    Locale francelocale=Locale.of("fr","FR");

    public String getMessage(String msgCategory) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", francelocale);
        return messages.getString(msgCategory);
    }
}

class EnglishLang implements Language{
    Locale uslocale=Locale.of("en","US");

    public String getMessage(String msgCategory) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", uslocale);
        return messages.getString(msgCategory);
    }
}


class MessageFactory {
    public static Language returnLang(String type) {
        if (type.equalsIgnoreCase("french") || type.equalsIgnoreCase("Français"))
            return new FrenchLang();
        else if (type.equalsIgnoreCase("english"))
            return new EnglishLang();
        throw new IllegalArgumentException("Unknown Language Type: " + type);
    }
}

public class MessageService{
    public String useLangService(String type, String msgCategory) {
        Language lang = MessageFactory.returnLang(type);
        return lang.getMessage(msgCategory);
    }
}

