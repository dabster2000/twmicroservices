package dk.trustworks.invoicewebui.web.employee.components.tabs;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.ITBudgetCategory;
import dk.trustworks.invoicewebui.model.ItBudgetItem;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ItBudgetStatus;
import dk.trustworks.invoicewebui.repositories.ITBudgetCategoryRepository;
import dk.trustworks.invoicewebui.repositories.ITBudgetItemRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.employee.components.parts.AddBudgetItem;
import dk.trustworks.invoicewebui.web.employee.components.parts.BudgetItemImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.ImportantMessageBoxImpl;
import org.vaadin.viritin.button.MButton;

import java.time.LocalDate;

import static com.jarektoro.responsivelayout.ResponsiveLayout.DisplaySize.LG;
import static com.jarektoro.responsivelayout.ResponsiveLayout.DisplaySize.MD;
import static dk.trustworks.invoicewebui.model.enums.ItBudgetStatus.*;
import static dk.trustworks.invoicewebui.utils.NumberConverter.*;

@SpringUI
@SpringComponent
public class ItBudgetTab {

    private final UserService userService;

    private final ITBudgetItemRepository itBudgetItemRepository;

    private final ITBudgetCategoryRepository itBudgetCategoryRepository;

    private ResponsiveRow userRow;
    private ResponsiveRow messageRow;
    private LocalDate currentDate;
    private ResponsiveRow budgetCardsRow;
    private User user;

    public ItBudgetTab(UserService userService, ITBudgetItemRepository itBudgetItemRepository, ITBudgetCategoryRepository itBudgetCategoryRepository) {
        this.userService = userService;
        this.itBudgetItemRepository = itBudgetItemRepository;
        this.itBudgetCategoryRepository = itBudgetCategoryRepository;
    }

    public ResponsiveLayout getTabLayout() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        userRow = responsiveLayout.addRow();
        messageRow = responsiveLayout.addRow();
        budgetCardsRow = responsiveLayout.addRow();

        currentDate = LocalDate.now();

        createUserSelectBox();

        //createEquipmentCards();
        return responsiveLayout;
    }

    public ResponsiveLayout getTabLayout(User user) {
        this.user = user;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        messageRow = responsiveLayout.addRow();

        currentDate = LocalDate.now();

        budgetCardsRow = responsiveLayout.addRow();
        createEquipmentCards();
        return responsiveLayout;
    }

    private void createUserSelectBox() {
        ResponsiveColumn column = userRow.addColumn().withDisplayRules(12, 12, 4, 4).withOffset(MD, 1).withOffset(LG, 1);

        ComboBox<User> comboBox = new ComboBox<>("Select User: ");
        comboBox.setItemCaptionGenerator(User::getUsername);
        comboBox.setItems(userService.findAll());
        comboBox.addValueChangeListener(event -> {
            if(!event.getSource().isEmpty()) {
                user = event.getValue();
                createEquipmentCards();
            }
        });

        column.withComponent(comboBox);
    }

    private void createImportantMessgeBox(int budgetLeft) {
        messageRow.removeAllComponents();
        ImportantMessageBoxImpl importantMessageBox = new ImportantMessageBoxImpl(formatCurrency(budgetLeft) + " (w/tax)", "Available IT Budget").withHalftoneSecondline();
        importantMessageBox.withComponent(new MButton("Add Equipment", event -> {
            Window win = new Window("Add Equipment");
            win.setModal(true);
            AddBudgetItem addBudgetItem = new AddBudgetItem();
            addBudgetItem.getCbCategory().setItems(itBudgetCategoryRepository.findAll());
            addBudgetItem.getCbCategory().setItemCaptionGenerator(ITBudgetCategory::getLongName);
            addBudgetItem.getBtnCancel().addClickListener(event1 -> win.close());
            addBudgetItem.getBtnAdd().addClickListener(event1 -> {
                itBudgetItemRepository.save(new ItBudgetItem(user,
                        addBudgetItem.getCbCategory().getValue(),
                        addBudgetItem.getTxtDescription().getValue(),
                        convertDoubleToInt(parseDouble(addBudgetItem.getTxtAmount().getValue())),
                        ACTIVE,
                        addBudgetItem.getDate().getValue()));
                win.close();
                createEquipmentCards();
            });
            win.setContent(addBudgetItem);
            UI.getCurrent().addWindow(win);
        }));

        messageRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(importantMessageBox);
    }

    private void createEquipmentCards() {
        int budgetLeft = 25000;
        budgetCardsRow.removeAllComponents();
        for (ItBudgetItem budgetItem : itBudgetItemRepository.findByUser(user)) {
            LocalDate tempDate = currentDate.minusMonths(budgetItem.getCategory().getLifespan());
            BudgetItemImpl item = new BudgetItemImpl(budgetItem);
            if(budgetItem.getInvoicedate().isAfter(tempDate) && budgetItem.getStatus().equals(ACTIVE)) {
                budgetLeft -= budgetItem.getPrice();
                item.getBtnCrashed().addClickListener(event -> updateItemStatus(budgetItem, BROKEN));
                item.getBtnAmortized().addClickListener(event -> updateItemStatus(budgetItem, AMORTIZED));
                item.getBtnLost().addClickListener(event -> updateItemStatus(budgetItem, LOST));
                item.getBtnDelete().addClickListener(event -> {
                    itBudgetItemRepository.delete(budgetItem.getId());
                    createEquipmentCards();
                });
            } else {
                item.getBtnLost().setEnabled(false);
                item.getBtnAmortized().setEnabled(false);
                item.getBtnCrashed().setEnabled(false);
            }
            budgetCardsRow.addColumn().withDisplayRules(12, 12, 6, 4).withComponent(item);
        }
        createImportantMessgeBox(budgetLeft);
    }

    private void updateItemStatus(ItBudgetItem budgetItem, ItBudgetStatus status) {
        budgetItem.setStatus(status);
        itBudgetItemRepository.save(budgetItem);
        createEquipmentCards();
    }
}
