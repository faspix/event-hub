package com.faspix.utility;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {

    private Double lat;

    private Double lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
