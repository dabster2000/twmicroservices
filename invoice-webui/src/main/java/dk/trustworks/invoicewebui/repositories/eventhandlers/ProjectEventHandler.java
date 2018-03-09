package dk.trustworks.invoicewebui.repositories.eventhandlers;

import dk.trustworks.InvoiceWebUIApplication;
import dk.trustworks.invoicewebui.model.News;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RepositoryEventHandler(Project.class)
public class ProjectEventHandler {

    private static final Logger log = LoggerFactory.getLogger(InvoiceWebUIApplication.class);

    private NewsRepository newsRepository;

    @Autowired
    public ProjectEventHandler(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @HandleBeforeSave
    public void clearNews(Project project) {
        log.info("clearNews project = " + project);
        List<News> news = newsRepository.findFirstBySha512(project.getUuid() + LocalDate.now().withDayOfMonth(1));
        newsRepository.delete(news);
    }
}
