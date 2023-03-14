package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.ConferenceParticipant;
import dk.trustworks.invoicewebui.network.rest.KnowledgeRestService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

@SpringComponent
@SpringUI
public class ForefrontLayout extends VerticalLayout {

    @Autowired
    private KnowledgeRestService knowledgeRestService;

    private Grid<ConferenceParticipant> grid;

    public ForefrontLayout init() {
        this.removeAllComponents();

        Button btnInvite = new MButton("Add to invited list",
                event -> knowledgeRestService.inviteParticipants(grid.getSelectedItems()));

        Button btnReject = new MButton("Reject from waitinglist",
                event -> knowledgeRestService.denyParticipants(grid.getSelectedItems()));

        Button btnWithdraw = new MButton("Withdraw from list",
                event -> knowledgeRestService.withdrawParticipants(grid.getSelectedItems()));

        this.addComponents(btnInvite, btnWithdraw, btnReject);

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        this.addComponent(new BoxImpl().instance(mainLayout));

        ResponsiveRow masterViewRow = mainLayout.addRow();
        ResponsiveColumn masterViewColumn = masterViewRow.addColumn().withDisplayRules(12, 12, 12, 12);

        Grid<ConferenceParticipant> grid = createGrid();

        masterViewColumn.withComponent(grid);

        return this;
    }

    private Grid<ConferenceParticipant> createGrid() {
        List<ConferenceParticipant> gridItemList = knowledgeRestService.findAllConferenceParticipants();

        List<ConferenceParticipant> gridItems = gridItemList.stream().sorted(Comparator.comparing(ConferenceParticipant::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        ListDataProvider<ConferenceParticipant> dataProvider = new ListDataProvider<>(gridItems);

        grid = new Grid<>("", dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.setWidth(100, PERCENTAGE);
        grid.setHeightByRows(gridItemList.size());

        Grid.Column<ConferenceParticipant, String> nameGridCol = grid.addColumn(ConferenceParticipant::getName);
        nameGridCol.setCaption("Name");

        Grid.Column<ConferenceParticipant, String> companyGridCol = grid.addColumn(ConferenceParticipant::getCompany);
        companyGridCol.setCaption("Company");

        Grid.Column<ConferenceParticipant, String> titelGridCol = grid.addColumn(ConferenceParticipant::getTitel);
        titelGridCol.setCaption("titel");

        Grid.Column<ConferenceParticipant, String> emailGridCol = grid.addColumn(ConferenceParticipant::getEmail);
        emailGridCol.setCaption("Email");

        Grid.Column<ConferenceParticipant, String> samtykkeGridCol = grid.addColumn(c -> c.isSamtykke()?"ja":"nej");
        samtykkeGridCol.setCaption("Samtykke");

        Grid.Column<ConferenceParticipant, String> clientGridCol = grid.addColumn(c -> c.getClient()!=null?c.getClient().getName():"");
        clientGridCol.setCaption("Client");

        Grid.Column<ConferenceParticipant, String> statusGridCol = grid.addColumn(c -> c.getStatus().name());
        statusGridCol.setCaption("Status");

        return grid;
    }
}
/*
  private String uuid;
    private String conferenceuuid;
    private String name;
    private String company;
    private String titel;
    private String email;
    private boolean samtykke;
    private ConferenceType type;
    private ConferenceApplicationStatus status;
    private Client client;
 */