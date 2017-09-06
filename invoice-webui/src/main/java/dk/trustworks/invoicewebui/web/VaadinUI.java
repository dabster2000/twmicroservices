package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.invoice.components.DraftListImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 02/07/2017.
 */
@Push
@SpringUI
@SpringViewDisplay
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener, ViewDisplay {

    private Panel springViewDisplay;

    @Autowired
    private DraftListImpl draftList;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setSizeFull();
        setContent(root);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);
        Broadcaster.register(this);
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        //projectList.reloadData();
        //int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        //System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            draftList.receiveBroadcast(message);
        });

    }

    @Override
    public void showView(View view) {
        springViewDisplay.setContent((Component) view);
    }
}