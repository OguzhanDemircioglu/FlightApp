package com.casestudy.client.providera;

import com.casestudy.client.providera.gen.AvailabilitySearchRequest;
import com.casestudy.client.providera.gen.AvailabilitySearchResponse;
import com.casestudy.util.DateTimeConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDateTime;

@Component
public class ProviderASoapClient {

    private final WebServiceTemplate webServiceTemplate;

    public ProviderASoapClient(
            @Qualifier("providerAWebServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public AvailabilitySearchResponse search(String origin, String destination,
                                              LocalDateTime departureDate) {
        AvailabilitySearchRequest request = new AvailabilitySearchRequest();
        request.setOrigin(origin);
        request.setDestination(destination);
        request.setDepartureDate(DateTimeConverter.toXmlGregorianCalendar(departureDate));

        return (AvailabilitySearchResponse) webServiceTemplate
                .marshalSendAndReceive(request);
    }
}
