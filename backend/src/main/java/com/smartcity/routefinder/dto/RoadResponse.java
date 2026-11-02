package com.smartcity.routefinder.dto;

public class RoadResponse {
    private String cityA;
    private String cityB;
    private int distance;

    public RoadResponse() {}

    public RoadResponse(String cityA, String cityB, int distance) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }

    public String getCityA() { return cityA; }
    public void setCityA(String cityA) { this.cityA = cityA; }

    public String getCityB() { return cityB; }
    public void setCityB(String cityB) { this.cityB = cityB; }

    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
}

