package dk.trustworks.invoicewebui.web.employee.components.tabs;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.UserService;

import java.time.LocalDate;

@SpringUI
@SpringComponent
public class YourTrustworksTab {

    /*
     * Everage expenses: 31.200
     * Salary expenses: 60.000
     * Expenses per year = 1.094.400
     * Gross Margin = 60.000 * 12 = 720.000
     * = 72.000 bonus
     *
     * 810.000 = 67.500
     */

    private final UserService userService;

    private ResponsiveRow userRow;
    private ResponsiveRow messageRow;
    private LocalDate currentDate;
    private ResponsiveRow budgetCardsRow;
    private User user;

    public YourTrustworksTab(UserService userService) {
        this.userService = userService;
    }

    public ResponsiveLayout getTabLayout() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        userRow = responsiveLayout.addRow();
        messageRow = responsiveLayout.addRow();
        budgetCardsRow = responsiveLayout.addRow();

        currentDate = LocalDate.now();

        return responsiveLayout;
    }

    public ResponsiveLayout getTabLayout(User user) {
        this.user = user;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        messageRow = responsiveLayout.addRow();

        currentDate = LocalDate.now();

        budgetCardsRow = responsiveLayout.addRow();

        return responsiveLayout;
    }

}
