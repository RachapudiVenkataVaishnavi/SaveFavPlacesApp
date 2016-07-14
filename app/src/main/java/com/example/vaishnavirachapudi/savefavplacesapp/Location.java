package com.example.vaishnavirachapudi.savefavplacesapp;

import java.io.Serializable;

/**
 * Created by vaishnavirachapudi on 6/16/16.
 */
public class Location implements Serializable {
    String location;
    String latitude;
    String longitude;

    public Location() {
    }

    public Location(String location, String latitude, String longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "location='" + location + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
