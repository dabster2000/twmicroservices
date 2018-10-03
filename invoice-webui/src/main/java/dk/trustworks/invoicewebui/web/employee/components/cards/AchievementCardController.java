package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.common.BoxImpl;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class AchievementCardController {

    private final UserRepository userRepository;



    public AchievementCardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Component getCard(User user) {
        BoxImpl box = new BoxImpl().witHeight(600, PIXELS).withWidth(100, PERCENTAGE);



        return box;
    }
}
