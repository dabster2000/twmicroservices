package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.Push;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.web.model.YearMonthSelect;
import dk.trustworks.invoicewebui.network.clients.ProjectSummaryClient;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.producttour.actions.TourActions;
import org.vaadin.addons.producttour.button.StepButton;
import org.vaadin.addons.producttour.shared.step.StepAnchor;
import org.vaadin.addons.producttour.step.Step;
import org.vaadin.addons.producttour.step.StepBuilder;
import org.vaadin.addons.producttour.tour.Tour;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hans on 12/07/2017.
 */
@Push
@SpringComponent
@UIScope
public class ProjectListImpl extends ProjectListDesign {

    private final ProjectSummaryClient projectSummaryClient;

    @Autowired
    public ProjectListImpl(ProjectSummaryClient projectSummaryClient) {
        super();
        this.projectSummaryClient = projectSummaryClient;
        List<YearMonthSelect> yearMonthList = createYearMonthSelector();
        cbSelectYearMonth.setItems(yearMonthList);
        cbSelectYearMonth.setItemCaptionGenerator(c -> c.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        cbSelectYearMonth.setEmptySelectionAllowed(false);
        cbSelectYearMonth.setSelectedItem(yearMonthList.get(1));
        cbSelectYearMonth.addValueChangeListener(event -> reloadData());

        Grid.Column colRegisteredAmount = gridProjectSummaryList.getColumn("registeredamount");
        colRegisteredAmount.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        colRegisteredAmount.setStyleGenerator(item -> "v-align-right");

        Grid.Column colInvoicedAmount = gridProjectSummaryList.getColumn("invoicedamount");
        colInvoicedAmount.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        colInvoicedAmount.setStyleGenerator(item -> "v-align-right");

        gridProjectSummaryList.setSelectionMode(Grid.SelectionMode.MULTI);
        gridProjectSummaryList.addSelectionListener(event -> {
            System.out.println("event = " + event);
            Set<ProjectSummary> selectedItems = event.getAllSelectedItems();
            switch (selectedItems.size()) {
                case 0:
                    btnCreateInvoice.setCaption("Create invoice");
                    btnCreateInvoice.setEnabled(false);
                    break;
                case 1:
                    btnCreateInvoice.setCaption("Create invoice");
                    btnCreateInvoice.setEnabled(true);
                    break;
                default:
                    btnCreateInvoice.setCaption("Create invoices");
                    btnCreateInvoice.setEnabled(true);
            }
        });

        btnCreateInvoice.addClickListener(event -> {
            for (ProjectSummary projectSummary : gridProjectSummaryList.getSelectedItems()) {
                projectSummaryClient.createInvoiceFromProject(projectSummary.getProjectuuid(), cbSelectYearMonth.getValue().getDate().getYear(), cbSelectYearMonth.getValue().getDate().getMonthValue()-1);
                reloadData();
            }
        });

        gridProjectSummaryList.setSizeFull();

        addTour();
    }

    public void init() {
        reloadData();
    }

    public void reloadData() {
        System.out.println("ProjectListImpl.reloadData");
        long start = System.currentTimeMillis();
        System.out.println("start = " + start);
        //getUI().getCurrent().access(() -> {
            List<ProjectSummary> projectSummaries = projectSummaryClient.loadProjectSummaryByYearAndMonth(cbSelectYearMonth.getValue().getDate().getYear(), cbSelectYearMonth.getValue().getDate().getMonthValue() - 1);
            gridProjectSummaryList.setDataProvider(DataProvider.ofCollection(projectSummaries));
            gridProjectSummaryList.getDataProvider().refreshAll();
        //});
        double end = System.currentTimeMillis() - start;
        System.out.println("end = " + end);
    }

    private List<YearMonthSelect> createYearMonthSelector() {
        List<YearMonthSelect> yearMonthSelectList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 2, 1);
        LocalDate countDate = LocalDate.now();
        while (countDate.isAfter(startDate)) {
            yearMonthSelectList.add(new YearMonthSelect(countDate));
            countDate = countDate.minusMonths(1);
        }

        return yearMonthSelectList;
    }

    private void addTour() {
        btnTour.addClickListener(e -> {
            Tour tour = new Tour();
            tour.addStep(getStep1(cbSelectYearMonth));
            tour.addStep(getStep2(cbSelectYearMonth));
            tour.addStep(getStep3(gridProjectSummaryList));
            tour.addStep(getStep4(btnCreateInvoice));
            tour.start();
        });
    }

    private Step getStep1(AbstractComponent attachTo) {
        return new StepBuilder()
                //.withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(430, Unit.PIXELS)
                .withTitle("Welcome to InvoiceManager!")
                .withText(
                        "This page is a brief overview of the projects that have been worked on in the chosen month. This is meant as " +
                                "the entry point where new invoices are made. When the page is loaded for the first time, the previous month is selected by default." +
                                "<br />" +
                                "<br />" +
                                "This is the first step in an invoice life cycle. The go from: " +
                                "<br />" +
                                "DRAFTS -> CREATED -> SENT -> PAID" +
                                "<br />" +
                                "<br />" +
                                "Some end the lifecycle as PAID, but if something wrong happened it may be necessary to send an credit note. In that case the invoice " +
                                "gets a new final state: CREDIT NOTE" +
                                "<br />" +
                                "More on that topic later.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .build();
    }

    private Step getStep2(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(200, Unit.PIXELS)
                .withTitle("Selecting year and month")
                .withText("You may select another year/month from this dropdown menu.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep3(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(400, Unit.PIXELS)
                .withTitle("Project list")
                .withText("This list contains all the projects that have been worked on during the chosen month. " +
                        "It only contains the most useful information about the project in order for you to navigate through them. " +
                        "If you need to change any of the shown data, you need to do it from the TimeManager. " +
                        "<br />" +
                        "<br />" +
                        "The grid shows details such as the clients name, project name, a brief description, and how many hours have been registered " +
                        "on the project. It also shows how many invoices have been created based on this project on the chosen month. This also includes " +
                        "Credit Notes. This way you are less likely to create more than one invoice per project.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep4(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(300, Unit.PIXELS)
                .withTitle("Creating invoice drafts")
                .withText("From the grid you may choose one or more projects - fx the projects that you are managing. " +
                        "When you have selected the relevant projects, press this button - create invoices. " +
                        "<br />" +
                        "<br />" +
                        "The InvoiceManager will then create invoice drafts for all the selected projects for the chosen month. " +
                        "This is the state of an invoice life.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Finished", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }
}
