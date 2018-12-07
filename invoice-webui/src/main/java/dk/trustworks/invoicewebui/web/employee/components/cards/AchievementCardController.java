package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.Achievement;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.repositories.AchievementRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.employee.components.parts.AchievementBar;

import java.util.List;

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
        //BoxImpl box = new BoxImpl().witHeight(600, PIXELS).withWidth(100, PERCENTAGE);
        AchievementBar achievementBar = new AchievementBar();

        List<Achievement> achievementList = achievementRepository.findByUser(user);

        for (AchievementType achievementType : AchievementType.values()) {
            if(achievementList.stream().anyMatch(achievement -> achievement.getAchievement().equals(achievementType))) {
                Image image = new Image(null, new ThemeResource("images/achievements/" + achievementType.getFilename() + ".png"));
                image.setWidth(100, Sizeable.Unit.PIXELS);
                image.setHeight(100, Sizeable.Unit.PIXELS);
                achievementBar.getImgContainer().addComponent(image);
            }
        }


        return achievementBar;
    }
}
