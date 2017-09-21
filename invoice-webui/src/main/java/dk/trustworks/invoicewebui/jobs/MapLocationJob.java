package dk.trustworks.invoicewebui.jobs;

import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.addon.leaflet.LImageOverlay;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 12/09/2017.
 */

@Component
public class MapLocationJob {

    private static final Logger log = LoggerFactory.getLogger(MapLocationJob.class);

    Map<String, Point> addresses;

    @Autowired
    private ProjectRepository projectRepository;

    public MapLocationJob() {
        /*
        addresses = new HashMap<>();

        for (Project project : projectRepository.findAllByActiveTrueOrderByNameAsc()) {
            if(project.getLatitude() == 0.0) continue;
            double lat = project.getLatitude();//55.707043;
            double lon = project.getLongitude(); //12.589604000000008;
            addresses.put(project.getAddress(), new Point(lat, lon));
        }
        */
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {

    }
}
