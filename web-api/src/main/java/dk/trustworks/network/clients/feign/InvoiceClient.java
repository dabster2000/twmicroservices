package dk.trustworks.network.clients.feign;

import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceStatus;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

/**
 * Created by hans on 11/07/2017.
 */

@FeignClient("invoice-service")
public interface InvoiceClient {
    @RequestMapping(value = "/invoices/search/findByYearAndMonth", method = GET)
    Resources<Resource<Invoice>> findByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

    @RequestMapping(method = POST, value = "/invoices")
    void saveInvoice(@RequestBody Invoice invoice);

    @RequestMapping(method = PATCH, value = "/invoices/{uuid}")
    void updateInvoice(@PathVariable("uuid") String uuid, @RequestBody Invoice invoice);
}
