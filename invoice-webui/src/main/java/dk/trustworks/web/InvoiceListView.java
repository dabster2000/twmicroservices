package dk.trustworks.web;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.network.clients.ProjectSummaryClient;
import dk.trustworks.network.dto.ProjectSummary;
import dk.trustworks.web.model.YearMonthSelect;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 09/07/2017.
 */
//@SpringComponent
//@UIScope
public class InvoiceListView extends VerticalLayout {

    private final ProjectSummaryClient projectSummaryClient;
    private List<ProjectSummary> invoices;

    private CssLayout cssLayout = new CssLayout();

    @Autowired
    public InvoiceListView(ProjectSummaryClient projectSummaryClient) {
        this.projectSummaryClient = projectSummaryClient;
        addComponent(createYearMonthSelector());

        cssLayout.setStyleName("invoice-list-css");

        addComponent(cssLayout);
    }


    private ComboBox<YearMonthSelect> createYearMonthSelector() {
        List<YearMonthSelect> yearMonthSelectList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 2, 1);
        while (startDate.isBefore(LocalDate.now())) {
            yearMonthSelectList.add(new YearMonthSelect(startDate));
            startDate = startDate.plusMonths(1);
        }

        ComboBox<YearMonthSelect> selYearMonth = new ComboBox<>("Select your country", yearMonthSelectList);
        selYearMonth.setEmptySelectionAllowed(false);
        selYearMonth.setSelectedItem(yearMonthSelectList.get(yearMonthSelectList.size()-1));
        selYearMonth.addValueChangeListener(event -> {
            invoices = projectSummaryClient.loadProjectSummaryByYearAndMonth(event.getValue().getDate().getYear(), event.getValue().getDate().getMonthValue()-1);
            Notification.show("Invoices loaded: ",
                    String.valueOf(invoices.size()),
                    Notification.Type.TRAY_NOTIFICATION);
            System.out.println("invoices.size() = " + invoices.size());
            if(invoices != null) {
                cssLayout.removeAllComponents();
                for (ProjectSummary invoice : invoices) {
                    //cssLayout.addComponent(new InvoicePreviewView(invoice));
                }
            }
        });
        return selYearMonth;
    }
}
