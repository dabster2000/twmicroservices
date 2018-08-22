package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.shared.data.sort.SortDirection;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;

/**
 * Created by hans on 09/09/2017.
 */


public class CKOExpenseImpl extends CKOExpenseDesign {

    private final CKOExpenseRepository ckoExpenseRepository;

    public CKOExpenseImpl(CKOExpenseRepository ckoExpenseRepository, User user) {
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.setVisible(false);

        getGridCKOExpenses().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size() > 0) {
                getHlAddBar().setVisible(false);
                getBtnDelete().setVisible(true);
            } else {
                getHlAddBar().setVisible(true);
                getBtnDelete().setVisible(false);
            }
        });

        getBtnDelete().addClickListener(event -> {
            this.ckoExpenseRepository.delete(getGridCKOExpenses().getSelectedItems());
            getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUser(user));
        });
        getBtnAddSalary().addClickListener(event -> {
            this.ckoExpenseRepository.save(new CKOExpense(getDfDate().getValue(), user, getTxtDescription().getValue(), Integer.parseInt(getTxtPrice().getValue()), CKOExpenseType.valueOf(getCbType().getValue())));
            getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUser(user));
        });

        getGridCKOExpenses().sort("eventdate", SortDirection.ASCENDING);

        this.setVisible(true);
        getGridCKOExpenses().setItems(ckoExpenseRepository.findCKOExpenseByUser(user));
    }
}
