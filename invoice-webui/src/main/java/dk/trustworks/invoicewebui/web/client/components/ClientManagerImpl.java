package dk.trustworks.invoicewebui.web.client.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.repositories.LogoRepository;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by hans on 12/08/2017.
 */
@SpringComponent
@SpringUI
public class ClientManagerImpl extends ClientManagerDesign {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClientdataRepository clientdataRepository;

    @Autowired
    LogoRepository logoRepository;

    @Autowired
    MainTemplate mainTemplate;

    public ClientManagerImpl init() {
        createClientListViev();
        return this;
    }

    private void createClientListViev() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        int rowItemCount = 1;
        ResponsiveRow row = responsiveLayout.addRow();

        Iterable<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            ClientCardImpl clientCard = new ClientCardImpl(client);
            clientCard.getBtnEdit().addClickListener(event -> {
                createClientDetailsView(client);
            });
            row.addColumn().withDisplayRules(12, 6, 3, 3).withComponent(clientCard);
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                row = responsiveLayout.addRow();
            }
        }

        AddClientCardDesign addClientCardDesign = new AddClientCardDesign();
        addClientCardDesign.getBtnAddClient().addClickListener(event -> {
            final Window window = new Window("Window");
            window.setWidth(600.0f, Unit.PIXELS);
            window.setHeight("500px");
            window.setModal(true);
            createClientBlock(new Client());
        });
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 12, 12).withComponent(addClientCardDesign);
    }

    private void createClientDetailsView(Client client) {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        VerticalLayout verticalLayout = new VerticalLayout(responsiveLayout);

        final Window window2 = new Window("Window");
        window2.setWidth(400.0f, Unit.PIXELS);
        window2.setHeight("700px");
        window2.setModal(true);
        window2.addCloseListener(e -> {
            ResponsiveLayout newBoard = new ResponsiveLayout();
            fillBoard(client, newBoard, window2);
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(newBoard);
        });

        fillBoard(client, responsiveLayout, window2);

        mainTemplate.setMainContent(verticalLayout, ClientManagerView.VIEW_ICON, client.getName(), "Our best friends", ClientManagerView.VIEW_BREADCRUMB + " / "+client.getName());
    }

    private void fillBoard(Client client, ResponsiveLayout clientDetailBoard, Window window2) {
        /*
        Image image = createTopBarImage();
        clientDetailBoard.addRow()
                .withAlignment(Alignment.TOP_CENTER)
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(image);
*/
        Image logo = createCompanyLogo(client);
        logo.setWidth("100%");
        LogoCardDesign cardLogo = new LogoCardDesign();
        cardLogo.getContainer().addComponent(logo);
        ClientImpl clientComponent = createClientBlock(client);

        ResponsiveRow row = clientDetailBoard.addRow();

        row.addColumn().withDisplayRules(12, 0, 0, 0).withVisibilityRules(true, false, false, false).withComponent(cardLogo);
        row.addColumn().withDisplayRules(12, 8, 6, 6).withComponent(clientComponent);
        row.addColumn().withDisplayRules(0, 4, 6, 6).withVisibilityRules(false, true, true, true).withComponent(cardLogo);

        createContactInformationHeading(clientDetailBoard);

        createClientDataBlock(client, clientDetailBoard, window2);
    }

    private void createClientDataBlock(Client client, ResponsiveLayout clientDetailBoard, Window window2) {
        int rowItemCount = 1;
        ResponsiveRow clientDataRow = clientDetailBoard.addRow();
        int columns = 3;
        for (Clientdata clientdata : client.getClientdata()) {
            clientDataRow
                    .addColumn()
                    .withDisplayRules(0, 2, 3, 0)
                    .withVisibilityRules(false, true, true, false)
                    .withComponent(new Label());
            clientDataRow
                    .addColumn()
                    .withDisplayRules(12, 8, 6, 4)
                    .withComponent(new ClientDataImpl(clientdataRepository, clientdata));
            clientDataRow
                    .addColumn()
                    .withDisplayRules(0, 2, 3, 0)
                    .withVisibilityRules(false, true, true, false)
                    .withComponent(new Label());
            rowItemCount++;
            if(rowItemCount > columns) {
                rowItemCount = 1;
                clientDataRow = clientDetailBoard.addRow();
                //clientDetailBoard.addRow(clientDataRow);
            }
        }
        if(rowItemCount > columns) {
            System.out.println("new row");
            System.out.println("rowItemCount = " + rowItemCount);
            clientDataRow = clientDetailBoard.addRow();
            rowItemCount = 1;
        }

        createAddClientDataButton(clientDataRow, window2, client);
        rowItemCount++;

        while (rowItemCount <= columns) {
            System.out.println("add label");
            System.out.println("rowItemCount = " + rowItemCount);
            clientDataRow.addColumn().withDisplayRules(12, 8, 8, 4).withComponent(new Label());
            rowItemCount++;
        }
    }

    private void createContactInformationHeading(ResponsiveLayout clientDetailBoard) {
        ResponsiveRow row = clientDetailBoard.addRow().withAlignment(Alignment.MIDDLE_CENTER);
        Label lblClientContactInformation = new Label("Client Contact Information");
        lblClientContactInformation.addStyleName("h3");
        //lblClientContactInformation.setSizeFull();
        row.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(lblClientContactInformation);
    }

    private ClientImpl createClientBlock(Client client) {
        ClientImpl clientComponent = new ClientImpl(logoRepository, client);
        clientComponent.setHeight("100%");
        clientComponent.getTxtName().setValue(client.getName());
        clientComponent.getTxtName().addValueChangeListener(event -> {
            client.setName(event.getValue());
            System.out.println("client = " + client);
            if(client.getUuid() == null || client.getUuid().equals("")) {
                client.setUuid(UUID.randomUUID().toString());
                client.setCreated(Timestamp.from(Instant.now()));
                clientRepository.save(client);
            } else {
                clientRepository.save(client);
            }
        });
        return clientComponent;
    }

    private Image createCompanyLogo(Client client) {
        Image logo;
        if(client.getLogo()!=null && client.getLogo().getLogo().length > 0) {
            logo = new Image(null,
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(client.getLogo().getLogo()),
                            "logo.jpg"));
        } else {
            logo = new Image("Upload logo please", new ThemeResource("images/clients/missing-logo.jpg"));
        }
        logo.setHeightUndefined();
        return logo;
    }

    private Image createTopBarImage() {
        Image image = new Image("", new ThemeResource("images/top-bar.png"));
        image.setResponsive(true);
        image.setWidth(100, Unit.PERCENTAGE);
        image.setHeightUndefined();
        return image;
    }

    private void createAddClientDataButton(ResponsiveRow clientDataRow, Window window2, Client client) {
        Button btnAddContactInformation = new Button("Add Client ContactInformation");
        btnAddContactInformation.addStyleName("primary");
        btnAddContactInformation.addStyleName("huge");
        VerticalLayout verticalLayout = new VerticalLayout(btnAddContactInformation);
        verticalLayout.setSizeFull();
        verticalLayout.setComponentAlignment(btnAddContactInformation, Alignment.MIDDLE_CENTER);
        clientDataRow.addColumn().withDisplayRules(12, 8, 8, 4).withComponent(verticalLayout);

        btnAddContactInformation.addClickListener(event -> {
            ClientDataImpl clientData = new ClientDataImpl(clientdataRepository, new Clientdata("", "", "", "", "", "", 0L, "", client));
            clientData.getBtnDelete().setVisible(false);
            clientData.getCssHider().setVisible(true);
            clientData.getBtnEdit().setVisible(false);
            window2.setContent(clientData);
            UI.getCurrent().addWindow(window2);
        });
    }

}
