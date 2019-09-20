package dk.trustworks.handlers;

import dk.trustworks.InvoiceApplication;
import dk.trustworks.model.Invoice;
import dk.trustworks.model.InvoiceStatus;
import dk.trustworks.pdf.InvoicePdfGenerator;
import dk.trustworks.repositories.InvoiceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by hans on 15/07/2017.
 */
@Component
@RepositoryEventHandler(Invoice.class)
public class InvoiceEventHandler {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    InvoicePdfGenerator invoicePdfGenerator;

    @HandleBeforeSave
    public void handleInvoiceSave(Invoice invoice) throws IOException {
        System.out.println("InvoiceEventHandler.handleInvoiceSave");
        System.out.println("invoice = [" + invoice + "]");
        if(invoice.status.equals(InvoiceStatus.CREATED) && invoice.invoicenumber == 0) {
            System.out.println("invoice.status = " + invoice.status + " && "+ invoice.invoicenumber);
            invoice.invoicenumber = invoiceRepository.getMaxInvoiceNumber() + 1;
            invoice.pdf = invoicePdfGenerator.createInvoice(invoice);
        }
    }

    @HandleAfterSave
    public void handleAfterInvoiceSave(Invoice invoice) {
        System.out.println("InvoiceEventHandler.handleAfterInvoiceSave");
        System.out.println("invoice = [" + invoice + "]");
        rabbitTemplate.convertAndSend(InvoiceApplication.queueName, "invoice");
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Invoice invoice) {
        System.out.println("InvoiceEventHandler.handleBeforeDelete");
        System.out.println("invoice = [" + invoice + "]");
        if(!invoice.status.equals(InvoiceStatus.DRAFT)) {
            throw new RuntimeException(HttpStatus.FORBIDDEN.toString());
        }
    }

    @HandleAfterDelete
    public void handleAfterDelete(Invoice invoice) {
        System.out.println("InvoiceEventHandler.handleAfterDelete");
        System.out.println("invoice = [" + invoice + "]");
        rabbitTemplate.convertAndSend(InvoiceApplication.queueName, "invoice");
    }

    @HandleBeforeCreate
    public void handleAfterCreate(Invoice invoice) {
        System.out.println("InvoiceEventHandler.handleBeforeCreate");
        System.out.println("invoice = [" + invoice + "]");
        rabbitTemplate.convertAndSend(InvoiceApplication.queueName, "invoice");
    }
}