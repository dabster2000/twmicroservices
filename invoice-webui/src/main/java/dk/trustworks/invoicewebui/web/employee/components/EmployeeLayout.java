package dk.trustworks.invoicewebui.web.employee.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@SpringUI
public class EmployeeLayout extends VerticalLayout {

    private ResponsiveRow contentRow = null;

    private UserMonthReportImpl monthReport;

    @Autowired
    public EmployeeLayout(UserMonthReportImpl monthReport) {
        this.monthReport = monthReport;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        contentRow = responsiveLayout.addRow();
        addComponent(responsiveLayout);
        loadData();
    }

    private void loadData() {
        createMonthReportCard();
    }

    public EmployeeLayout init() {
        return this;
    }

    private void createMonthReportCard() {
        monthReport.init();
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(monthReport);
    }
}
