package com.casestudy.controller;

import com.casestudy.dto.FlightDto;
import com.casestudy.dto.FlightSearchRequest;
import com.casestudy.dto.FlightSearchResponse;
import com.casestudy.service.FlightAggregatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightSearchControllerTest {

    @Mock
    private FlightAggregatorService aggregatorService;

    @InjectMocks
    private FlightSearchController controller;

    private FlightSearchRequest request;

    @BeforeEach
    void setUp() {
        request = new FlightSearchRequest("IST", "COV", "2026-03-15T00:00:00");
    }

    // ==================== POST /api/flights/search ====================

    @Test
    @DisplayName("searchAllFlights - basarili cagri, 3 ucus doner")
    void searchAllFlights_returnsFlightList() {
        List<FlightDto> flights = List.of(
                createFlight("TK1001", "IST", "COV", "2026-03-15T09:00:00", "2026-03-15T13:00:00", 159.0),
                createFlight("PC1001", "IST", "COV", "2026-03-15T09:00:00", "2026-03-15T13:00:00", 180.0),
                createFlight("XQ1001", "IST", "COV", "2026-03-15T12:00:00", "2026-03-15T16:00:00", 207.0)
        );

        when(aggregatorService.searchAllFlights(eq("IST"), eq("COV"), any(LocalDateTime.class)))
                .thenReturn(flights);

        ResponseEntity<FlightSearchResponse> response = controller.searchAllFlights(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getFlights().size());
        assertEquals("TK1001", response.getBody().getFlights().get(0).getFlightNumber());
        assertEquals("IST", response.getBody().getFlights().get(0).getOrigin());
        assertEquals("COV", response.getBody().getFlights().get(0).getDestination());
        assertEquals(BigDecimal.valueOf(159.0), response.getBody().getFlights().get(0).getPrice());
        assertEquals("PC1001", response.getBody().getFlights().get(1).getFlightNumber());
        assertEquals("XQ1001", response.getBody().getFlights().get(2).getFlightNumber());

        verify(aggregatorService, times(1))
                .searchAllFlights(eq("IST"), eq("COV"), eq(LocalDateTime.of(2026, 3, 15, 0, 0, 0)));
    }

    @Test
    @DisplayName("searchAllFlights - bos sonuc, bos liste doner")
    void searchAllFlights_emptyResult() {
        when(aggregatorService.searchAllFlights(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<FlightSearchResponse> response = controller.searchAllFlights(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getFlights().isEmpty());
    }

    @Test
    @DisplayName("searchAllFlights - farkli origin/destination parametreleri dogru iletilir")
    void searchAllFlights_passesCorrectParameters() {
        FlightSearchRequest customRequest = new FlightSearchRequest("ADB", "LHR", "2026-06-20T10:30:00");

        when(aggregatorService.searchAllFlights(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        controller.searchAllFlights(customRequest);

        verify(aggregatorService).searchAllFlights(
                eq("ADB"),
                eq("LHR"),
                eq(LocalDateTime.of(2026, 6, 20, 10, 30, 0))
        );
    }

    @Test
    @DisplayName("searchAllFlights - response'taki tarih alanlari korunur")
    void searchAllFlights_dateTimeFieldsPreserved() {
        LocalDateTime departure = LocalDateTime.of(2026, 3, 15, 9, 0, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, 3, 15, 13, 30, 0);

        List<FlightDto> flights = List.of(
                createFlight("TK1001", "IST", "COV", departure, arrival, new BigDecimal("199.50"))
        );

        when(aggregatorService.searchAllFlights(any(), any(), any()))
                .thenReturn(flights);

        ResponseEntity<FlightSearchResponse> response = controller.searchAllFlights(request);

        FlightDto result = response.getBody().getFlights().get(0);
        assertEquals(departure, result.getDepartureDateTime());
        assertEquals(arrival, result.getArrivalDateTime());
        assertEquals(new BigDecimal("199.50"), result.getPrice());
    }

    // ==================== POST /api/flights/search/cheapest ====================

    @Test
    @DisplayName("searchCheapestFlights - gruplanmis en ucuz ucuslari doner")
    void searchCheapestFlights_returnsGroupedCheapest() {
        List<FlightDto> cheapestFlights = List.of(
                createFlight("TK1001", "IST", "COV", "2026-03-15T09:00:00", "2026-03-15T13:00:00", 159.0),
                createFlight("TK1002", "IST", "COV", "2026-03-15T12:00:00", "2026-03-15T16:00:00", 207.0)
        );

        when(aggregatorService.searchCheapestFlights(eq("IST"), eq("COV"), any(LocalDateTime.class)))
                .thenReturn(cheapestFlights);

        ResponseEntity<FlightSearchResponse> response = controller.searchCheapestFlights(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getFlights().size());
        assertEquals("TK1001", response.getBody().getFlights().get(0).getFlightNumber());
        assertEquals(BigDecimal.valueOf(159.0), response.getBody().getFlights().get(0).getPrice());
        assertEquals("TK1002", response.getBody().getFlights().get(1).getFlightNumber());
        assertEquals(BigDecimal.valueOf(207.0), response.getBody().getFlights().get(1).getPrice());

        verify(aggregatorService, times(1))
                .searchCheapestFlights(eq("IST"), eq("COV"), eq(LocalDateTime.of(2026, 3, 15, 0, 0, 0)));
    }

    @Test
    @DisplayName("searchCheapestFlights - bos sonuc, bos liste doner")
    void searchCheapestFlights_emptyResult() {
        when(aggregatorService.searchCheapestFlights(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<FlightSearchResponse> response = controller.searchCheapestFlights(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getFlights().isEmpty());
    }

    @Test
    @DisplayName("searchCheapestFlights - tek ucus grubu, tek sonuc doner")
    void searchCheapestFlights_singleGroup() {
        List<FlightDto> singleFlight = List.of(
                createFlight("TK1001", "IST", "COV", "2026-03-15T09:00:00", "2026-03-15T13:00:00", 150.0)
        );

        when(aggregatorService.searchCheapestFlights(any(), any(), any()))
                .thenReturn(singleFlight);

        ResponseEntity<FlightSearchResponse> response = controller.searchCheapestFlights(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getFlights().size());
        assertEquals(BigDecimal.valueOf(150.0), response.getBody().getFlights().get(0).getPrice());
    }

    // ==================== Helper methods ====================

    private FlightDto createFlight(String flightNumber, String origin, String destination,
                                    String departure, String arrival, double price) {
        return createFlight(flightNumber, origin, destination,
                LocalDateTime.parse(departure), LocalDateTime.parse(arrival),
                BigDecimal.valueOf(price));
    }

    private FlightDto createFlight(String flightNumber, String origin, String destination,
                                    LocalDateTime departure, LocalDateTime arrival, BigDecimal price) {
        FlightDto dto = new FlightDto();
        dto.setFlightNumber(flightNumber);
        dto.setOrigin(origin);
        dto.setDestination(destination);
        dto.setDepartureDateTime(departure);
        dto.setArrivalDateTime(arrival);
        dto.setPrice(price);
        return dto;
    }
}
