package com.flightprovidera.endpoint;

import com.flightprovidera.gen.AvailabilitySearchRequest;
import com.flightprovidera.gen.AvailabilitySearchResponse;
import com.flightprovidera.service.SearchRequest;
import com.flightprovidera.service.SearchResult;
import com.flightprovidera.service.SearchService;
import com.flightprovidera.util.DateTimeConverter;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FlightSearchEndpoint {

    private static final String NAMESPACE_URI = "http://flightprovidera.com/soap";

    private final SearchService searchService;

    public FlightSearchEndpoint(SearchService searchService) {
        this.searchService = searchService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "availabilitySearchRequest")
    @ResponsePayload
    public AvailabilitySearchResponse availabilitySearch(
            @RequestPayload AvailabilitySearchRequest request) {

        SearchRequest domainRequest = new SearchRequest(
                request.getOrigin(),
                request.getDestination(),
                DateTimeConverter.toLocalDateTime(request.getDepartureDate())
        );

        SearchResult result = searchService.availabilitySearch(domainRequest);

        AvailabilitySearchResponse response = new AvailabilitySearchResponse();
        response.setHasError(result.isHasError());
        response.setErrorMessage(result.getErrorMessage());

        if (result.getFlightOptions() != null) {
            for (com.flightprovidera.service.Flight domainFlight : result.getFlightOptions()) {
                com.flightprovidera.gen.Flight soapFlight = new com.flightprovidera.gen.Flight();
                soapFlight.setFlightNo(domainFlight.getFlightNo());
                soapFlight.setOrigin(domainFlight.getOrigin());
                soapFlight.setDestination(domainFlight.getDestination());
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
