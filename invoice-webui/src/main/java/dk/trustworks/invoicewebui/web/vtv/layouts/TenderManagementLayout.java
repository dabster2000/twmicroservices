package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;
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

        List<GridItem> gridItems = ckoExpenseRepository.findAll().stream().filter(ckoExpense -> ckoExpense.getCertified() == 1).map(ckoExpense -> new GridItem(userService.findByUUID(ckoExpense.getUseruuid(), true).getUsername(), ckoExpense.getDescription(), DateUtils.stringIt(ckoExpense.getEventdate()))).collect(Collectors.toList());
        Grid<GridItem> grid = new Grid<>();
        Grid.Column<GridItem, String> consultantColumn = grid.addColumn(GridItem::getConsultant);
        consultantColumn.setCaption("Consultant");
        Grid.Column<GridItem, String> descriptionColumn = grid.addColumn(GridItem::getDescription);
        descriptionColumn.setCaption("Certification");
        descriptionColumn.setExpandRatio(1);
        Grid.Column<GridItem, String> dateColumn = grid.addColumn(GridItem::getDate);
        dateColumn.setCaption("Date");
        grid.setSizeFull();
        grid.setItems(gridItems);
        column.withComponent(new MVerticalLayout(
                new MLabel("Consultant certifications").withStyleName("H2"),
                new BoxImpl().instance(grid))
        );

        this.addComponent(responsiveLayout);
        return this;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GridItem {
    String consultant;
    String description;
    String date;
}
