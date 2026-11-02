package com.smartcity.routefinder.dto;

import java.util.List;

public class RouteResponse {
    private String source;
    private String destination;
    private int totalDistance;
    private int numberOfRoads;
    private int numberOfStops;
    private List<String> path;
    private List<LegDetail> legs;
    private boolean reachable;
    private String message;
    private String disclaimer;

    public static class LegDetail {
        private String fromCity;
        private String toCity;
        private int distance;
        private int cumulativeDistance;

        public LegDetail() {}

        public LegDetail(String fromCity, String toCity, int distance, int cumulativeDistance) {
            this.fromCity = fromCity;
            this.toCity = toCity;
            this.distance = distance;
            this.cumulativeDistance = cumulativeDistance;
        }

        public String getFromCity() { return fromCity; }
        public void setFromCity(String fromCity) { this.fromCity = fromCity; }

        public String getToCity() { return toCity; }
        public void setToCity(String toCity) { this.toCity = toCity; }

        public int getDistance() { return distance; }
        public void setDistance(int distance) { this.distance = distance; }

        public int getCumulativeDistance() { return cumulativeDistance; }
        public void setCumulativeDistance(int cumulativeDistance) { this.cumulativeDistance = cumulativeDistance; }
    }

    public RouteResponse() {}

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public int getTotalDistance() { return totalDistance; }
    public void setTotalDistance(int totalDistance) { this.totalDistance = totalDistance; }

    public int getNumberOfRoads() { return numberOfRoads; }
    public void setNumberOfRoads(int numberOfRoads) { this.numberOfRoads = numberOfRoads; }

    public int getNumberOfStops() { return numberOfStops; }
    public void setNumberOfStops(int numberOfStops) { this.numberOfStops = numberOfStops; }

    public List<String> getPath() { return path; }
    public void setPath(List<String> path) { this.path = path; }

    public List<LegDetail> getLegs() { return legs; }
    public void setLegs(List<LegDetail> legs) { this.legs = legs; }

    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}

