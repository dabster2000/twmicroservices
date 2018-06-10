package dk.trustworks.invoicewebui.web.contracts.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
@SpringUI
public class ContractSearchImpl extends ContractSearchDesign {

    @Autowired
    private ClientRepository clientRepository;

    public ContractSearchImpl() {
    }

    @PostConstruct
    public void init() {
        getSelClient().setItems(clientRepository.findByActiveTrueOrderByName());
    }
}
