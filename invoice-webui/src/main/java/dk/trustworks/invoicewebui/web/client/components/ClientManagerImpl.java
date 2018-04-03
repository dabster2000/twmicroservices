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
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.springframework.beans.factory.annotation.Autowired;

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
    private ClientRepository clientRepository;

    @Autowired
    private ClientdataRepository clientdataRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MainTemplate mainTemplate;

    ResponsiveLayout responsiveLayout;

    public ClientManagerImpl init() {
        createClientListView();
        return this;
    }

    private void createClientListView() {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);
/*
        OnOffSwitch onOffSwitch = new OnOffSwitch(false);
        onOffSwitch

        responsiveLayout.addRow().addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.XS, 4)
                .withOffset(ResponsiveLayout.DisplaySize.SM, 4)
                .withComponent();
*/
        int rowItemCount = 1;
        ResponsiveRow row = responsiveLayout.addRow();

        Iterable<Client> clients = clientRepository.findAllByOrderByActiveDescNameAsc();
        for (Client client : clients) {
            ClientCardImpl clientCard = new ClientCardImpl(client, photoRepository.findByRelateduuid(client.getUuid()));
            clientCard.getBtnEdit().addClickListener(event -> {
                createClientDetailsView(client);
            });
            clientCard.getBtnDelete().addClickListener(event -> {
                client.setActive(!client.isActive());
                clientRepository.save(client);
                if(client.isActive()) {
                    clientCard.getBtnDelete().setCaption("DEACTIVATE");
                    clientCard.getBtnDelete().setStyleName("danger");
                    //clientCard.getBtnDelete().setStyleName("flat", true);
                }
                if(!client.isActive()) {
                    clientCard.getBtnDelete().setCaption("ACTIVATE");
                    clientCard.getBtnDelete().setStyleName("friendly");
                    //clientCard.getBtnDelete().setStyleName("flat", true);
                }
            });
            row.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(clientCard);
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                //row = responsiveLayout.addRow();
            }
        }

        AddClientCardDesign addClientCardDesign = new AddClientCardDesign();
        addClientCardDesign.getBtnAddClient().addClickListener(event -> {
            Client client = clientRepository.save(new Client("", "New Client"));
            createClientDetailsView(client);
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
        Image logo = createCompanyLogo(client);
        logo.setWidth("100%");
        LogoCardDesign cardLogo = new LogoCardDesign();
        cardLogo.getContainer().addComponent(logo);

        LogoCardDesign cardLogoWithEditor = new LogoCardDesign();
        cardLogoWithEditor.getContainer().addComponent(new PhotoUploader(client.getUuid(), 800, 400, "Find and upload a logo for this client.", photoRepository).getUploader());

        ClientImpl clientComponent = createClientBlock(client);

        ResponsiveRow row = clientDetailBoard.addRow();

        row.addColumn().withDisplayRules(12, 12, 0, 0).withVisibilityRules(true, true, false, false).withComponent(cardLogo);
        row.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(clientComponent);
        row.addColumn().withDisplayRules(12, 12, 8, 8).withVisibilityRules(false, false, true, true).withComponent(cardLogoWithEditor);

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
                    .withComponent(new ClientDataImpl(clientdataRepository, clientdata, projectRepository));
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
        ClientImpl clientComponent = new ClientImpl(photoRepository, client);
        clientComponent.setHeight("100%");
        clientComponent.getBtnActive().setValue(client.isActive());
        clientComponent.getBtnActive().addValueChangeListener(event -> {
            client.setActive(event.getValue());
            saveClient(client);
        });
        clientComponent.getTxtName().setValue(client.getName());
        clientComponent.getTxtName().addValueChangeListener(event -> {
            client.setName(event.getValue());
            saveClient(client);
        });
        return clientComponent;
    }

    private void saveClient(Client client) {
        if(client.getUuid() == null || client.getUuid().equals("")) {
            client.setUuid(UUID.randomUUID().toString());
            client.setCreated(Timestamp.from(Instant.now()));
            clientRepository.save(client);
        } else {
            clientRepository.save(client);
        }
    }

    private Image createCompanyLogo(Client client) {
        Image logo;
        Photo photo = photoRepository.findByRelateduuid(client.getUuid());
        if(photo!=null && photo.getPhoto().length > 0) {
            logo = new Image(null,
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getPhoto()),
                            "logo.jpg"));
        } else {
            logo = new Image("Upload logo please", new ThemeResource("images/clients/missing-logo.jpg"));
        }
        logo.setHeightUndefined();
        return logo;
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
            ClientDataImpl clientData = new ClientDataImpl(clientdataRepository, new Clientdata("", "", "", "", "", "", 0L, "", client), projectRepository);
            clientData.getBtnDelete().setVisible(false);
            clientData.getCssHider().setVisible(true);
            clientData.getBtnEdit().setVisible(false);
            window2.setContent(clientData);
            UI.getCurrent().addWindow(window2);
        });
    }

}
