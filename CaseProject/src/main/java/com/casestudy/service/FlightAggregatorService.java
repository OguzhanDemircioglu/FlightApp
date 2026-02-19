package com.casestudy.service;

import com.casestudy.client.providera.ProviderASoapClient;
import com.casestudy.client.providera.gen.AvailabilitySearchResponse;
import com.casestudy.client.providerb.ProviderBSoapClient;
import com.casestudy.dto.FlightDto;
import com.casestudy.dto.FlightGroupKey;
import com.casestudy.mapper.FlightMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightAggregatorService {

    private final ProviderASoapClient providerAClient;
    private final ProviderBSoapClient providerBClient;
    private final FlightMapper flightMapper;

    public FlightAggregatorService(ProviderASoapClient providerAClient,
                                    ProviderBSoapClient providerBClient,
                                    FlightMapper flightMapper) {
        this.providerAClient = providerAClient;
        this.providerBClient = providerBClient;
        this.flightMapper = flightMapper;
    }

    /**
     * REST Endpoint 1: Tum ucuslari birlestirir.
     * Her iki provider'dan gelen ucuslari tek bir listede doner.
     */
    public List<FlightDto> searchAllFlights(String origin, String destination,
                                             LocalDateTime departureDate) {

        // Provider A'dan ucuslari al
        AvailabilitySearchResponse responseA = providerAClient.search(origin, destination, departureDate);
        List<FlightDto> flightsA = flightMapper.mapAllFromProviderA(responseA.getFlightOptions());

        // Provider B'den ucuslari al
        com.casestudy.client.providerb.gen.AvailabilitySearchResponse responseB =
                providerBClient.search(origin, destination, departureDate);
        List<FlightDto> flightsB = flightMapper.mapAllFromProviderB(responseB.getFlightOptions());

        // Birlestir
        List<FlightDto> allFlights = new ArrayList<>(flightsA);
        allFlights.addAll(flightsB);

        return allFlights;
    }

    /**
     * REST Endpoint 2: Ucuslari grupla, gruptaki en ucuzu sec.
     * Gruplama: flightNumber, origin, destination, departureDateTime, arrivalDateTime
     */
    public List<FlightDto> searchCheapestFlights(String origin, String destination,
                                                  LocalDateTime departureDate) {

        List<FlightDto> allFlights = searchAllFlights(origin, destination, departureDate);

        return allFlights.stream()
                .collect(Collectors.groupingBy(
                        flight -> new FlightGroupKey(
                                flight.getFlightNumber(),
                                flight.getOrigin(),
                                flight.getDestination(),
                                flight.getDepartureDateTime(),
                                flight.getArrivalDateTime()
                        )
                ))
                .values().stream()
                .map(group -> group.stream()
                        .min(Comparator.comparing(FlightDto::getPrice))
                        .orElseThrow()
                )
                .collect(Collectors.toList());
    }
}
