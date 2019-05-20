package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.Achievement;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.repositories.AchievementRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.utils.SpriteSheet;
import dk.trustworks.invoicewebui.web.employee.components.parts.AchievementBar;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.ui.Alignment.BOTTOM_CENTER;

@SpringUI
@SpringComponent
public class AchievementCardController {

    private final UserRepository userRepository;

    private final SpriteSheet spriteSheet;

    private final AchievementRepository achievementRepository;

    public AchievementCardController(UserRepository userRepository, SpriteSheet spriteSheet, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.spriteSheet = spriteSheet;
        this.achievementRepository = achievementRepository;
    }

    public Component getCard(User user) {
        //BoxImpl box = new BoxImpl().witHeight(600, PIXELS).withWidth(100, PERCENTAGE);
        AchievementBar achievementBar = new AchievementBar();

        List<Achievement> achievementList = achievementRepository.findByUser(user);

        Set<String> collected = new TreeSet<>();
        for (AchievementType achievementType : Arrays.stream(AchievementType.values()).sorted(Comparator.comparingInt(AchievementType::getRank).reversed()).collect(Collectors.toCollection(ArrayList::new))) {
            if(achievementList.stream().anyMatch(achievement -> achievement.getAchievement().equals(achievementType))) {
                if(collected.contains(achievementType.getParent())) continue;
                collected.add(achievementType.getParent());
                MVerticalLayout vl = new MVerticalLayout()
                        .withWidth(100, PIXELS)
                        .withHeight(140, PIXELS)
                        .withMargin(false);
                Image image = new Image(null, spriteSheet.getSprite(achievementType.getNumber()));
                image.setWidth(100, PIXELS);
                image.setHeight(100, PIXELS);
                image.setDescription(achievementType.getDescription());
                vl.addComponent(image);
                vl.addComponent(new MLabel(achievementType.toString())
                        .withWidth(85, PIXELS)
                        .withHeight(40, PIXELS)
                        .withStyleName("very-light-gold-font", "tiny", "align-center"));
                vl.alignAll(BOTTOM_CENTER);
                achievementBar.getImgContainer().addComponent(vl);
            }
        }

        // TODO: REMOVE THIS!!!
        //achievementBar.setVisible(false);
        return achievementBar;
    }
}
