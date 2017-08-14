package dk.trustworks.invoicewebui.web.login;

import com.vaadin.board.Board;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
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
        Board board = new Board();
        board.setSizeFull();
        //board.addRow(new Label(), new LoginImpl(), new Label());
        LoginImpl login = new LoginImpl();
        addComponent(login);
        this.setComponentAlignment(login, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
