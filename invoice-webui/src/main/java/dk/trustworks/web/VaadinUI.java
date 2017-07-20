package dk.trustworks.web;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import dk.trustworks.web.views.MainWindowImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 02/07/2017.
 */
@SpringUI
@Theme("invoice")
public class VaadinUI extends UI {

    //private final InvoiceListView invoiceView;
    //private final InvoiceListImpl invoiceList;
    private final MainWindowImpl mainWindow;


    @Autowired
    public VaadinUI(MainWindowImpl mainWindow) {
        this.mainWindow = mainWindow;
    }


    @Override
    protected void init(VaadinRequest request) {
/*
        VerticalLayout layoutContent = new VerticalLayout();
        layoutContent.setMargin(false);
        layoutContent.setSpacing(false);
        layoutContent.addStyleName("outlined");
        layoutContent.setHeight(100.0f, Unit.PERCENTAGE);

        layoutContent.addComponent(invoiceView);
*/
        //VerticalLayout mainLayout = new VerticalLayout(invoiceList);
        setContent(mainWindow);
    }


}