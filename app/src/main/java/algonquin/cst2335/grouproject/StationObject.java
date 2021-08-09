package algonquin.cst2335.grouproject;

import java.io.Serializable;

public class StationObject implements Serializable {
    /**
     * variables for the charging station
     */
    public String phone;
    public String title;
    public long id;
    public double latitude;
    public double longitude;

    /**
     * no argument constructor
     */
    public StationObject() {

    }

    /**
     * constructor to create a charging station object
     * @param phone
     * @param title
     * @param id
     * @param latitude
     * @param longitude
     */
    public StationObject(String phone, String title, long id, double latitude, double longitude) {

        this.phone=phone;
        this.title=title;
        this.id=id;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    /**
     * @return a phone number of a station
     */
    public String getPhone()
    {
        return phone;
    }

    /**
     * @return title of station
     */
    public String getTitle() {

        return title;
    }

    /**
     * @return id of a charging station
     */
    public long getId() {

        return id;
    }

    /**
     * @return latitude of station
     */
    public double getLatitude() {

        return latitude;
    }

    /**
     * @return longitude of station
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param phone number of a station
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @param title set of station
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param latitude set of station
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude set of station
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
