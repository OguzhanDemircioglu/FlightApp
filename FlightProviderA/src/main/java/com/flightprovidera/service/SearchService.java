package com.flightprovidera.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SearchService {

	   public SearchResult availabilitySearch(SearchRequest request) {
	        if (request == null ||
	            request.getOrigin() == null || request.getOrigin().trim().isEmpty() ||
	            request.getDestination() == null || request.getDestination().trim().isEmpty() ||
	            request.getDepartureDate() == null) {

	            return new SearchResult(true, new ArrayList<>(), "Invalid search parameters. Origin, destination, and departure date are required.");
	        }

	        if (request.getDepartureDate().isBefore(LocalDateTime.now())) {
	            return new SearchResult(true, new ArrayList<>(), "Departure date cannot be in the past.");
	        }

	        try {
	            Random random = new Random();
	            List<Flight> flightOptions = new ArrayList<>();

	            for (int i = 1; i <= 3; i++) {
	                LocalDateTime departureTime = request.getDepartureDate()
	                    .withHour(6 + (i * 3))
	                    .withMinute(0)
	                    .withSecond(0);

	                Duration flightDuration = Duration.ofMinutes(
	                    120 + (long)(random.nextDouble() * 180));

	                LocalDateTime arrivalTime = departureTime.plus(flightDuration);

	                BigDecimal basePrice = BigDecimal.valueOf(100.0);
	                BigDecimal multiplier = BigDecimal.valueOf(i * 50);
	                BigDecimal randomPrice = BigDecimal.valueOf(random.nextInt(100));
	                BigDecimal totalPrice = basePrice.add(multiplier).add(randomPrice);

	                Flight flightOption = new Flight(
	                    "TK" + (1000 + i),
	                    "IST",
	                    "COV",
	                    departureTime,
	                    arrivalTime,
	                    totalPrice
	                );

	                Flight flightOption2 = new Flight(
		                    "PC" + (1000 + i),
		                    "IST",
		                    "COV",
		                    departureTime,
		                    arrivalTime,
		                    totalPrice
		                );

	                Flight flightOption3 = new Flight(
		                    "XQ" + (1000 + i),
		                    "IST",
		                    "COV",
		                    departureTime,
		                    arrivalTime,
		                    totalPrice
		                );

	                flightOptions.add(flightOption);
	                flightOptions.add(flightOption2);
	                flightOptions.add(flightOption3);
	            }

	            return new SearchResult(false, flightOptions, null);

	        } catch (Exception e) {
	            return new SearchResult(true, new ArrayList<>(), "An error occurred while searching for flights: " + e.getMessage());
	        }
	    }
}
