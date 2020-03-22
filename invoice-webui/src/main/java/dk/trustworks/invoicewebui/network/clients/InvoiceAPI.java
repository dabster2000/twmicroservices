package dk.trustworks.invoicewebui.network.clients;

import com.fasterxml.jackson.databind.SerializationFeature;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.clients.model.InvoiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvoiceAPI {

    @Autowired
    RestTemplate restTemplate;

    public byte[] createInvoicePDF(Invoice invoice) {
        InvoiceDTO invoiceDTO = new InvoiceDTO(invoice);


        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");

        HttpEntity<InvoiceDTO> entity = new HttpEntity<>(invoiceDTO, requestHeaders);

        ResponseEntity<byte[]> exchange = restTemplate.exchange("https://invoice-generator.com", HttpMethod.POST, entity, byte[].class);
        return exchange.getBody();
    }

}
