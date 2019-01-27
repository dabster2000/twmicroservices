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
            final UserAmbition userAmbition = userAmbitions.stream().filter(ua -> ua.getAmbitionid() == ambition.getId()).findFirst().orElseGet(() -> {
                UserAmbition save = userAmbitionRepository.save(new UserAmbition(ambition.getId(), user, 0, 1));
                return save;
            });
            //userAmbitionRepository.save(userAmbition);
            double knowledgeScore = userAmbition.getScore();
            int ambitionScore = userAmbition.getAmbition();

            UserAmbitionEntry ambitionEntry = new UserAmbitionEntry();
            ambitionEntry.getLblAmbitionName().setValue(ambition.getName());
            ambitionEntry.getRatingStars().setMaxValue(4);
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
            ambitionEntry.getRatingStars().setValueCaption("I know it by name, but I have neither deeper knowledge nor experience with the method", "I know the method but need help to apply it", "I can use the method independently in a project, but may in some cases need assistance", "I am very experienced in the field and use the method as an expert in a project");
            userAmbitionTable.getContentLayout().addComponent(ambitionEntry);
        }

        return userAmbitionTable;
    }
}
