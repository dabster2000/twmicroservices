package dk.trustworks.web.views;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.network.dto.Invoice;

/**
 * Created by hans on 09/07/2017.
 */

public class InvoicePreviewView extends VerticalLayout {

    private final Invoice invoice;

    private CssLayout container;

    public InvoicePreviewView(Invoice invoice) {
        this.invoice = invoice;
        this.setStyleName("invoice-container");
        this.setResponsive(true);
        this.setWidthUndefined();

        InvoicePreview invoicePreview = new InvoicePreview();
        invoicePreview.lblClientname.setValue(invoice.clientname);
        invoicePreview.lblStreet.setValue(invoice.clientaddresse);
        invoicePreview.lblZipCity.setValue(invoice.zipcity);
        //invoicePreview.lblCvrEan.setValue(invoice.cvr_ean);
        invoicePreview.lblAttention.setValue(invoice.attention);
        //invoicePreview.lb

        container = new CssLayout();
        container.setResponsive(true);

        container.setHeight(500.0f, Unit.PIXELS);
        container.setStyleName("invoice-preview");
/*
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-app .v-vertical.invoice-container { " +
                "box-shadow: 10px 10px 5px grey; " +
                "border-radius: 5px; " +
                "border: 1px solid #000000; " +
                "padding: 20px; " +
                "}");
*/
        container.addComponent(invoicePreview);
        this.addComponent(container);
    }
}



/*

        container = new VerticalLayout();
        container.setResponsive(true);

        container.setHeight(500.0f, Unit.PIXELS);
        container.setStyleName("invoice-preview");

        Label lblTitel = new Label("Invoice");
        //lblTitel.setStyleName("invoice-titel");
        lblTitel.addStyleName(ValoTheme.LABEL_LARGE);
        lblTitel.addStyleName(ValoTheme.LABEL_BOLD);
        lblTitel.setWidthUndefined();
        //container.addComponent(lblTitel);
        //container.setComponentAlignment(lblTitel, Alignment.TOP_RIGHT);
        //container.setExpandRatio(lblTitel, 0.1f);

        Label lblClientnameHeader = new Label(invoice.clientname);
        lblClientnameHeader.addStyleName(ValoTheme.LABEL_SMALL);

        Label lblProjectnameHeader = new Label(invoice.projectname);
        lblProjectnameHeader.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        lblProjectnameHeader.addStyleName(ValoTheme.LABEL_SMALL);

        double hours = 0.0;
        double income = 0.0;
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            hours += invoiceitem.hours;
            income += (invoiceitem.hours * invoiceitem.rate);
        }

        Label lblSpacer = new Label(" ");
        lblSpacer.addStyleName(ValoTheme.LABEL_HUGE);

        Label lblHours = new Label(hours+" hours");
        lblHours.addStyleName(ValoTheme.LABEL_HUGE);

        Label lblIncome = new Label(income+" kr");
        lblIncome.addStyleName(ValoTheme.LABEL_HUGE);

        VerticalLayout vlAddresse = new VerticalLayout(lblTitel, lblClientnameHeader, lblProjectnameHeader, lblSpacer, lblHours, lblIncome);
        vlAddresse.setComponentAlignment(lblTitel, Alignment.TOP_CENTER);
        vlAddresse.setComponentAlignment(lblHours, Alignment.BOTTOM_RIGHT);
        vlAddresse.setComponentAlignment(lblIncome, Alignment.BOTTOM_RIGHT);
        container.addComponent(vlAddresse);
        container.setComponentAlignment(vlAddresse, Alignment.TOP_LEFT);

        addComponent(container);
        //container.setMargin(true);
        //container.setSpacing(true);

        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-app .v-label.invoice-titel { " +
                "color: #999999; " +
                "font-size: 24px; " +
                "font-weight: 400; " +
                "}");
        styles.add(".v-app .v-vertical.invoice-container { " +
                "padding: 20px; " +
                "}");
        styles.add(".v-app .v-vertical.invoice-preview { " +
                "box-shadow: 10px 10px 5px grey; " +
                "border-radius: 5px; " +
                "border: 1px solid #000000; " +
                "padding: 20px; " +
                "}");
    }
 */