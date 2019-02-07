package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Ambition;
import dk.trustworks.invoicewebui.model.AmbitionCategory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserAmbition;
import dk.trustworks.invoicewebui.model.enums.AmbitionType;
import dk.trustworks.invoicewebui.repositories.AmbitionRepository;
import dk.trustworks.invoicewebui.repositories.UserAmbitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserAmbitionTableImpl {

    public static final int MAX_VALUE = 5;
    private final AmbitionRepository ambitionRepository;
    private final UserAmbitionRepository userAmbitionRepository;

    @Autowired
    public UserAmbitionTableImpl(AmbitionRepository ambitionRepository, UserAmbitionRepository userAmbitionRepository) {
        this.ambitionRepository = ambitionRepository;
        this.userAmbitionRepository = userAmbitionRepository;
    }

    @Transactional
    public Component getUserAmbitionTable(User user, AmbitionCategory ambitionCategory) {
        UserAmbitionTable userAmbitionTable = new UserAmbitionTable();
        List<UserAmbition> userAmbitions = userAmbitionRepository.findByUser(user);

        for (Ambition ambition : ambitionRepository.findAmbitionByActiveIsTrueAndCategory(ambitionCategory.getAmbitionCategoryType())) {
            final UserAmbition userAmbition = userAmbitions.stream().filter(ua -> ua.getAmbitionid() == ambition.getId()).findFirst().orElseGet(() ->
                    userAmbitionRepository.save(new UserAmbition(ambition.getId(), user, 0, 1)));
            //userAmbitionRepository.save(userAmbition);
            double knowledgeScore = userAmbition.getScore();
            int ambitionScore = userAmbition.getAmbition();

            UserAmbitionEntry ambitionEntry = new UserAmbitionEntry();
            ambitionEntry.getLblAmbitionName().setValue(ambition.getName());
            ambitionEntry.getRatingStars().setMaxValue(MAX_VALUE);
            ambitionEntry.getRatingStars().setAnimated(true);
            ambitionEntry.getRatingStars().setValue(knowledgeScore);
            ambitionEntry.getRatingStars().addValueChangeListener(event -> {
                UserAmbition one = userAmbitionRepository.findOne(userAmbition.getId());
                one.setScore(ambitionEntry.getRatingStars().getValue().intValue());
                userAmbition.setScore(ambitionEntry.getRatingStars().getValue().intValue());
                one.setUpdated(LocalDate.now());
                userAmbitionRepository.save(one);
            });
            ambitionEntry.getBtnAmbition().setCaption(AmbitionType.values()[ambitionScore].getName());
            ambitionEntry.getBtnAmbition().setDescription(AmbitionType.values()[ambitionScore].getDescription());
            ambitionEntry.getBtnAmbition().addClickListener(event -> {
                UserAmbition one = userAmbitionRepository.findOne(userAmbition.getId());
                if(one.getAmbition()==1 && userAmbitionRepository.findByUser(user).stream().filter(ua -> ua.getAmbition()!=1).count() >= 5) {
                    Notification.show("Only 5 changes", "Only five changes are allowed across all skills", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                ambitionEntry.getBtnAmbition().setCaption(AmbitionType.values()[userAmbition.rollAmbition()].getName());
                one.setAmbition(userAmbition.getAmbition());
                userAmbition.setAmbition(userAmbition.getAmbition());
                one.setUpdated(LocalDate.now());
                ambitionEntry.getBtnAmbition().setDescription(AmbitionType.values()[userAmbition.getAmbition()].getDescription());
                userAmbitionRepository.save(one);
            });
            ambitionEntry.getRatingStars().setValueCaption("Never heard of or just know by name.", "I have basic knowledge about this, perhaps even a certification, but have no actual project experience with this", "I have project experience with this, but I sometimes need assistance", "I have quite some years project experience with this, and can work independently with this on a project", "I have several years of project experience with this -and regarded as an expert by my peers");
            userAmbitionTable.getContentLayout().addComponent(ambitionEntry);
        }

        return userAmbitionTable;
    }
}
