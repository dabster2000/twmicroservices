package dk.trustworks.invoicewebui.web.client.views;

import com.vaadin.board.Board;
import com.vaadin.board.Row;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.network.clients.ClientClient;
import dk.trustworks.invoicewebui.network.clients.ClientdataClient;
import dk.trustworks.invoicewebui.network.clients.ProjectClient;
import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.Clientdata;
import dk.trustworks.invoicewebui.web.client.components.AddClientCardDesign;
import dk.trustworks.invoicewebui.web.client.components.ClientCardImpl;
import dk.trustworks.invoicewebui.web.client.components.ClientDataImpl;
import dk.trustworks.invoicewebui.web.client.components.ClientImpl;
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

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = ClientManagerViewImpl.VIEW_NAME)
public class ClientManagerViewImpl extends ClientManagerViewDesign implements View {

    public static final String VIEW_NAME = "client";

    @Autowired
    ClientClient clientClient;

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
        showClientListView();
    }

    private void showClientListView() {
        addComponent(clientListBoard);
    }

    private void createClientListViev() {
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
        clientListBoard.addRow(new AddClientCardDesign());
    }

    private void createClientDetailsView(Resource<Client> clientResource) {
        Link clientDataResourceLink = clientResource.getLink("clientdata");
        System.out.println("clientDataResourceLink = " + clientDataResourceLink.getHref());
        ResponseEntity<Resources<Resource<Clientdata>>> responseEntity = restTemplate.exchange(clientDataResourceLink.getHref(), HttpMethod.GET, null, new ParameterizedTypeReference<Resources<Resource<Clientdata>>>() {});

        final Window window = new Window("Client Contact Information");
        window.setWidth(100.0f, Unit.PERCENTAGE);
        window.setHeight(100.0f, Unit.PERCENTAGE);
        window.setModal(true);

        Board clientDetailBoard = new Board();
        VerticalLayout verticalLayout = new VerticalLayout(clientDetailBoard);

        final Window window2 = new Window("Window");
        window2.setWidth(400.0f, Unit.PIXELS);
        window2.setHeight("600px");
        window2.setModal(true);
        window2.addCloseListener(e -> {
            Board newBoard = new Board();
            ResponseEntity<Resources<Resource<Clientdata>>> responseEntity2 = restTemplate.exchange(clientDataResourceLink.getHref(), HttpMethod.GET, null, new ParameterizedTypeReference<Resources<Resource<Clientdata>>>() {});
            fillBoard(clientResource, responseEntity2, newBoard, window2);
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(newBoard);
            window.setContent(new VerticalLayout(newBoard));
        });

        fillBoard(clientResource, responseEntity, clientDetailBoard, window2);

        this.getUI().addWindow(window);
        window.setContent(verticalLayout);
    }

    private void fillBoard(Resource<Client> clientResource, ResponseEntity<Resources<Resource<Clientdata>>> responseEntity, Board clientDetailBoard, Window window2) {
        Image image = createTopBarImage();
        clientDetailBoard.addRow(image);

        Image logo = createCompanyLogo(clientResource);
        ClientImpl clientComponent = createClientBlock(clientResource);

        Row row = new Row(clientComponent, logo);
        row.setComponentSpan(clientComponent, 2);
        row.setComponentSpan(logo, 2);
        clientDetailBoard.addRow(row);

        Row rowContactInformation = createContactInformationHeading();
        clientDetailBoard.addRow(rowContactInformation);

        Row clientDataRow = createClientDataBlock(clientResource.getContent(), responseEntity, clientDetailBoard, window2);


    }

    private Row createClientDataBlock(Client client, ResponseEntity<Resources<Resource<Clientdata>>> responseEntity, Board clientDetailBoard, Window window2) {
        int rowItemCount = 1;
        Row clientDataRow = new Row();
        for (Resource<Clientdata> clientdataResource : responseEntity.getBody().getContent()) {
            System.out.println("clientdataResource.getContent() = " + clientdataResource.getContent());
            clientDataRow.addComponent(new ClientDataImpl(clientdataClient, projectClient, client, clientdataResource.getContent()));
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                clientDataRow = new Row();
                clientDetailBoard.addRow(clientDataRow);
            }
            System.out.println("rowItemCount = " + rowItemCount);
        }
        if(rowItemCount > 4) {
            System.out.println("new row");
            System.out.println("rowItemCount = " + rowItemCount);
            clientDataRow = new Row();
            rowItemCount = 1;
        }

        createAddClientDataButton(clientDataRow, window2, client);
        rowItemCount++;

        while (rowItemCount <= 4) {
            System.out.println("add label");
            System.out.println("rowItemCount = " + rowItemCount);
            clientDataRow.addComponent(new Label());
            rowItemCount++;
        }
        clientDetailBoard.addRow(clientDataRow);
        return clientDataRow;
    }

    private Row createContactInformationHeading() {
        Label lblClientContactInformation = new Label("Client Contact Information");
        lblClientContactInformation.addStyleName("h3");
        lblClientContactInformation.setSizeFull();
        Row rowContactInformation = new Row(lblClientContactInformation);
        rowContactInformation.setComponentSpan(lblClientContactInformation, 4);
        return rowContactInformation;
    }

    private ClientImpl createClientBlock(Resource<Client> clientResource) {
        ClientImpl clientComponent = new ClientImpl(clientClient, clientResource);
        clientComponent.getTxtName().setValue(clientResource.getContent().getName());
        clientComponent.getTxtName().addValueChangeListener(event -> {
            clientResource.getContent().setName(event.getValue());
            System.out.println("clientResource = " + clientResource.getContent());
            clientClient.save(clientResource.getContent().getUuid(), clientResource.getContent());
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

    private void createAddClientDataButton(Row clientDataRow, Window window2, Client client) {
        Button btnAddContactInformation = new Button("Add Client ContactInformation");
        btnAddContactInformation.addStyleName("primary");
        btnAddContactInformation.addStyleName("huge");
        VerticalLayout verticalLayout = new VerticalLayout(btnAddContactInformation);
        verticalLayout.setSizeFull();
        verticalLayout.setComponentAlignment(btnAddContactInformation, Alignment.MIDDLE_CENTER);
        clientDataRow.addComponent(verticalLayout);

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
