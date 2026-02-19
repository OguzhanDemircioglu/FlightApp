package com.casestudy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Value("${soap.provider-a.url}")
    private String providerAUrl;

    @Value("${soap.provider-b.url}")
    private String providerBUrl;

    // --- Provider A ---

    @Bean
    public Jaxb2Marshaller providerAMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.casestudy.client.providera.gen");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate providerAWebServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        Jaxb2Marshaller marshaller = providerAMarshaller();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri(providerAUrl);
        return template;
    }

    // --- Provider B ---

    @Bean
    public Jaxb2Marshaller providerBMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.casestudy.client.providerb.gen");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate providerBWebServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        Jaxb2Marshaller marshaller = providerBMarshaller();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri(providerBUrl);
        return template;
    }
}
