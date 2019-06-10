package dk.trustworks.invoicewebui.web.admin.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.TalentPassion;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.TalentPassionType;
import dk.trustworks.invoicewebui.repositories.TalentPassionRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.admin.components.TalentPassionResultImpl;
import dk.trustworks.invoicewebui.web.admin.components.TalentPassionScoringDesign;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static dk.trustworks.invoicewebui.web.admin.components.TalentPassionResultImpl.performanceConverter;
import static dk.trustworks.invoicewebui.web.admin.components.TalentPassionResultImpl.potentialConverter;

@SpringUI
@SpringComponent
public class TalentPassionLayout {

    @Autowired
    TalentPassionRepository talentPassionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TalentPassionResultImpl talentPassionResult;


    public TalentPassionLayout() {
    }

    public Component getLayout() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 12, 12).withComponent(talentPassionResult.getResultBoard());

        ResponsiveRow row = responsiveLayout.addRow();

        for (User user : userService.findCurrentlyEmployedUsers()) {
            TalentPassionScoringDesign scoringDesign = new TalentPassionScoringDesign();
            Image roundMemberImage = photoService.getRoundMemberImage(user, false, 125, PIXELS);
            scoringDesign.getImgUser().addComponent(roundMemberImage);
            scoringDesign.getImgDna().setSource(new ThemeResource("images/icons/trustworks_icon_kaffe.svg"));
            scoringDesign.getImgCustomer().setSource(new ThemeResource("images/icons/trustworks_icon_konsulent.svg"));
            scoringDesign.getImgTeam().setSource(new ThemeResource("images/icons/trustworks_icon_kollega.svg"));
            row.addColumn().withComponent(scoringDesign).withDisplayRules(12, 12, 6, 6);

            scoringDesign.getRbgPotentialTeam().setValue(-1.0);
            scoringDesign.getRbgPerformanceTeam().setValue(-1.0);
            scoringDesign.getRbgPotentialCustomer().setValue(-1.0);
            scoringDesign.getRbgPerformanceCustomer().setValue(-1.0);
            scoringDesign.getRbgPotentialDNA().setValue(-1.0);
            scoringDesign.getRbgPerformanceDNA().setValue(-1.0);

            int[] performance = {-1, -1, -1};
            int[] potential = {-1, -1, -1};

            scoringDesign.getRbgPerformanceDNA().addValueChangeListener(event -> {
                performance[0] = event.getValue().intValue();
                if(performance[0]>-1 && potential[0]>-1) scoringDesign.getBtnDNA().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getRbgPotentialDNA().addValueChangeListener(event -> {
                potential[0] = event.getValue().intValue();
                if(performance[0]>-1 && potential[0]>-1) scoringDesign.getBtnDNA().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getRbgPerformanceCustomer().addValueChangeListener(event -> {
                performance[1] = event.getValue().intValue();
                if(performance[1]>-1 && potential[1]>-1) scoringDesign.getBtnCustomer().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getRbgPotentialCustomer().addValueChangeListener(event -> {
                potential[1] = event.getValue().intValue();
                if(performance[1]>-1 && potential[1]>-1) scoringDesign.getBtnCustomer().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getRbgPerformanceTeam().addValueChangeListener(event -> {
                performance[2] = event.getValue().intValue();
                if(performance[2]>-1 && potential[2]>-1) scoringDesign.getBtnTeam().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getRbgPotentialTeam().addValueChangeListener(event -> {
                potential[2] = event.getValue().intValue();
                if(performance[2]>-1 && potential[2]>-1) scoringDesign.getBtnTeam().setEnabled(true);
                updateChart(user, performance, potential);
            });

            scoringDesign.getBtnDNA().addClickListener(event -> {
                saveChoice(user, performance, potential, TalentPassionType.DNA, 0, scoringDesign.getVlChoiceDNA());
            });

            scoringDesign.getBtnCustomer().addClickListener(event -> {
                saveChoice(user, performance, potential, TalentPassionType.CUSTOMER, 1, scoringDesign.getVlChoiceCustomer());
            });

            scoringDesign.getBtnTeam().addClickListener(event -> {
                saveChoice(user, performance, potential, TalentPassionType.TEAM, 2, scoringDesign.getVlChoiceTeam());
            });
        }

        return responsiveLayout;
    }

    private void saveChoice(User user, int[] performance, int[] potential, TalentPassionType dna, int i, VerticalLayout vlChoiceDNA) {
        talentPassionRepository.findByUserAndOwnerAndTypeAndRegistered(user, userService.getLoggedInUser().get(), dna, LocalDate.now()).ifPresent(talentPassion -> talentPassionRepository.delete(talentPassion.getUuid()));
        talentPassionRepository.save(new TalentPassion(UUID.randomUUID().toString(), user, userService.getLoggedInUser().get(), dna, performance[i], potential[i], LocalDate.now()));
        vlChoiceDNA.removeAllComponents();
        Image image = new Image(null, new ThemeResource("images/icons/trustworks_icon_kage.svg"));
        image.setWidth(100, PIXELS);
        image.setHeight(100, PIXELS);
        vlChoiceDNA.addComponent(image);
        vlChoiceDNA.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
    }

    private void updateChart(User user, int[] performance, int[] potential) {
        int count = 0;
        double performanceScore = 0.0;
        double potentialScore = 0.0;

        for (int i = 0; i < 3; i++) {
            if(performance[i] < 0) continue;
            if(potential[i] < 0) continue;

            performanceScore += performanceConverter(performance[i]);
            potentialScore += potentialConverter(potential[i]);
            count++;
        }
        if(count==0) return;
        talentPassionResult.updateUser(user, performanceScore / count, potentialScore / count);
    }
}
