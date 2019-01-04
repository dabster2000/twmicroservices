package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkService {

    private final WorkRepository workRepository;

    @Autowired
    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Transactional
    public Work saveWork(Work work) {
        Work existingWork = workRepository.findByDayAndMonthAndYearAndUserAndTask(work.getDay(), work.getMonth(), work.getYear(), work.getUser(), work.getTask());
        if(existingWork!=null) {
            existingWork.setWorkduration(work.getWorkduration());
            work = existingWork;
        }
        workRepository.save(work);
        return work;
    }

    @Cacheable("workdaysInMonth")
    public int getWorkDaysInMonth(String useruuid, LocalDate month) {
        int weekDays = DateUtils.countWeekDays(month, month.plusMonths(1));
        List<Work> workList = workRepository.findByYearAndMonthAndUserAndTasks(month.getYear(), month.getMonthValue()-1, useruuid, "02bf71c5-f588-46cf-9695-5864020eb1c4", "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
        double vacationAndSickdays = workList.stream().mapToDouble(Work::getWorkduration).sum() / 7.4;
        weekDays -= vacationAndSickdays;
        return weekDays;
    }

    public List<Work> findVacationByUser(User user) {
        return workRepository.findByUserAndTasks(user.getUuid(), "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
    }

}
