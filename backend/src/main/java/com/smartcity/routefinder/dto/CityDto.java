package com.smartcity.routefinder.dto;

import java.util.List;

public class CityDto {
    private String name;
    private int roadCount;
    private List<String> connectedCities;

    public CityDto() {}

    public CityDto(String name, int roadCount, List<String> connectedCities) {
        this.name = name;
        this.roadCount = roadCount;
        this.connectedCities = connectedCities;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRoadCount() { return roadCount; }
    public void setRoadCount(int roadCount) { this.roadCount = roadCount; }

    public List<String> getConnectedCities() { return connectedCities; }
    public void setConnectedCities(List<String> connectedCities) { this.connectedCities = connectedCities; }
}

