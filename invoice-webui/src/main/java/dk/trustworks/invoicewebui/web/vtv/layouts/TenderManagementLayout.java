package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.knowledge.components.CertificationTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class TenderManagementLayout extends VerticalLayout {

    @Autowired
    private CKOExpenseRepository ckoExpenseRepository;

    @Autowired
    private UserService userService;

    public TenderManagementLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();
        ResponsiveColumn column = row.addColumn().withDisplayRules(12, 12, 12, 12);

        VerticalLayout certificationsTable = new CertificationTable(ckoExpenseRepository, userService).createCertificationsTable();
        column.withComponent(new MVerticalLayout(new BoxImpl().instance(certificationsTable)));

        this.addComponent(responsiveLayout);
        return this;
    }


}

