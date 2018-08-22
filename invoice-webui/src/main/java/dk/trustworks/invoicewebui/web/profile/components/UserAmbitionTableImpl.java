package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.Ambition;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserAmbition;
import dk.trustworks.invoicewebui.model.enums.AmbitionType;
import dk.trustworks.invoicewebui.repositories.AmbitionRepository;
import dk.trustworks.invoicewebui.repositories.UserAmbitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Component getUserAmbitionTable(User user) {
        UserAmbitionTable userAmbitionTable = new UserAmbitionTable();
        List<UserAmbition> userAmbitions = userAmbitionRepository.findByUser(user);

        for (Ambition ambition : ambitionRepository.findAll()) {
            final UserAmbition userAmbition = userAmbitions.stream().filter(ua -> ua.getAmbitionid() == ambition.getId()).findFirst().orElse(new UserAmbition(ambition.getId(), user, 0, 1));
            userAmbitionRepository.save(userAmbition);
            double knowledgeScore = userAmbition.getScore();
            int ambitionScore = userAmbition.getAmbition();

            UserAmbitionEntry ambitionEntry = new UserAmbitionEntry();
            ambitionEntry.getLblAmbitionName().setValue(ambition.getName());
            ambitionEntry.getRatingStars().setMaxValue(4);
            ambitionEntry.getRatingStars().setAnimated(true);
            ambitionEntry.getRatingStars().setValue(knowledgeScore);
            ambitionEntry.getRatingStars().addValueChangeListener(event -> {
                userAmbition.setScore(ambitionEntry.getRatingStars().getValue().intValue());
                userAmbitionRepository.save(userAmbition);
            });
            ambitionEntry.getBtnAmbition().setCaption(AmbitionType.values()[ambitionScore].toString());
            ambitionEntry.getBtnAmbition().addClickListener(event -> {
                ambitionEntry.getBtnAmbition().setCaption(AmbitionType.values()[userAmbition.rollAmbition()].toString());
                userAmbitionRepository.save(userAmbition);
            });
            ambitionEntry.getRatingStars().setValueCaption("I know it by name, but I have neither deeper knowledge nor experience with the method", "I know the method but need help to apply it", "I can use the method independently in a project, but may in some cases need assistance", "I am very experienced in the field and use the method as an expert in a project");
            userAmbitionTable.getContentLayout().addComponent(ambitionEntry);
        }

        return userAmbitionTable;
    }
}
