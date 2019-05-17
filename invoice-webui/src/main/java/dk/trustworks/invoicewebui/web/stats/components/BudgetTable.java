package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TreeGrid;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import dk.trustworks.invoicewebui.web.model.stats.BudgetItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class BudgetTable {

    private final StatisticsService statisticsService;

    private final UserService userService;

    @Autowired
    public BudgetTable(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    public TreeGrid<BudgetItem> createRevenuePerConsultantChart() {
        Map<String, BudgetItem> userList = new HashMap<>();
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        int months = (int) ChronoUnit.MONTHS.between(startDate, LocalDate.now())+2;
        for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
            startDate = LocalDate.of(2014, 7, 1);
            int countMonths = 0;

            userList.put(user.getUsername(), new BudgetItem(Math.random(), user.getUsername(), new double[months], new double[months]));

            Map<String, BudgetItem> clientHoursMap = new HashMap<>();
            do {
                double budgetHoursSum = 0.0;
                double budgetAmountSum = 0.0;

                for (BudgetDocument budgetDocument : statisticsService.getConsultantBudgetDataByMonth(user, startDate)) {
                    double budgetHours = budgetDocument.getBudgetHours();
                    double budgetAmount = budgetDocument.getRate() * budgetHours;

                    budgetAmountSum += budgetAmount;
                    budgetHoursSum += budgetHours;

                    if(!clientHoursMap.containsKey(budgetDocument.getClient().getName())) {
                        clientHoursMap.put(budgetDocument.getClient().getName(), new BudgetItem(Math.random(), budgetDocument.getClient().getName(), new double[months], new double[months]));
                        userList.get(user.getUsername()).getClients().add(clientHoursMap.get(budgetDocument.getClient().getName()));
                    }

                    BudgetItem clientHoursArray = clientHoursMap.get(budgetDocument.getClient().getName());

                    clientHoursArray.getBudgetHours()[countMonths] += NumberUtils.round(budgetHours, 0);
                    clientHoursArray.getBudgetAmount()[countMonths] += NumberUtils.round(budgetAmount, 0);
                }

                countMonths++;
                userList.get(user.getUsername()).getBudgetAmount()[countMonths] = NumberUtils.round(budgetAmountSum, 0);
                userList.get(user.getUsername()).getBudgetHours()[countMonths] = NumberUtils.round(budgetHoursSum, 0);
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now()));
        }

        TreeGrid<BudgetItem> treeGrid = new TreeGrid<>();
        treeGrid.setWidth(100, Sizeable.Unit.PERCENTAGE);
        treeGrid.setItems(userList.values(), BudgetItem::getClients);

        treeGrid.addColumn(BudgetItem::getName).setCaption("Project Name").setId("name-column");
        int c = 0;
        for (int i = months-12; i < months; i++) {
            int finalI = i;
            treeGrid.addColumn(budgetItem -> budgetItem.getValueArray()[finalI]).setCaption(DateUtils.stringIt(LocalDate.of(2014,7,1).plusMonths(i), "MMM/yy")).setId(c+"");
            c++;
        }

        return treeGrid;
    }

}