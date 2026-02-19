package com.flightprovidera.util;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class DateTimeConverter {

    private DateTimeConverter() {
    }

    public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null) return null;
        return xmlCal.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        try {
            GregorianCalendar gc = GregorianCalendar.from(
                    localDateTime.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (Exception e) {
            throw new RuntimeException("DateTime conversion error", e);
        }
    }
}
