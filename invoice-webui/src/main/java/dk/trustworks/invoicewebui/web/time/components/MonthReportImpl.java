package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.time.model.UserHourItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class MonthReportImpl extends MonthReportDesign {

    @Autowired
    private WorkRepository workRepository;

    public MonthReportImpl() {

    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public void init(String projectUUID, LocalDate localDate) {
        List<Work> workList = workRepository.findByPeriod(localDate.withDayOfMonth(1).toString(), localDate.withDayOfMonth(localDate.lengthOfMonth()).toString());
        List<UserHourItem> userHourItems = new ArrayList<>();

        for (Work work : workList) {
            if(work.getTask().getProject().getUuid().equals(projectUUID)) {
                UserHourItem userHourItem = null;
                for (UserHourItem item : userHourItems) {
                    if (item.getUserUUID().equals(work.getUser().getUuid())
                            && item.getTaskUUID().equals(work.getTask().getUuid())) {
                        userHourItem = item;
                    }
                }
                if (userHourItem == null) {
                    userHourItem = new UserHourItem(work.getUser().getUuid(),
                            work.getTask().getUuid(),
                            work.getTask().getName(),
                            work.getUser().getUsername());
                    userHourItems.add(userHourItem);
                }
                userHourItem.addHours(work.getWorkduration());
            }
        }

        getGetReportGrid().setItems(userHourItems);
    }
}
