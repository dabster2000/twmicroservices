package dk.trustworks.eventhandlers;

import dk.trustworks.model.Work;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 29/06/2017.
 */

@Component
@RepositoryEventHandler
public class WorkEventHandler {

    @HandleBeforeCreate
    public void handleWorkSave(Work work) {
        System.out.println("WorkEventHandler.handleWorkSave");
        System.out.println("work = [" + work + "]");
        work.setUuid(UUID.randomUUID().toString());
        work.setCreated(new Timestamp(new Date().getTime()));
    }

}
