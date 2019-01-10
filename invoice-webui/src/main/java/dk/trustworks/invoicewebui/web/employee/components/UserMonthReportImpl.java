package dk.trustworks.invoicewebui.web.employee.components;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.model.UserHourItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class UserMonthReportImpl extends UserMonthReportDesign {

    private final User user;

    @Autowired
    private WorkService workService;

    public UserMonthReportImpl() {
        user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public void init() {
        getLblTitle().setValue(user.getFirstname() + " " + user.getLastname());
        getDate().setValue(LocalDate.now().withDayOfMonth(1));
        getDate().setResolution(DateResolution.MONTH);
        getDate().addValueChangeListener(event -> loadProjectReport(getDate().getValue()));
        loadProjectReport(getDate().getValue());
    }

    private void loadProjectReport(LocalDate localDate) {
        List<Work> workList = workService.findByPeriodAndUserUUID(localDate.withDayOfMonth(1), localDate.withDayOfMonth(localDate.lengthOfMonth()), user.getUuid());

        Map<String, UserHourItem> userHourItemMap = new HashMap<>();

        for (Work work : workList) {
            if(!userHourItemMap.containsKey(work.getTask().getUuid())) userHourItemMap.put(work.getTask().getUuid(),
                    new UserHourItem(work.getUser().getUuid(),
                            work.getTask().getUuid(),
                            work.getTask().getProject().getName() + " / " + work.getTask().getName(),
                            work.getUser().getUsername()));

            userHourItemMap.get(work.getTask().getUuid()).addHours(work.getWorkduration());

        }

        getGetReportGrid().setItems(userHourItemMap.values());
    }
}
