package dk.trustworks.invoicewebui.web.time;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.network.clients.*;
import dk.trustworks.invoicewebui.network.dto.*;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.security.Authorizer;
import dk.trustworks.invoicewebui.web.time.components.TimeManagerImpl;
import dk.trustworks.invoicewebui.web.time.model.WeekItem;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.vaadin.patrik.FastNavigation;
import tm.kod.widgets.numberfield.NumberField;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 16/08/2017.
 */
@SpringView(name = TimeManagerView.VIEW_NAME)
public class TimeManagerView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TimeManagerImpl timeManager;

    public static final String VIEW_NAME = "time";

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(timeManager.init());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Authorizer.authorize(this);
    }
}
