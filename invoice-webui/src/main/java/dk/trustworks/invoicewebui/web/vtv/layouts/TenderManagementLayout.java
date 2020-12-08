package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.network.rest.KnowledgeRestService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.knowledge.components.CertificationTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class TenderManagementLayout extends VerticalLayout {

    @Autowired
    private KnowledgeRestService knowledgeRestService;

    @Autowired
    private UserService userService;

    public TenderManagementLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();
        ResponsiveColumn column = row.addColumn().withDisplayRules(12, 12, 12, 12);

        VerticalLayout certificationsTable = new CertificationTable(knowledgeRestService, userService).createCertificationsTable();
        column.withComponent(new MVerticalLayout(new BoxImpl().instance(certificationsTable)));

        this.addComponent(responsiveLayout);
        return this;
    }


}

