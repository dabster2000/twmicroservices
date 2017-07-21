package dk.trustworks.web;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import dk.trustworks.web.views.MainWindowImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static dk.trustworks.network.dto.InvoiceStatus.DRAFT;

/**
 * Created by hans on 02/07/2017.
 */
@SpringUI
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener {

    //private final InvoiceListView invoiceView;
    //private final InvoiceListImpl invoiceList;
    private final MainWindowImpl mainWindow;


    @Autowired
    public VaadinUI(MainWindowImpl mainWindow) {
        this.mainWindow = mainWindow;
    }


    @Override
    protected void init(VaadinRequest request) {
        Broadcaster.register(this);
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
        receiveBroadcast("");
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        if(numberOfDrafts>0) mainWindow.getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
        else mainWindow.getMenuButton4().setCaption("DRAFTS");
    }


}