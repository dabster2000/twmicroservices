package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.ProjectSummary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("web-api")
public interface ProjectSummaryClient {
    //@RequestMapping(value = "/invoices/search/findByYearAndMonth", method = RequestMethod.GET)
    //List<Invoice> findByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

    //@Cacheable("projectsummaries")
    @RequestMapping(method = RequestMethod.GET, value = "/projectsummaries/search/loadProjectSummaryByYearAndMonth")
    List<ProjectSummary> loadProjectSummaryByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

    @RequestMapping(method = RequestMethod.GET, value = "/projectsummaries/onMethod/createInvoiceFromProject")
    void createInvoiceFromProject(@RequestParam("projectuuid") String projectuuid, @RequestParam("year") int year, @RequestParam("month") int month);

    @RequestMapping(value = "/invoices/methodOn/createCreditNota", method = RequestMethod.POST)
    void createCreditNota(@RequestBody Invoice invoice);
}
