package info.mindbreak.ardhinata.gpstracker;

/**
 * Created by Ardhinata Juari (ardhinata.juari@gmail.com) for GPS Tracker on 08/03/16.
 */
public class InfoContainer {
    private String status;
    private String lat;
    private String lon;
    private String alt;
    private String acc;
    private String button;
    
    private boolean statusSet = false;
    private boolean latSet = false;
    private boolean lonSet = false;
    private boolean altSet = false;
    private boolean accSet = false;
    private boolean buttonSet = false;


    public boolean isStatusSet() {
        return statusSet;
    }

    public boolean isLatSet() {
        return latSet;
    }

    public boolean isLonSet() {
        return lonSet;
    }

    public boolean isAltSet() {
        return altSet;
    }

    public boolean isAccSet() {
        return accSet;
    }

    public boolean isButtonSet() {
        return buttonSet;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        statusSet=true;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
        latSet=true;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
        lonSet=true;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
        altSet=true;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
        accSet=true;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
        buttonSet=true;
    }
}
