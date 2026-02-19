package com.casestudy.client.providerb;

import com.casestudy.client.providerb.gen.AvailabilitySearchRequest;
import com.casestudy.client.providerb.gen.AvailabilitySearchResponse;
import com.casestudy.util.DateTimeConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDateTime;

@Component
public class ProviderBSoapClient {

    private final WebServiceTemplate webServiceTemplate;

    public ProviderBSoapClient(
            @Qualifier("providerBWebServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public AvailabilitySearchResponse search(String origin, String destination,
                                              LocalDateTime departureDate) {
        AvailabilitySearchRequest request = new AvailabilitySearchRequest();
        request.setDeparture(origin);
        request.setArrival(destination);
        request.setDepartureDate(DateTimeConverter.toXmlGregorianCalendar(departureDate));

        return (AvailabilitySearchResponse) webServiceTemplate
                .marshalSendAndReceive(request);
    }
}
