package com.flightproviderb.endpoint;

import com.flightproviderb.gen.AvailabilitySearchRequest;
import com.flightproviderb.gen.AvailabilitySearchResponse;
import com.flightproviderb.gen.Flight;
import com.flightproviderb.service.SearchRequest;
import com.flightproviderb.service.SearchResult;
import com.flightproviderb.service.SearchService;
import com.flightproviderb.util.DateTimeConverter;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FlightSearchEndpoint {

    private static final String NAMESPACE_URI = "http://flightproviderb.com/soap";

    private final SearchService searchService;

    public FlightSearchEndpoint(SearchService searchService) {
        this.searchService = searchService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "availabilitySearchRequest")
    @ResponsePayload
    public AvailabilitySearchResponse availabilitySearch(
            @RequestPayload AvailabilitySearchRequest request) {

        SearchRequest domainRequest = new SearchRequest(
                request.getDeparture(),
                request.getArrival(),
                DateTimeConverter.toLocalDateTime(request.getDepartureDate())
        );

        SearchResult result = searchService.availabilitySearch(domainRequest);

        AvailabilitySearchResponse response = new AvailabilitySearchResponse();
        response.setHasError(result.isHasError());
        response.setErrorMessage(result.getErrorMessage());

        if (result.getFlightOptions() != null) {
            for (com.flightproviderb.service.Flight domainFlight : result.getFlightOptions()) {
                Flight soapFlight = new Flight();
                soapFlight.setFlightNumber(domainFlight.getFlightNumber());
                soapFlight.setDeparture(domainFlight.getDeparture());
                soapFlight.setArrival(domainFlight.getArrival());
                soapFlight.setDeparturedatetime(
                        DateTimeConverter.toXmlGregorianCalendar(domainFlight.getDeparturedatetime()));
                soapFlight.setArrivaldatetime(
                        DateTimeConverter.toXmlGregorianCalendar(domainFlight.getArrivaldatetime()));
                soapFlight.setPrice(domainFlight.getPrice());
                response.getFlightOptions().add(soapFlight);
            }
        }

        return response;
    }
}
