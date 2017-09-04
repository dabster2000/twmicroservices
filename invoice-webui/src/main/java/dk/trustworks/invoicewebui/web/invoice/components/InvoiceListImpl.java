package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.Push;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.generators.InvoicePdfGenerator;
import dk.trustworks.invoicewebui.model.InvoiceItem;
import dk.trustworks.invoicewebui.model.InvoiceStatus;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.web.Broadcaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.client.RestTemplate;
import org.vaadin.addons.producttour.actions.TourActions;
import org.vaadin.addons.producttour.button.StepButton;
import org.vaadin.addons.producttour.shared.step.StepAnchor;
import org.vaadin.addons.producttour.step.Step;
import org.vaadin.addons.producttour.step.StepBuilder;
import org.vaadin.addons.producttour.tour.Tour;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import static dk.trustworks.invoicewebui.model.InvoiceStatus.DRAFT;

/**
 * Created by hans on 13/07/2017.
 */

@SpringComponent
@SpringUI
@Push
public class InvoiceListImpl extends InvoiceListDesign
        implements Broadcaster.BroadcastListener {

    private final InvoiceRepository invoiceRepository;

    private final RestTemplate restTemplate;

    @Autowired
    InvoicePdfGenerator invoicePdfGenerator;

    @Autowired
    public InvoiceListImpl(InvoiceRepository invoiceRepository, RestTemplate restTemplate) {
        Broadcaster.register(this);
        this.restTemplate = restTemplate;
        System.out.println("InvoiceListImpl.InvoiceListImpl");
        this.invoiceRepository = invoiceRepository;

        loadInvoicesToGrid();

        gridInvoiceList.setSelectionMode(Grid.SelectionMode.MULTI);

        Grid.Column invoicedateColumn = gridInvoiceList.getColumn("invoicedate");
        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        invoicedateColumn.setRenderer(new LocalDateRenderer(localDateFormatter));

        gridInvoiceList.addItemClickListener(event -> {
            if(event.getMouseEventDetails().isDoubleClick()) {
                final Window window = new Window("Invoice editor");
                window.setWidth(700.0f, Unit.PIXELS);
                window.setHeight(1000.0f, Unit.PIXELS);
                window.setModal(true);

                Invoice invoice = event.getItem();

                InvoiceEditImpl invoiceEdit = new InvoiceEditImpl(invoice);

                invoiceEdit.btnCopyDescription.addClickListener(clickEvent -> {
                    System.out.println("invoice.projectuuid = " + invoice.projectuuid);
                    Invoice latestInvoiceByProjectuuid = invoiceRepository.findByLatestInvoiceByProjectuuid(invoice.projectuuid);
                    invoiceEdit.setSpecificDescription(latestInvoiceByProjectuuid.specificdescription);
                });

                invoiceEdit.btnCreateInvoice.addClickListener(clickEvent -> {
                    try {
                        saveFormToInvoiceBean(invoice, invoiceEdit);
                        invoice.setStatus(InvoiceStatus.CREATED);
                        invoice.invoicenumber = invoiceRepository.getMaxInvoiceNumber() + 1;
                        try {
                            invoice.pdf = invoicePdfGenerator.createInvoice(invoice);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        invoiceRepository.save(invoice);
                        window.close();
                    } catch (ValidationException e) {
                        e.printStackTrace();
                    }
                });

                invoiceEdit.btnSave.addClickListener(clickEvent -> {
                    try {
                        saveFormToInvoiceBean(invoice, invoiceEdit);
                        invoiceRepository.save(invoice);
                    } catch (ValidationException e) {
                        Notification.show("Invoice could not be saved, " +
                                "please check error messages for each field.");
                    }
                    window.close();
                });

                window.setContent(invoiceEdit);
                this.getUI().addWindow(window);
            }
        });

        btnDelete.addClickListener(event -> {
            for (Invoice selectedInvoice : gridInvoiceList.getSelectedItems()) {
                invoiceRepository.delete(selectedInvoice.getUuid());
            }
        });

        addTour();
    }

    public void saveFormToInvoiceBean(Invoice invoice, InvoiceEditImpl invoiceEdit) throws ValidationException {
        for (Binder<InvoiceItem> binder : invoiceEdit.binders.keySet()) {
            binder.writeBean(invoiceEdit.binders.get(binder));
        }
        invoiceEdit.invoiceBinder.writeBean(invoice);
        invoice.getInvoiceitems().removeIf(invoiceItem -> invoiceItem.itemname.trim().length() == 0);
    }

    @Override
    public void receiveBroadcast(final String message) {
        System.out.println("InvoiceListImpl.receiveBroadcast");
        System.out.println("message = [" + message + "]");
        if(this.getUI() != null) {
            this.getUI().access(this::loadInvoicesToGrid);
        }
    }

    public void loadInvoicesToGrid() {
        System.out.println("InvoiceListImpl.loadInvoicesToGrid");
        System.out.println("");
        List<Invoice> invoices = invoiceRepository.findByStatus(DRAFT);
        gridInvoiceList.setItems(invoices);
        gridInvoiceList.getDataProvider().refreshAll();
    }

    private void addTour() {
        btnTour.addClickListener(e -> {
            Tour tour = new Tour();
            tour.addStep(getStep1(gridInvoiceList));
            tour.addStep(getStep2(gridInvoiceList));
            tour.addStep(getStep3(btnDelete));
            tour.start();
        });
    }

    private Step getStep1(AbstractComponent attachTo) {
        return new StepBuilder()
                //.withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(280, Unit.PIXELS)
                .withTitle("Drafts!")
                .withText(
                        "On this page you can see the draft invoices you created on the previous page. You may " +
                                "still edit invoices with a status of \"DRAFT\". If there are things you can't edit " +
                                "it's because of one of two things. Either its a computer generated value (like invoice " +
                                "number) og it's something you edit using the TimeManager (like description or client information.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .build();
    }

    private Step getStep2(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(510, Unit.PIXELS)
                .withTitle("Editing invoices")
                .withText("Double click an invoice in this grid if you want to see or edit an invoice. After you double click an invoice " +
                        "a window opens with a preview of the invoice. In this window you can edit all the fields you want. " +
                        "<br />" +
                        "<br />" +
                        "You may also add new invoice lines by clicking the + button. If you leave \"varer nr\" blank the invoice line " +
                        "will get deleted during the next update." +
                        "<br />" +
                        "<br />" +
                        "When you are finished you can click update to update changes to your draft. " +
                        "<br />" +
                        "<br />" +
                        "You may also click \"Create Invoice\" to create a new invoice - which actually means setting the invoice state to " +
                        "CREATED and creating an invoice number. This is IRREVERSIBLE and invoices cannot be changed after this point. So make sure " +
                        "that everything is correct before clicking that button." +
                        "<br />" +
                        "Invoices can now be seen on the next page - INVOICE STATUS")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.BOTTOM)
                .build();
    }

    private Step getStep3(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(200, Unit.PIXELS)
                .withTitle("Project list")
                .withText("Since invoices with status DRAFT are in fact... drafts, you may delete them again. This is done " +
                        "by selecting one or more invoice drafts and clicking the Delete button.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Finished", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }
}
