package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Budget;
import dk.trustworks.invoicewebui.network.dto.Taskworkerconstraint;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("client-service")
public interface BudgetClient {

    @RequestMapping(method = GET, value = "/budgets")
    Resources<Resource<Budget>> findAllBudgets();

}
