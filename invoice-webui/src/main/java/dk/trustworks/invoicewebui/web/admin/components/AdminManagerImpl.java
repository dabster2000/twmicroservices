package dk.trustworks.invoicewebui.web.admin.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.admin.layout.DocumentLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class AdminManagerImpl extends VerticalLayout {

    @Autowired
    private UserLayout userLayout;

    @Autowired
    private PurposeLayout purposeLayout;

    @Autowired private DocumentLayout documentLayout;

    ResponsiveLayout responsiveLayout;

    ResponsiveRow buttonRow;

    private ResponsiveRow employeeContentRow;
    private ResponsiveRow slackContentRow;
    private ResponsiveRow docsContentRow;
    private ResponsiveRow purposeContentRow;
    private ResponsiveRow budgContentRow;

    public AdminManagerImpl() {
    }

    @Transactional
    @PostConstruct
    public void init() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        buttonRow = responsiveLayout.addRow();

        employeeContentRow = responsiveLayout.addRow();
        slackContentRow = responsiveLayout.addRow();
        slackContentRow.setVisible(false);
        purposeContentRow = responsiveLayout.addRow();
        purposeContentRow.setVisible(false);
        budgContentRow = responsiveLayout.addRow();
        budgContentRow.setVisible(false);
        docsContentRow = responsiveLayout.addRow();
        docsContentRow.setVisible(false);

        final Button btnEmployee = new MButton(MaterialIcons.VERIFIED_USER, "employees", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top").withEnabled(false);
        final Button btnSlack = new MButton(MaterialIcons.CALL, "slack comms", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnBudget = new MButton(MaterialIcons.SHOPPING_CART, "mmm", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnDocuments = new MButton(MaterialIcons.ARCHIVE, "Documents", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnPurpose = new MButton(MaterialIcons.TRENDING_UP, "key purpose", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");

        btnEmployee.addClickListener(event -> {
            setNewButtonPressState(btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, event, employeeContentRow);
        });
        btnSlack.addClickListener(event -> {
            setNewButtonPressState(btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, event, slackContentRow);
        });
        btnBudget.addClickListener(event -> {
            setNewButtonPressState(btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, event, budgContentRow);
        });
        btnDocuments.addClickListener(event -> {
            setNewButtonPressState(btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, event, docsContentRow);
        });
        btnPurpose.addClickListener(event -> {
            setNewButtonPressState(btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, event, purposeContentRow);
        });

        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnEmployee);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnSlack);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnPurpose);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnDocuments);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(new MButton().withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top"));
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(new MButton().withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top"));

        userLayout.createEmployeeLayout(employeeContentRow);
        purposeLayout.createEmployeeLayout(purposeContentRow);
        docsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(documentLayout);

        addComponent(responsiveLayout);
    }

    private void setNewButtonPressState(Button btnWork, Button btnKnowledge, Button btnBudget, Button btnDocuments, Button btnPurpose, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button btnWork, Button btnKnowledge, Button btnBudget, Button btnDocuments, Button btnPurpose) {
        btnWork.setEnabled(true);
        btnKnowledge.setEnabled(true);
        btnPurpose.setEnabled(true);
        btnBudget.setEnabled(true);
        btnDocuments.setEnabled(true);
    }

    private void hideAllDynamicRows() {
        employeeContentRow.setVisible(false);
        slackContentRow.setVisible(false);
        purposeContentRow.setVisible(false);
        docsContentRow.setVisible(false);
        budgContentRow.setVisible(false);
    }
}

class Employee {
    String name;
    String status;
    int hours;
    int salary;

    public Employee() {
    }

    public Employee(String name, String status, int hours, int salary) {
        this.name = name;
        this.status = status;
        this.hours = hours;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}