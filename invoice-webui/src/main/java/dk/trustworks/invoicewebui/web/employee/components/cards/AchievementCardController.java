package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.Achievement;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.repositories.AchievementRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.common.BoxImpl;

import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class AchievementCardController {

    private final UserRepository userRepository;

    private final AchievementRepository achievementRepository;

    public AchievementCardController(UserRepository userRepository, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Component getCard(User user) {
        BoxImpl box = new BoxImpl().witHeight(600, PIXELS).withWidth(100, PERCENTAGE);

        List<Achievement> achievementList = achievementRepository.findByUser(user);

        for (AchievementType achievementType : AchievementType.values()) {

        }


        return box;
    }
}
