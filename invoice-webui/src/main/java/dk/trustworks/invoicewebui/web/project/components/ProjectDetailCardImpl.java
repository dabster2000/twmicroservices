package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.data.Binder;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import dk.trustworks.invoicewebui.network.dto.Logo;
import dk.trustworks.invoicewebui.network.dto.Project;
import dk.trustworks.invoicewebui.network.dto.User;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Created by hans on 13/08/2017.
 */

public class ProjectDetailCardImpl extends ProjectDetailCardDesign {

    public ProjectDetailCardImpl(Project project, List<User> users, Logo logo) {

        Binder<Project> projectBinder = new Binder<>();

        projectBinder.forField(getTxtName()).bind(Project::getName, Project::setName);
        projectBinder.forField(getTxtBudget())
                .withConverter(new MyConverter())
                .bind(Project::getBudget, Project::setBudget);
        projectBinder.forField(getChkActive()).bind(Project::getActive, Project::setActive);
        projectBinder.forField(getSelStartDate()).bind(Project::getStartdate, Project::setStartdate);
        projectBinder.forField(getSelEndDate()).bind(Project::getEnddate, Project::setEnddate);

        projectBinder.readBean(project);

        getSelRelationManager().setItems(users);
        getSelRelationManager().setItemCaptionGenerator(User::getUsername);
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
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            } catch (ParseException e) {
                e.printStackTrace();
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
            return String.valueOf(formatter.format(aDouble));
        }
    }
}
