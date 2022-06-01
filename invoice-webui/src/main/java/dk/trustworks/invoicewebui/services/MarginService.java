package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.network.dto.ClientMarginResult;
import dk.trustworks.invoicewebui.network.rest.MarginRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarginService implements InitializingBean {

    private static MarginService instance;

    private final MarginRestService marginRestService;

    @Autowired
    public MarginService(MarginRestService marginRestService) {
        this.marginRestService = marginRestService;
    }

    public int calculateCapacityByMonthByUser(String useruuid, int rate) {
        return marginRestService.calculateMargin(useruuid, rate);
    }

    public List<ClientMarginResult> calculateMarginPerCustomer(int fiscalYear) {
        return marginRestService.calculateMarginPerClient(fiscalYear);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static MarginService get() {
        return instance;
    }
}
