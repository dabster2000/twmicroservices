package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.ProjectSummary;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("web-api")
public interface ProjectSummaryClient {
    //@RequestMapping(value = "/invoices/search/findByYearAndMonth", method = RequestMethod.GET)
    //List<Invoice> findByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

    @Cacheable("projectsummaries")
    @RequestMapping(method = RequestMethod.GET, value = "/projectsummaries/search/loadProjectSummaryByYearAndMonth")
    List<ProjectSummary> loadProjectSummaryByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

    @Async
    @CacheEvict(cacheNames={"invoices","projectsummaries"}, allEntries=true)
    @RequestMapping(method = RequestMethod.GET, value = "/projectsummaries/onMethod/createInvoiceFromProject")
    public void createInvoiceFromProject(@RequestParam("projectuuid") String projectuuid, @RequestParam("year") int year, @RequestParam("month") int month);

    @Async
    @CacheEvict(cacheNames={"invoices","projectsummaries"}, allEntries=true)
    @RequestMapping(value = "/invoices/methodOn/createCreditNota", method = RequestMethod.POST)
    public void createCreditNote(@RequestBody Invoice invoice);
}
