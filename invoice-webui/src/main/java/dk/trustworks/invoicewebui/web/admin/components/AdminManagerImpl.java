package dk.trustworks.invoicewebui.web.admin.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class AdminManagerImpl extends VerticalLayout {

    @Autowired
    private CountEmployeesJob countEmployees;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoCardImpl userInfoCard;

    @Autowired
    private UserSalaryCardImpl userSalaryCard;

    @Autowired
    private UserStatusCardImpl userStatusCard;

    @Autowired
    private UserPhotoCardImpl userPhotoCard;

    @Autowired
    private RoleCardImpl roleCard;

    ResponsiveLayout responsiveLayout;
    ComboBox<User> userComboBox;
    ResponsiveRow contentRow;

    public AdminManagerImpl() {
    }

    @Transactional
    @PostConstruct
    public void init() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        MButton addUserButton = new MButton("add user").withStyleName("flat", "friendly").withListener((Button.ClickListener) event -> {
            User user = new User();
            user.setUsername("new.new");
            userRepository.save(user);
            userComboBox.setItems(userRepository.findAll());
            userComboBox.setSelectedItem(user);
        });

        userComboBox = new ComboBox<>();
        userComboBox.setItems(userRepository.findByOrderByUsername());
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        responsiveLayout.addRow()
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(new MHorizontalLayout().withFullWidth().add(userComboBox).add(addUserButton));
        userComboBox.addValueChangeListener(event -> {
            contentRow.removeAllComponents();
            loadData();
        });
        contentRow = responsiveLayout.addRow();
        contentRow.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(getChart());
        addComponent(responsiveLayout);
    }

    protected Component getChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        LocalDate periodStart = LocalDate.of(2014, 02, 01);
        LocalDate periodEnd = LocalDate.now();
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Employee Growth");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Employees");

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            List<User> usersByLocalDate = countEmployees.getUsersByLocalDate(currentDate);
            revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")), usersByLocalDate.size()));
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private void loadData() {
        createUserInfoCard();
        createUserSalaryCard();
        createRoleCard();
        createUserStatusCard();
        createUserPhotoCard();
    }

    private void createUserSalaryCard() {
        userSalaryCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userSalaryCard);
    }

    private void createUserInfoCard() {
        userInfoCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userInfoCard);
    }

    private void createRoleCard() {
        roleCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(roleCard);
    }

    private void createUserStatusCard() {
        userStatusCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userStatusCard);
    }

    private void createUserPhotoCard() {
        userPhotoCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userPhotoCard);

    }
}
