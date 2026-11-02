package com.smartcity.routefinder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RoadRequest {

    @NotBlank(message = "First city name (cityA) is required.")
    private String cityA;

    @NotBlank(message = "Second city name (cityB) is required.")
    private String cityB;

    @Min(value = 1, message = "Distance must be greater than 0 km.")
    private int distance;

    public RoadRequest() {}

    public RoadRequest(String cityA, String cityB, int distance) {
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

