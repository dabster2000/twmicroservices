package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.security.Authorizer;
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

    @Autowired
    private Navigator navigator;

    @Autowired
    private Authorizer authorizer;

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

        navigator.addViewChangeListener((ViewChangeListener) event -> {
            System.out.println("authorizer.hasAccess(event.getNewView()) = " + authorizer.hasAccess(event.getNewView()));
            if(!authorizer.hasAccess(event.getNewView())) event.getNavigator().navigateTo("login");
            System.out.println("never");
            return authorizer.hasAccess(event.getNewView());
        });

        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                // Find the final cause
                String cause = "<b>The click failed because:</b><br/>";
                for (Throwable t = event.getThrowable(); t != null;
                     t = t.getCause())
                    if (t.getCause() == null) // We're at final cause
                        cause += t.getClass().getName() + "<br/>";

                // Do the default error handling (optional)
                doDefault(event);
            }
        });
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