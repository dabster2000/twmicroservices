package dk.trustworks.invoicewebui.web.admin.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.admin.layout.DocumentLayout;
import dk.trustworks.invoicewebui.web.admin.layout.PurposeLayout;
import dk.trustworks.invoicewebui.web.admin.layout.TalentPassionLayout;
import dk.trustworks.invoicewebui.web.admin.layout.UserLayout;
import dk.trustworks.invoicewebui.web.employee.components.tabs.ItBudgetTab;
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
    private UserService userService;

    @Autowired
    private UserLayout userLayout;

    @Autowired
    private PurposeLayout purposeLayout;

    @Autowired
    private DocumentLayout documentLayout;

    @Autowired
    private ItBudgetTab itBudgetTab;

    @Autowired
    private TalentPassionLayout talentPassionLayout;

    ResponsiveLayout responsiveLayout;

    ResponsiveRow buttonRow;

    private ResponsiveRow employeeContentRow;
    private ResponsiveRow slackContentRow;
    private ResponsiveRow docsContentRow;
    private ResponsiveRow purposeContentRow;
    private ResponsiveRow budgContentRow;
    private ResponsiveRow talentPassionContentRow;

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
        talentPassionContentRow = responsiveLayout.addRow();
        talentPassionContentRow.setVisible(false);

        final Button btnEmployee = new MButton(MaterialIcons.VERIFIED_USER, "employees", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top").withEnabled(false);
        final Button btnSlack = new MButton(MaterialIcons.CALL, "slack comms", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnBudget = new MButton(MaterialIcons.SHOPPING_CART, "IT Budget", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnDocuments = new MButton(MaterialIcons.ARCHIVE, "Documents", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnPurpose = new MButton(MaterialIcons.TRENDING_UP, "key purpose", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnTalentPassion = new MButton(MaterialIcons.TRENDING_UP, "talent & passion", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");

        btnEmployee.addClickListener(event -> {
            setNewButtonPressState(event, employeeContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });
        btnSlack.addClickListener(event -> {
            setNewButtonPressState(event, slackContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });
        btnBudget.addClickListener(event -> {
            setNewButtonPressState(event, budgContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });
        btnDocuments.addClickListener(event -> {
            setNewButtonPressState(event, docsContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });
        btnPurpose.addClickListener(event -> {
            setNewButtonPressState(event, purposeContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });
        btnTalentPassion.addClickListener(event -> {
            setNewButtonPressState(event, talentPassionContentRow, btnEmployee, btnSlack, btnBudget, btnDocuments, btnPurpose, btnTalentPassion);
        });

        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnEmployee);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnSlack);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnPurpose);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnDocuments);
        buttonRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnBudget);
        buttonRow.addColumn().withDisplayRules(12, 6 ,2, 2).withComponent(btnTalentPassion);


        userLayout.createEmployeeLayout(employeeContentRow);
        purposeLayout.createEmployeeLayout(purposeContentRow);
        docsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(documentLayout.init(userService.findAll(true)));
        budgContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(itBudgetTab.getTabLayout());
        talentPassionContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(talentPassionLayout.getLayout());
        addComponent(responsiveLayout);
    }

    private void setNewButtonPressState(Button.ClickEvent event, ResponsiveRow contentRow, Button... buttons) {
        hideAllDynamicRows();
        for (Button button : buttons) {
            button.setEnabled(true);
        }
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void hideAllDynamicRows() {
        employeeContentRow.setVisible(false);
        slackContentRow.setVisible(false);
        purposeContentRow.setVisible(false);
        docsContentRow.setVisible(false);
        budgContentRow.setVisible(false);
    }
}
