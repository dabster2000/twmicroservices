package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.data.*;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.DateResolution;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.UserService;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Created by hans on 13/08/2017.
 */

public class ProjectDetailCardImpl extends ProjectDetailCardDesign {

    private final Project project;
    private final ProjectService projectService;
    private final Binder<Project> projectBinder;

    public ProjectDetailCardImpl(Project project, List<User> users, Photo photo, ProjectService projectService, UserService userService) {
        this.project = project;
        this.projectService = projectService;

        if(photo !=null && photo.getPhoto()!=null && photo.getPhoto().length > 0) {
            getLogo().setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()),
                    "logo.jpg"));
        } else {
            getLogo().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }

        getSelRelationManager().setItems(userService.findAll());
        getSelRelationManager().setItemCaptionGenerator(User::getUsername);

        getSelStartDate().setDateFormat("yyyy-MM");
        getSelStartDate().setSizeFull();
        getSelStartDate().setRequiredIndicatorVisible(true);
        getSelStartDate().setResolution(DateResolution.MONTH);

        getSelEndDate().setDateFormat("yyyy-MM");
        getSelEndDate().setSizeFull();
        getSelEndDate().setRequiredIndicatorVisible(true);
        getSelEndDate().setResolution(DateResolution.MONTH);

        projectBinder = new Binder<>();

        projectBinder.forField(getTxtName()).bind(Project::getName, Project::setName);
        projectBinder.forField(getTxtBudget())
                .withConverter(new MyConverter())
                .bind(Project::getBudget, Project::setBudget);
        projectBinder.forField(getSelRelationManager()).bind(Project::getOwner, Project::setOwner);
        projectBinder.forField(getChkActive()).bind(Project::getActive, Project::setActive);
        projectBinder.forField(getChkMonthLock()).bind(Project::isLocked, Project::setLocked);
        //projectBinder.forField(getSelStartDate()).bind(Project::getStartdate, Project::setStartdate);
        //projectBinder.forField(getSelEndDate()).bind(Project::getEnddate, Project::setEnddate);
        projectBinder.forField(getTxtDescription()).bind(Project::getCustomerreference, Project::setCustomerreference);

        projectBinder.readBean(project);

        getSelRelationManager().setItems(users);
        getSelRelationManager().setItemCaptionGenerator(User::getUsername);
    }

    public void update() {
        try {
            projectBinder.writeBean(project);
            projectService.update(project);
            //newsRepository.deleteAll();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public class MyConverter implements Converter<String, Double> {
        @Override
        public Result<Double> convertToModel(String fieldValue, ValueContext context) {
            System.out.println("MyConverter.convertToModel");
            System.out.println("fieldValue = [" + fieldValue + "], context = [" + context + "]");
            // Produces a converted value or an error
            try {
                // ok is a static helper method that creates a Result
                NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
                return Result.ok(formatter.parse(fieldValue).doubleValue());
            } catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            }
        }

        @Override
        public String convertToPresentation(Double aDouble, ValueContext context) {
            System.out.println("MyConverter.convertToPresentation");
            System.out.println("aDouble = [" + aDouble + "], context = [" + context + "]");
            // Converting to the field type should always succeed,
            // so there is no support for returning an error Result.
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            return formatter.format(aDouble);
        }
    }
}
