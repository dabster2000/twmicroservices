package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.security.RolePermissionAuthorizer;
import org.ilay.Authorization;
import org.ilay.api.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hans on 02/07/2017.
 */
@Push
@SpringUI
@SpringViewDisplay
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener, ViewDisplay {

    private Panel springViewDisplay;

    private VerticalLayout mainLayout;

    @Override
    protected void init(VaadinRequest request) {

        RolePermissionAuthorizer rolePermissionAuthorizer = new RolePermissionAuthorizer();

        Set<Authorizer> authorizers = new HashSet<>();
        authorizers.add(rolePermissionAuthorizer);

        final VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setSizeFull();
        setContent(root);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        //projectList.reloadData();
        //int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        //System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            //if(numberOfDrafts>0) mainWindow.getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            //else mainWindow.getMenuButton4().setCaption("DRAFTS");
        });

    }

    @Override
    public void showView(View view) {
        springViewDisplay.setContent((Component) view);
    }
}