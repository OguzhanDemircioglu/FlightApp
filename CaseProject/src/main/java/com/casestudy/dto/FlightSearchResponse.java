package com.casestudy.dto;

import java.util.List;

public class FlightSearchResponse {

    private List<FlightDto> flights;

    public FlightSearchResponse() {
    }

    public FlightSearchResponse(List<FlightDto> flights) {
        this.flights = flights;
    }

    public List<FlightDto> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightDto> flights) {
        this.flights = flights;
    }
}
