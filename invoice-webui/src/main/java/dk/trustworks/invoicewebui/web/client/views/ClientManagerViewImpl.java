package dk.trustworks.invoicewebui.web.client.views;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.board.Board;
import com.vaadin.board.Row;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.network.clients.ClientClientImpl;
import dk.trustworks.invoicewebui.network.clients.ClientdataClient;
import dk.trustworks.invoicewebui.network.clients.ProjectClient;
import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.Clientdata;
import dk.trustworks.invoicewebui.web.client.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = ClientManagerViewImpl.VIEW_NAME)
public class ClientManagerViewImpl extends ClientManagerViewDesign implements View {

    public static final String VIEW_NAME = "client";

    @Autowired
    ClientClientImpl clientClient;

    @Autowired
    ProjectClient projectClient;

    @Autowired
    ClientdataClient clientdataClient;

    @Autowired
    RestTemplate restTemplate;

    private Board clientListBoard;

    public ClientManagerViewImpl() {
        clientListBoard = new Board();
    }

    @PostConstruct
    void init() {
        createClientListViev();
        //showClientListView();
    }

    private void showClientListView() {
        addComponent(clientListBoard);
    }

    private void createClientListViev() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Image image = createTopBarImage();
        responsiveLayout.addRow()
                .withAlignment(Alignment.TOP_CENTER)
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(image);

        int rowItemCount = 1;
        ResponsiveRow row = responsiveLayout.addRow();

        Resources<Resource<Client>> clients = clientClient.findAllClientsAndLogo();
        for (Resource<Client> client : clients) {
            ClientCardImpl clientCard = new ClientCardImpl(client.getContent());
            clientCard.getBtnEdit().addClickListener(event -> {
                createClientDetailsView(client);
            });
            row.addColumn().withDisplayRules(12, 6, 3, 3).withComponent(clientCard);
            //row.addComponent(clientCard);
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                row = responsiveLayout.addRow();
                //clientListBoard.addRow(row);
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

        /*
        Image image = createTopBarImage();
        clientListBoard.addRow(image);
        int rowItemCount = 1;
        Row row = new Row();
        clientListBoard.addRow(row);
        for (Resource<Client> clientResource : clientClient.findAllClients()) {
            Client client = clientResource.getContent();
            ClientCardImpl clientCard = new ClientCardImpl(client);
            clientCard.getBtnEdit().addClickListener(event -> {
                createClientDetailsView(clientResource);
            });
            row.addComponent(clientCard);
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                row = new Row();
                clientListBoard.addRow(row);
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
        clientListBoard.addRow(addClientCardDesign);
        */
    }

    private void createClientDetailsView(Resource<Client> clientResource) {
        Link clientDataResourceLink = clientResource.getLink("clientdata");
        System.out.println("clientDataResourceLink = " + clientDataResourceLink.getHref());
        ResponseEntity<Resources<Resource<Clientdata>>> responseEntity = restTemplate.exchange(clientDataResourceLink.getHref(), HttpMethod.GET, null, new ParameterizedTypeReference<Resources<Resource<Clientdata>>>() {});

        final Window window = new Window("Client Contact Information");
        window.setWidth(100.0f, Unit.PERCENTAGE);
        window.setHeight(100.0f, Unit.PERCENTAGE);
        window.setModal(true);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        VerticalLayout verticalLayout = new VerticalLayout(responsiveLayout);

        final Window window2 = new Window("Window");
        window2.setWidth(400.0f, Unit.PIXELS);
        window2.setHeight("600px");
        window2.setModal(true);
        window2.addCloseListener(e -> {
            ResponsiveLayout newBoard = new ResponsiveLayout();
            ResponseEntity<Resources<Resource<Clientdata>>> responseEntity2 = restTemplate.exchange(clientDataResourceLink.getHref(), HttpMethod.GET, null, new ParameterizedTypeReference<Resources<Resource<Clientdata>>>() {});
            fillBoard(clientResource, responseEntity2, newBoard, window2);
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(newBoard);
            window.setContent(new VerticalLayout(newBoard));
        });

        fillBoard(clientResource, responseEntity, responsiveLayout, window2);

        this.getUI().addWindow(window);
        window.setContent(verticalLayout);
    }

    private void fillBoard(Resource<Client> clientResource, ResponseEntity<Resources<Resource<Clientdata>>> responseEntity, ResponsiveLayout clientDetailBoard, Window window2) {
        /*
        Image image = createTopBarImage();
        clientDetailBoard.addRow()
                .withAlignment(Alignment.TOP_CENTER)
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(image);
*/
        Image logo = createCompanyLogo(clientResource);
        logo.setWidth("100%");
        Card cardLogo = new Card();
        cardLogo.getCardHolder().addComponent(logo);
        ClientImpl clientComponent = createClientBlock(clientResource.getContent());

        ResponsiveRow row = clientDetailBoard.addRow();
        row.addStyleName("dark-blue");
        //HorizontalLayout horizontalLayout = new HorizontalLayout(clientComponent, cardLogo);
        //horizontalLayout.setWidth("100%");

        row.addColumn().withDisplayRules(12, 0, 0, 0).withVisibilityRules(true, false, false, false).withComponent(cardLogo);
        row.addColumn().withDisplayRules(12, 8, 6, 6).withComponent(clientComponent);
        row.addColumn().withDisplayRules(0, 4, 6, 6).withVisibilityRules(false, true, true, true).withComponent(cardLogo);

        createContactInformationHeading(clientDetailBoard);

        createClientDataBlock(clientResource.getContent(), responseEntity, clientDetailBoard, window2);
    }

    private void createClientDataBlock(Client client, ResponseEntity<Resources<Resource<Clientdata>>> responseEntity, ResponsiveLayout clientDetailBoard, Window window2) {
        int rowItemCount = 1;
        ResponsiveRow clientDataRow = clientDetailBoard.addRow();
        int columns = 3;
        for (Resource<Clientdata> clientdataResource : responseEntity.getBody().getContent()) {
            clientDataRow
                    .addColumn()
                    .withDisplayRules(0, 2, 3, 0)
                    .withVisibilityRules(false, true, true, false)
                    .withComponent(new Label());
            clientDataRow
                    .addColumn()
                    .withDisplayRules(12, 8, 6, 4)
                    .withComponent(new ClientDataImpl(clientdataClient, projectClient, client, clientdataResource.getContent()));
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
        ClientImpl clientComponent = new ClientImpl(clientClient, client);
        clientComponent.setHeight("100%");
        clientComponent.getTxtName().setValue(client.getName());
        clientComponent.getTxtName().addValueChangeListener(event -> {
            client.setName(event.getValue());
            System.out.println("client = " + client);
            if(client.getUuid() == null || client.getUuid().equals("")) {
                client.setUuid(UUID.randomUUID().toString());
                client.setCreated(Timestamp.from(Instant.now()));
                clientClient.create(client);
            } else {
                clientClient.save(client.getUuid(), client);
            }
        });
        return clientComponent;
    }

    private Image createCompanyLogo(Resource<Client> clientResource) {
        Image logo;
        if(clientResource.getContent().getLogo()!=null && clientResource.getContent().getLogo().length > 0) {
            logo = new Image("",
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(clientResource.getContent().getLogo()),
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
            ClientDataImpl clientData = new ClientDataImpl(clientdataClient, projectClient, client, new Clientdata());
            clientData.getBtnDelete().setVisible(false);
            clientData.getCssHider().setVisible(true);
            clientData.getBtnEdit().setVisible(false);
            window2.setContent(clientData);
            UI.getCurrent().addWindow(window2);
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
