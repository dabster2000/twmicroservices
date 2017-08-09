package dk.trustworks.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.web.views.MainWindowImpl;
import dk.trustworks.web.views.ProjectListImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static dk.trustworks.network.dto.InvoiceStatus.DRAFT;

/**
 * Created by hans on 02/07/2017.
 */
@Push
@SpringUI
@Theme("valo")
//@StyleSheet("valo-theme-ui.css")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener {

    //private final InvoiceListView invoiceView;
    //private final InvoiceListImpl invoiceList;
    private final ProjectListImpl projectList;
    private final MainWindowImpl mainWindow;


    @Autowired
    public VaadinUI(ProjectListImpl projectList, MainWindowImpl mainWindow) {
        this.projectList = projectList;
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
        mainWindow.init();
        receiveBroadcast("");
        setTheme("invoice");
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        projectList.reloadData();
        int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            if(numberOfDrafts>0) mainWindow.getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            else mainWindow.getMenuButton4().setCaption("DRAFTS");
        });

    }


}