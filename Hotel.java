import java.util.Date;

public class Hotel {
    private String hotelName;
    private Date openingDate;
    private String location;
    private String owner;

    public Hotel(String hotelName, Date openingDate, String location, String owner) {
        this.hotelName = hotelName;
        this.openingDate = openingDate;
        this.location = location;
        this.owner = owner;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
