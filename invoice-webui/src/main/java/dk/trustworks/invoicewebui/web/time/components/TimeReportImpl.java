package dk.trustworks.invoicewebui.web.time.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class TimeReportImpl extends VerticalLayout {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MonthReportImpl monthReport;

    @Autowired
    private BudgetReportImpl budgetReport;

    ResponsiveLayout responsiveLayout;
    ComboBox<Project> projectComboBox;
    ResponsiveRow contentRow;
    DateField date;

    public TimeReportImpl() {
    }

    @Transactional
    @PostConstruct
    public void init() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        projectComboBox = new ComboBox<>();
        date = new DateField(null, LocalDate.now().withDayOfMonth(1));
        HorizontalLayout horizontalLayout = new HorizontalLayout(projectComboBox, date);
        projectComboBox.setItems(projectRepository.findAllByActiveTrueOrderByNameAsc());
        projectComboBox.setItemCaptionGenerator(Project::getName);
        responsiveLayout.addRow()
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(horizontalLayout);
        projectComboBox.addValueChangeListener(event -> {
            contentRow.removeAllComponents();
            loadData();
        });
        date.addValueChangeListener(event -> {
            contentRow.removeAllComponents();
            loadData();
        });
        contentRow = responsiveLayout.addRow();
        addComponent(responsiveLayout);
    }

    private void loadData() {
        createMonthReportCard();
        createBudgetReportCard();
    }

    private void createMonthReportCard() {
        monthReport.init(projectComboBox.getSelectedItem().get().getUuid(), date.getValue());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(monthReport);
    }

    private void createBudgetReportCard() {
        budgetReport.init(projectComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(budgetReport);
    }

}
