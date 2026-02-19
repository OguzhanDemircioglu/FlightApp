package com.flightproviderb.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class DateTimeConverter {

    private static final DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to create DatatypeFactory", e);
        }
    }

    public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null) return null;
        return xmlCal.toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDateTime();
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        GregorianCalendar gc = GregorianCalendar.from(
                localDateTime.atZone(ZoneId.systemDefault()));
        return datatypeFactory.newXMLGregorianCalendar(gc);
    }
}
