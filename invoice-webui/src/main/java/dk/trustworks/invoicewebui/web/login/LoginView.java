package dk.trustworks.invoicewebui.web.login;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.board.Board;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.login.components.LoginImpl;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "login";

    @PostConstruct
    void init() {
        System.out.println("LoginView.init");
        this.setSizeFull();
        //Board board = new Board();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        //board.setSizeFull();
        //board.addRow(new Label(), new LoginImpl(), new Label());
        LoginImpl login = new LoginImpl();
        ResponsiveRow row = responsiveLayout.addRow();
        row//.withAlignment(Alignment.MIDDLE_CENTER)
                .addColumn()
                .withDisplayRules(12,2, 3, 4)
                .withComponent(new Label());
        row//.withAlignment(Alignment.MIDDLE_CENTER)
                .addColumn()
                .withDisplayRules(12, 8, 6, 4)
                .withComponent(login);
        row//.withAlignment(Alignment.MIDDLE_CENTER)
                .addColumn()
                .withDisplayRules(12,2, 3, 4)
                .withComponent(new Label());
        addComponent(responsiveLayout);
        //addComponent(login);
        //this.setComponentAlignment(responsiveLayout, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
