package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.TalentPassion;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.TalentPassionRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.admin.components.TalentPassionResultImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class TalentPassionResultBox {

    private final TalentPassionResultImpl talentPassionResult;

    private final TalentPassionRepository talentPassionRepository;

    private final UserService userService;

    @Autowired
    public TalentPassionResultBox(TalentPassionResultImpl talentPassionResult, TalentPassionRepository talentPassionRepository, UserService userService) {

        this.talentPassionResult = talentPassionResult;
        this.talentPassionRepository = talentPassionRepository;
        this.userService = userService;
    }

    public Component create() {

        Component resultInstance = talentPassionResult.getResultBoard();
        talentPassionResult.showResultDescription();

        for (User user : userService.findCurrentlyEmployedUsers(true, ConsultantType.CONSULTANT)) {
            Map<String, Double> performanceScoreMap = new HashMap<>();
            Map<String, Double> potentialScoreMap = new HashMap<>();
            for (TalentPassion talentPassion : talentPassionRepository.findByUseruuidOrderByRegisteredDesc(user.getUuid())) {
                performanceScoreMap.putIfAbsent(
                        talentPassion.getType().name() + talentPassion.getOwner().getUuid(),
                        TalentPassionResultImpl.performanceConverter(talentPassion.getPerformance()));
                potentialScoreMap.putIfAbsent(
                        talentPassion.getType().name() + talentPassion.getOwner().getUuid(),
                        TalentPassionResultImpl.potentialConverter(talentPassion.getPotential()));
            }
            if(performanceScoreMap.size() == 0) continue;
            if(potentialScoreMap.size() == 0) continue;

            double performanceAverage = performanceScoreMap.values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            double potentialAverage = potentialScoreMap.values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();

            talentPassionResult.updateUser(user, performanceAverage, potentialAverage);
        }


        return resultInstance;
    }

}