package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
