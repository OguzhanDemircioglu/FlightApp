package com.casestudy.dto;

import java.time.LocalDateTime;

public record FlightGroupKey(
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureDateTime,
        LocalDateTime arrivalDateTime
) {
}
