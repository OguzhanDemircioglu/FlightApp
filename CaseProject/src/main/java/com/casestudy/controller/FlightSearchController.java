package com.casestudy.controller;

import com.casestudy.dto.FlightDto;
import com.casestudy.dto.FlightSearchRequest;
import com.casestudy.dto.FlightSearchResponse;
import com.casestudy.service.FlightAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightSearchController {

    private final FlightAggregatorService aggregatorService;

    public FlightSearchController(FlightAggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    /**
     * REST Endpoint 1: Tum ucuslari birlestirir.
     * POST /api/flights/search
     */
    @PostMapping("/search")
    public ResponseEntity<FlightSearchResponse> searchAllFlights(
            @RequestBody FlightSearchRequest request) {

        LocalDateTime departureDate = LocalDateTime.parse(request.getDepartureDate());

        List<FlightDto> flights = aggregatorService.searchAllFlights(
                request.getOrigin(),
                request.getDestination(),
                departureDate
        );

        return ResponseEntity.ok(new FlightSearchResponse(flights));
    }

    /**
     * REST Endpoint 2: Gruplayip en ucuz ucuslari doner.
     * POST /api/flights/search/cheapest
     */
    @PostMapping("/search/cheapest")
    public ResponseEntity<FlightSearchResponse> searchCheapestFlights(
            @RequestBody FlightSearchRequest request) {

        LocalDateTime departureDate = LocalDateTime.parse(request.getDepartureDate());

        List<FlightDto> flights = aggregatorService.searchCheapestFlights(
                request.getOrigin(),
                request.getDestination(),
                departureDate
        );

        return ResponseEntity.ok(new FlightSearchResponse(flights));
    }
}
