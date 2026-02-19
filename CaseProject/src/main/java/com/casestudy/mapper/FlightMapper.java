package com.casestudy.mapper;

import com.casestudy.dto.FlightDto;
import com.casestudy.util.DateTimeConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    /**
     * ProviderA Flight -> unified FlightDto
     * flightNo -> flightNumber, origin -> origin, destination -> destination
     */
    public FlightDto fromProviderA(com.casestudy.client.providera.gen.Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setFlightNumber(flight.getFlightNo());
        dto.setOrigin(flight.getOrigin());
        dto.setDestination(flight.getDestination());
        dto.setDepartureDateTime(DateTimeConverter.toLocalDateTime(flight.getDeparturedatetime()));
        dto.setArrivalDateTime(DateTimeConverter.toLocalDateTime(flight.getArrivaldatetime()));
        dto.setPrice(flight.getPrice());
        return dto;
    }

    /**
     * ProviderB Flight -> unified FlightDto
     * flightNumber -> flightNumber, departure -> origin, arrival -> destination
     */
    public FlightDto fromProviderB(com.casestudy.client.providerb.gen.Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setOrigin(flight.getDeparture());
        dto.setDestination(flight.getArrival());
        dto.setDepartureDateTime(DateTimeConverter.toLocalDateTime(flight.getDeparturedatetime()));
        dto.setArrivalDateTime(DateTimeConverter.toLocalDateTime(flight.getArrivaldatetime()));
        dto.setPrice(flight.getPrice());
        return dto;
    }

    public List<FlightDto> mapAllFromProviderA(
            List<com.casestudy.client.providera.gen.Flight> flights) {
        return flights.stream().map(this::fromProviderA).collect(Collectors.toList());
    }

    public List<FlightDto> mapAllFromProviderB(
            List<com.casestudy.client.providerb.gen.Flight> flights) {
        return flights.stream().map(this::fromProviderB).collect(Collectors.toList());
    }
}
