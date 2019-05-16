package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TreeGrid;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.model.stats.BudgetItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    Map<String, BudgetItem> budgetItemMap = new HashMap<>();
    public TreeGrid<BudgetItem> createRevenuePerConsultantChart() {

        Set<LocalDate> dates = new TreeSet<>();
        Map<String, BudgetItem> userList = new HashMap<>();
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        for (User user : userService.findAll()) {
            userList.put(user.getUsername(), new BudgetItem(user.getUsername(), new ArrayList<>(), new ArrayList<>()));

            double budgetHoursSum = 0.0;
            double budgetAmountSum = 0.0;
            do {
                for (BudgetDocument budgetDocument : statisticsService.getConsultantBudgetDataByMonth(user, startDate)) {
                    double budgetHours = budgetDocument.getBudgetHours();
                    double budgetAmount = budgetDocument.getRate() * budgetHours;

                    budgetAmountSum += budgetAmount;
                    budgetHoursSum += budgetHours;

                    userList.get(user.getUsername()).getClients().add(new BudgetItem(budgetDocument.getClient().getName(), new ));
                }



                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now()));
        }


        List<BudgetDocument> budgetDocumentList = statisticsService.getBudgetData().stream().sorted(Comparator.comparing(BudgetDocument::getMonth)).collect(Collectors.toList());
        for (BudgetDocument budgetDocument : budgetDocumentList) {
            userList.putIfAbsent(budgetDocument.getUser().getUsername(), new BudgetItem(budgetDocument.getUser().getUsername(), new double[budgetDocumentList.size()], new double[budgetDocumentList.size()], new double[budgetDocumentList.size()]));
            BudgetItem budgetItem = userList.get(budgetDocument.getUser());
            BudgetItem clientItem = new BudgetItem(budgetDocument.getClient().getName(), new double[budgetDocumentList.size()], new double[budgetDocumentList.size()], new double[budgetDocumentList.size()]);

            budgetItem.getClients().add(clientItem);
        }


        TreeGrid<BudgetItem> treeGrid = new TreeGrid<>();
        treeGrid.setItems(generateProjectsForYears(2010, 2016), BudgetItem::getClients);

        treeGrid.addColumn(BudgetItem::getUser).setCaption("Project Name").setId("name-column");
        for (int i = 0; i < DateUtils.getMonthNames(); i++) {

        }
        treeGrid.addColumn(budgetItem -> budgetItem).setCaption("Hours Done");
        treeGrid.addColumn(BudgetItem::getLastModified).setCaption("Last Modified");


        return treeGrid;
    }

}