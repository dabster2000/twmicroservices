package dk.trustworks.invoicewebui.web.client.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.jobs.ChartCacheJob;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 12/08/2017.
 */
@SpringComponent
@SpringUI
public class ClientManagerImpl extends ClientManagerDesign {

    private final ClientRepository clientRepository;

    private final ClientdataRepository clientdataRepository;

    private final PhotoRepository photoRepository;

    private final ProjectService projectService;

    private final UserService userService;

    private final ChartCacheJob chartCache;

    ResponsiveLayout responsiveLayout;

    @Autowired
    public ClientManagerImpl(ClientRepository clientRepository, ClientdataRepository clientdataRepository, PhotoRepository photoRepository, ProjectService projectService, ChartCacheJob chartCache, MainTemplate mainTemplate, UserService userService) {
        this.clientRepository = clientRepository;
        this.clientdataRepository = clientdataRepository;
        this.photoRepository = photoRepository;
        this.projectService = projectService;
        this.chartCache = chartCache;
        this.userService = userService;
    }

    public ClientManagerImpl init() {
        createClientListView();
        return this;
    }

    private void createClientListView() {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        ResponsiveRow statsRow = responsiveLayout.addRow();
        statsRow.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(createClientRevenueChart());

        ResponsiveRow clientRow = responsiveLayout.addRow();

        Iterable<Client> clients = clientRepository.findAllByOrderByActiveDescNameAsc();
        for (Client client : clients) {
            ClientCardImpl clientCard = new ClientCardImpl(client, photoRepository.findByRelateduuid(client.getUuid()));
            clientCard.getBtnEdit().addClickListener(event -> createClientDetailsView(client));
            clientCard.getBtnDelete().addClickListener(event -> {
                client.setActive(!client.isActive());
                clientRepository.save(client);
                if(client.isActive()) {
                    clientCard.getBtnDelete().setCaption("DEACTIVATE");
                    clientCard.getBtnDelete().setStyleName("danger");
                }
                if(!client.isActive()) {
                    clientCard.getBtnDelete().setCaption("ACTIVATE");
                    clientCard.getBtnDelete().setStyleName("friendly");
                }
            });
            clientRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(clientCard);
        }

        AddClientCardDesign addClientCardDesign = new AddClientCardDesign();
        addClientCardDesign.getBtnAddClient().addClickListener(event -> {
            Client client = clientRepository.save(new Client("", "New Client"));
            createClientDetailsView(client);
        });
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 12, 12).withComponent(addClientCardDesign);
    }

    protected void createClientDetailsView(Client client) {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        fillBoard(client);
    }

    private void fillBoard(Client client) {
        Image logo = createCompanyLogo(client);
        logo.setWidth("100%");
        LogoCardDesign cardLogo = new LogoCardDesign();
        cardLogo.getContainer().addComponent(logo);

        LogoCardDesign cardLogoWithEditor = new LogoCardDesign();
        cardLogoWithEditor.getContainer().addComponent(new PhotoUploader(client.getUuid(), 800, 400, "Find and upload a logo for this client.", PhotoUploader.Step.PHOTO, photoRepository).getUploader());

        ClientImpl clientComponent = createClientBlock(client);

        ResponsiveRow row = responsiveLayout.addRow();

        row.addColumn().withDisplayRules(12, 12, 0, 0).withVisibilityRules(true, true, false, false).withComponent(cardLogo);
        row.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(clientComponent);
        row.addColumn().withDisplayRules(12, 12, 8, 8).withVisibilityRules(false, false, true, true).withComponent(cardLogoWithEditor);

        createContactInformationHeading();

        createClientDataBlock(client);
    }

    private void createClientDataBlock(Client client) {
        int rowItemCount = 1;
        ResponsiveRow clientDataRow = responsiveLayout.addRow();
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
                    .withComponent(new ClientDataImpl(clientdataRepository, clientdata, projectService));
            clientDataRow
                    .addColumn()
                    .withDisplayRules(0, 2, 3, 0)
                    .withVisibilityRules(false, true, true, false)
                    .withComponent(new Label());
            rowItemCount++;
            if(rowItemCount > columns) {
                rowItemCount = 1;
                clientDataRow = responsiveLayout.addRow();
            }
        }
        if(rowItemCount > columns) {
            clientDataRow = responsiveLayout.addRow();
            rowItemCount = 1;
        }

        createAddClientDataButton(clientDataRow, client);
        rowItemCount++;

        while (rowItemCount <= columns) {
            clientDataRow.addColumn().withDisplayRules(12, 8, 8, 4).withComponent(new Label());
            rowItemCount++;
        }
    }

    private void createContactInformationHeading() {
        ResponsiveRow row = responsiveLayout.addRow().withAlignment(Alignment.MIDDLE_CENTER);
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
        clientComponent.getCrmId().setValue(client.getCrmId());
        clientComponent.getCrmId().addValueChangeListener(event -> {
            client.setCrmId(event.getValue());
            saveClient(client);
        });
        clientComponent.getCbClientManager().setItems(userService.findAll());
        clientComponent.getCbClientManager().setItemCaptionGenerator(User::getUsername);
        clientComponent.getCbClientManager().setSelectedItem(client.getAccount_manager());
        clientComponent.getCbClientManager().addValueChangeListener(event -> {
            client.setAccount_manager(event.getValue());
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

    private void createAddClientDataButton(ResponsiveRow clientDataRow, Client client) {
        Button btnAddContactInformation = new Button("Add Client ContactInformation");
        btnAddContactInformation.addStyleName("primary");
        btnAddContactInformation.addStyleName("huge");
        VerticalLayout verticalLayout = new VerticalLayout(btnAddContactInformation);
        verticalLayout.setSizeFull();
        verticalLayout.setComponentAlignment(btnAddContactInformation, Alignment.MIDDLE_CENTER);
        clientDataRow.addColumn().withDisplayRules(12, 8, 8, 4).withComponent(verticalLayout);

        final Window window3 = new Window("Window");
        window3.setWidth(400.0f, Unit.PIXELS);
        window3.setHeight("700px");
        window3.setModal(true);

        btnAddContactInformation.addClickListener(event -> {
            Clientdata newClientdata = new Clientdata("", "", "", "", "", "", 0L, "", client);
            ClientDataImpl clientData = new ClientDataImpl(clientdataRepository, newClientdata, projectService);
            clientData.getBtnDelete().setVisible(false);
            clientData.getCssHider().setVisible(true);
            clientData.getBtnEdit().setVisible(false);
            clientData.getBtnSave().addClickListener(event1 -> {
                client.addClientdata(newClientdata);
                createClientDetailsView(client);
                window3.close();
            });
            window3.setContent(clientData);
            UI.getCurrent().addWindow(window3);
        });
    }

    private Chart createClientRevenueChart() {
        Map<String, Number> revenueMap = chartCache.getRevenuePerClientMap();

        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Total revenue, grouped by client");
        conf.setSubTitle("Only showing active clients");

        XAxis x = new XAxis();
        x.setCategories(revenueMap.keySet().stream().toArray(String[]::new));
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Revenue");
        conf.addyAxis(y);

        conf.getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.x +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);

        conf.addSeries(new ListSeries("Revenue", revenueMap.values()));

        chart.drawChart(conf);
        return chart;
    }



}
