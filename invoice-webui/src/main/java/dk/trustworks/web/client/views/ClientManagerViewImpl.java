package dk.trustworks.web.client.views;

import com.vaadin.board.Board;
import com.vaadin.board.Row;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import dk.trustworks.network.clients.ClientClient;
import dk.trustworks.network.clients.ClientdataClient;
import dk.trustworks.network.dto.Client;
import dk.trustworks.network.dto.Clientdata;
import dk.trustworks.web.client.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = ClientManagerViewImpl.VIEW_NAME)
public class ClientManagerViewImpl extends ClientManagerViewDesign implements View {

    public static final String VIEW_NAME = "client";

    @Autowired
    ClientClient clientClient;

    @Autowired
    private ClientdataClient clientdataClient;

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
        Image image = new Image("", new ThemeResource("images/top-bar.png"));
        image.setResponsive(true);
        image.setWidth(100, Unit.PERCENTAGE);
        image.setHeightUndefined();
        clientListBoard.addRow(image);
        int rowItemCount = 1;
        Row row = new Row();
        clientListBoard.addRow(row);
        for (Resource<Client> clientResource : clientClient.findAllClients()) {
            Client client = clientResource.getContent();
            ClientCardImpl clientCard = new ClientCardImpl("", client.getName(), client.getContactname(), client.getLongitude(), client.getLatitude());
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



        final Window window = new Window("Invoice editor");
        window.setWidth(100.0f, Unit.PERCENTAGE);
        window.setHeight(100.0f, Unit.PERCENTAGE);
        window.setModal(true);

        Board clientDetailBoard = new Board();

        Image image = new Image("", new ThemeResource("images/top-bar.png"));
        image.setResponsive(true);
        image.setWidth(100, Unit.PERCENTAGE);
        image.setHeightUndefined();
        clientDetailBoard.addRow(image);

        ClientMapLocationImpl clientMapLocation = new ClientMapLocationImpl();
        ClientImpl clientComponent = new ClientImpl();
        Row row = new Row(clientComponent, clientMapLocation);
        row.setComponentSpan(clientMapLocation, 2);
        row.setComponentSpan(clientComponent, 2);
        clientDetailBoard.addRow(row);
        clientDetailBoard.addRow(new AddClientCardDesign(), new AddClientCardDesign(), new AddClientCardDesign(), new AddClientCardDesign());

        System.out.println("responseEntity.getBody().getContent().size() = " + responseEntity.getBody().getContent().size());

        int rowItemCount = 1;
        Row clientDataRow = new Row();
        for (Resource<Clientdata> clientdataResource : responseEntity.getBody().getContent()) {
            System.out.println("clientdataResource.getContent() = " + clientdataResource.getContent());
            clientDataRow.addComponent(new ClientDataImpl(clientdataClient, clientdataResource.getContent()));
            rowItemCount++;
            if(rowItemCount > 4) {
                rowItemCount = 1;
                clientDataRow = new Row();
                clientDetailBoard.addRow(clientDataRow);
            }
        }
        if(rowItemCount <= 4) clientDetailBoard.addRow(clientDataRow);

        this.getUI().addWindow(window);
        window.setContent(new VerticalLayout(clientDetailBoard));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
