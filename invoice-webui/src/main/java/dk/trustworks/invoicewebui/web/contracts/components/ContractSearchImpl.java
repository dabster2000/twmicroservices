package dk.trustworks.invoicewebui.web.contracts.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
@SpringUI
public class ContractSearchImpl extends ContractSearchDesign {

    @Autowired
    private ClientService clientService;

    public ContractSearchImpl() {
    }

    @PostConstruct
    public void init() {
        getSelClient().setItems(clientService.findByActiveTrue());
    }
}
