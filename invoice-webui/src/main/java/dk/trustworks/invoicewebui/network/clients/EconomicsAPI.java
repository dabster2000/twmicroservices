package dk.trustworks.invoicewebui.network.clients;

import com.fasterxml.jackson.databind.SerializationFeature;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.clients.model.InvoiceDTO;
import dk.trustworks.invoicewebui.network.clients.model.economics.EconomicsInvoice;
import org.atmosphere.config.service.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EconomicsAPI {

    @Autowired
    RestTemplate restTemplate;

    public EconomicsInvoice getInvoices(String date) {
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("X-AppSecretToken", "GCCmf2TIXfrY3D9jEiqss8gUPa59rvBFbYAEjF1h7zQ1");
        requestHeaders.add("X-AgreementGrantToken", "B03oSVDidmk53uOIdMV9ptnI2hlVQykGdTvmisrtFq01");

        HttpEntity request = new HttpEntity(requestHeaders);

        //return restTemplate.getForObject("https://restapi.e-conomic.com/accounts/2101/accounting-years/2019_6_2020/entries?pagesize=500", EconomicsInvoice.class);

        String url = "https://restapi.e-conomic.com/accounts/2101/accounting-years/"+date+"/entries?pagesize=800";

        ResponseEntity<EconomicsInvoice> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                EconomicsInvoice.class
        );

        return response.getBody();
    }

}
