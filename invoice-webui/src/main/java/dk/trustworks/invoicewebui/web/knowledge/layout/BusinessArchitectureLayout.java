package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.ArchitectureCell;
import dk.trustworks.invoicewebui.web.knowledge.components.SideBannerDesign;
import dk.trustworks.invoicewebui.web.knowledge.model.Data;
import dk.trustworks.invoicewebui.web.knowledge.model.DocumentMetadata;
import dk.trustworks.invoicewebui.web.knowledge.model.DomainMetadata;
import dk.trustworks.invoicewebui.web.knowledge.model.Item;
import dk.trustworks.invoicewebui.web.model.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.vaadin.ui.themes.ValoTheme.COMBOBOX_BORDERLESS;

@SpringComponent
@SpringUI
public class BusinessArchitectureLayout extends VerticalLayout {

    private final String rootFilePath = "/Shared/Administration/Intra/knowledge_architecture/";

    private static final Map<String, ThemeResource> icons = new HashMap();

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @Autowired
    private DropboxAPI dropboxAPI;

    private ResponsiveLayout mainLayout;
    private ResponsiveRow gridRow;
    private Box mainBox;

    public BusinessArchitectureLayout init() {
        icons.put("pptx", new ThemeResource("images/icons/powerpoint.png"));
        icons.put("docx", new ThemeResource("images/icons/word.png"));

        this.removeAllComponents();

        mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        mainBox = new Box();
        mainBox.getContent().addComponent(mainLayout);
        mainBox.getContent().setMargin(false);
        this.addComponent(mainBox);

        gridRow = mainLayout.addRow();

        VerticalLayout firstColumn = createArchitectureColumn2("-", "bg-grey", "");
        firstColumn.addComponents(
                createVerticalHeadline("Konceptuel", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Logisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Fysisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        );

        String dataJson = dropboxAPI.getSpecificTextFile(rootFilePath+"data.json", StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Data[] data = objectMapper.readValue(dataJson, Data[].class);
            for (Data datum : data) {
                VerticalLayout column = createArchitectureColumn2(datum.getHeadline(), datum.getColor(), datum.getDescription());
                createRows(column, datum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    private Component createVerticalHeadline(String konceptuel, String description) {
        SideBannerDesign card = new SideBannerDesign();
        card.addStyleName("semi-white-bg");
        card.getVlContent().setHeight(175, Unit.PIXELS);
        Label label = new MLabel(konceptuel).withFullWidth().withStyleName("bold large align-center");
        MLabel descriptionLabel = new MLabel(description).withFullSize().withStyleName("align-center tiny");
        card.getVlContent().addComponents(label);
        card.getVlContent().addComponentsAndExpand(descriptionLabel);
        card.getVlContent().setSpacing(false);
        card.getImgTop().setSource(new ThemeResource("images/cards/architecture/transparent2.png"));
        return card;
    }

    private void createRows(VerticalLayout column, Data datum) {
        column.addComponent(createCard(datum, 0));
        column.addComponent(createCard(datum, 1));
        column.addComponent(createCard(datum, 2));
    }

    private ImageCardDesign createCard(Data datum, int i) {
        Item item = datum.getItems()[i];
        ImageCardDesign box = new ImageCardDesign();
        box.addLayoutClickListener(event -> {
            mainBox.getContent().removeAllComponents();

            ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            mainBox.getContent().addComponent(responsiveLayout);

            ResponsiveRow headerRow = responsiveLayout.addRow();
            headerRow.setStyleName(datum.getColor(), true);


            headerRow.addColumn().withDisplayRules(12, 12, 6,6).withComponent(
                    new MVerticalLayout(
                            new MLabel(datum.getHeadline().toUpperCase()).withFullSize().withStyleName("h4"),
                            new MLabel(datum.getDescription()).withContentMode(ContentMode.HTML)
                            )
                            .withFullWidth()
                            .withMargin(true)
                            .withSpacing(true)
            );
            headerRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(
                    new MVerticalLayout(
                            new MLabel("<b>Relevante kompetencer:</b>").withContentMode(ContentMode.HTML),
                            new MLabel("- Proceskortlægning&nbsp;&nbsp;&nbsp;" +
                                    "- Forretningsregler<br />" +
                                    "<br />" +
                                    "- User experience&nbsp;&nbsp;&nbsp;" +
                                    "- Behovsafdækning").withContentMode(ContentMode.HTML)
                            )
                            .withFullWidth()
                            .withMargin(true)
                            .withSpacing(true)
            );

            ResponsiveRow cardsRow = responsiveLayout.addRow();

            String filePath = rootFilePath+item.getKey();
            for (String filename : dropboxAPI.getFilesInFolder(filePath)) {
                //System.out.println("filename = " + filename);
                String domainMetadataJson = dropboxAPI.getSpecificTextFile(filename, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    DomainMetadata[] domainMetadata = objectMapper.readValue(domainMetadataJson, DomainMetadata[].class);

                    for (DomainMetadata domainMetadatum : domainMetadata) {
                        Map<FileItem, DocumentMetadata> fileItems = new HashMap<>();

                        ArchitectureCell architectureCell1 = new ArchitectureCell();
                        architectureCell1.getLblTitle().setValue(domainMetadatum.getHeadline());
                        architectureCell1.getCbFileSelector().setPlaceholder("");
                        architectureCell1.getCbFileSelector().addStyleName(COMBOBOX_BORDERLESS);
                        architectureCell1.getCbFileSelector().setItemCaptionGenerator(FileItem::getName);
                        architectureCell1.getCbFileSelector().setItemIconGenerator(FileItem::getIcon);
                        architectureCell1.getCbFileSelector().setEmptySelectionAllowed(false);
                        architectureCell1.getLblTitle().setVisible(false);
                        architectureCell1.getBtnAlt1().setVisible(false);
                        architectureCell1.getLblAreaTitle().setValue(domainMetadatum.getHeadline());

                        architectureCell1.getCbFileSelector().addValueChangeListener(event1 -> {
                            DocumentMetadata documentMetadata = fileItems.get(event1.getValue());
                            architectureCell1.getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                                    new ByteArrayInputStream(dropboxAPI.getSpecificBinaryFile(filePath + "/" + domainMetadatum.getFolder()+"/"+documentMetadata.getPreview())),
                                    Math.random()+".jpg"));
                            architectureCell1.getVlConsultants().removeAllComponents();

                            for (String author : documentMetadata.getAuthors()) {
                                architectureCell1.getVlConsultants().addComponent(photoService.getRoundMemberImage(userService.findByUUID(author), false, 50, Unit.PIXELS));
                            }
                            architectureCell1.getImgCustomer().setSource(photoService.getRelatedPhoto(documentMetadata.getCustomeruuid()));
                            architectureCell1.getImgCustomer().setHeight(50, Unit.PIXELS);
                            architectureCell1.getContent().removeAllComponents();
                            architectureCell1.getContent().addComponent(new MLabel(documentMetadata.getDescription()).withFullWidth());
                            //architectureCell1.getImgTop().setSource(dropboxAPI.getThumbnail());

                            //architectureCell1.getContent().setMargin(true);
                        });

                        for (String file : dropboxAPI.getFilesInFolder(filePath + "/" + domainMetadatum.getFolder())) {
                            if(!FilenameUtils.getExtension(file).equals("json")) continue;
                            String documentMetadataJson = dropboxAPI.getSpecificTextFile(file, StandardCharsets.UTF_8);
                            DocumentMetadata documentMetadata = objectMapper.readValue(documentMetadataJson, DocumentMetadata.class);
                            FileItem fileItem = new FileItem("\t"+documentMetadata.getHeadline(), icons.get(documentMetadata.getFiletype()));

                            fileItems.put(fileItem, documentMetadata);
                        }

                        architectureCell1.getCbFileSelector().setItems(fileItems.keySet());
                        architectureCell1.getCbFileSelector().setSelectedItem(fileItems.keySet().stream().findFirst().get());

                        cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                                .withComponent(architectureCell1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            /*
                FileItem fileItem = new FileItem("GCW-01.pptx", PPTX);
                architectureCell1.getCbFileSelector().setItems(
                        fileItem,
                        new FileItem("testfile.docx", DOCX),
                        new FileItem("maalarkitetur.pptx", PPTX));
                architectureCell1.getCbFileSelector().addValueChangeListener(event1 -> {
                    architectureCell1.getImgTop().setSource(new ThemeResource("images/cards/architecture/arc1.png"));
                    List<User> employees = userService.findWorkingUsersByDate(LocalDate.now(), ConsultantType.CONSULTANT);
                    architectureCell1.getVlConsultants().removeAllComponents();
                    for (int j = 0; j < 2; j++) {
                        architectureCell1.getVlConsultants().addComponent(photoService.getRoundMemberImage(employees.get(j+4), false, 50, Unit.PIXELS));
                    }
                    architectureCell1.getImgCustomer().setSource(photoService.getRelatedPhoto(clientRepository.findAllByOrderByActiveDescNameAsc().get(1).getUuid()));
                    architectureCell1.getImgCustomer().setHeight(50, Unit.PIXELS);
                    architectureCell1.getContent().removeAllComponents();
                    architectureCell1.getContent().addComponent(new MLabel("Jeannette lavede brugerrejser for RP " +
                            "som gav et overblik over de snitflader RP har til deres brugere.Derudover s lavede brugerrejser " +
                            "for RP som gav et overblik over de snitflader RP har til deres brugere.").withFullWidth());
                    //architectureCell1.getContent().setMargin(true);
                });

                */
            }

            /*
            architectureCell1.getImgTop().setSource(new ThemeResource("images/cards/architecture/applikation-1.png"));
            //architectureCell1.getImgTop2().setSource(photoService.getRelatedPhoto(clientRepository.findAllByOrderByActiveDescNameAsc().get(1).getUuid()));
            ArchitectureCell architectureCell2 = new ArchitectureCell();
            architectureCell2.getImgTop().setSource(new ThemeResource("images/cards/architecture/applikation-2.png"));
            //architectureCell2.getImgTop2().setSource(photoService.getRelatedPhoto(clientRepository.findAllByOrderByActiveDescNameAsc().get(2).getUuid()));
            ArchitectureCell architectureCell3 = new ArchitectureCell();
            architectureCell3.getImgTop().setSource(new ThemeResource("images/cards/architecture/applikation-3.png"));
            //architectureCell3.getImgTop2().setSource(photoService.getRelatedPhoto(clientRepository.findAllByOrderByActiveDescNameAsc().get(3).getUuid()));
            ArchitectureCell architectureCell4 = new ArchitectureCell();
            architectureCell4.getImgTop().setSource(new ThemeResource("images/cards/architecture/information-1.png"));
            //architectureCell4.getImgTop2().setSource(photoService.getRelatedPhoto(clientRepository.findAllByOrderByActiveDescNameAsc().get(4).getUuid()));
            Image image1 = new Image(null, new ThemeResource("images/icons/powerpoint.png"));
            image1.setWidth(25, Unit.PIXELS);
            image1.setHeight(25, Unit.PIXELS);
            Image image2 = new Image(null, new ThemeResource("images/icons/word.png"));
            image2.setWidth(25, Unit.PIXELS);
            Image image3 = new Image(null, new ThemeResource("images/icons/powerpoint.png"));
            image3.setWidth(25, Unit.PIXELS);
            Label label1 = new MLabel("GCW-01.pptx").withFullWidth();
            Label label2 = new MLabel("sad.docx").withFullWidth();
            Label label3 = new MLabel("maalarkitetur.pptx").withFullWidth();
            MHorizontalLayout horizontalLayout1 = new MHorizontalLayout(image1, label1).withExpand(label1, 1.0f).withMargin(false).withSpacing(true);
            MHorizontalLayout horizontalLayout2 = new MHorizontalLayout(image2, label2).withExpand(label2, 1.0f).withMargin(false).withSpacing(true);
            MHorizontalLayout horizontalLayout3 = new MHorizontalLayout(image3, label3).withExpand(label3, 1.0f).withMargin(false).withSpacing(true);

             */
            /*
            GridLayout gridLayout = new MGridLayout(2, 3).withSpacing(true).withMargin(true).withFullWidth();
            gridLayout.setRows(3);
            gridLayout.addComponent(image1, 0, 0);
            gridLayout.addComponent(new MLabel("GCW-01.pptx").withFullWidth(),1,0);
            gridLayout.addComponent(image2, 0,1);
            gridLayout.addComponent(new MLabel("testfile.docx").withFullWidth(), 1, 1);
            gridLayout.addComponent(image3,0 ,2);
            gridLayout.addComponent(new MLabel("maalarkitetur.pptx").withFullWidth(), 1, 2);
            gridLayout.setColumnExpandRatio(1, 1.0f);
            architectureCell1.getVlContent1().addComponent(gridLayout);
            */
            //architectureCell1.getContent().addComponent(new MVerticalLayout(horizontalLayout1, horizontalLayout2, horizontalLayout3).withSpacing(true).withMargin(true));
            //architectureCell1.getVlContent2().addComponent(new MLabel("test").withFullWidth());

            /*
            cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                    .withComponent(architectureCell2);
            cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                    .withComponent(architectureCell3);
            cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                    .withComponent(architectureCell4);

             */
        });
        box.addStyleName("semi-white-bg");
        box.getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                new ByteArrayInputStream(dropboxAPI.getSpecificBinaryFile(rootFilePath + "" + item.getKey() + ".png")),
                Math.random()+".png"));
        box.getVlContent().addComponent(
                new MVerticalLayout(
                        new MLabel(item.getTitle())
                                .withFullWidth()
                                .withStyleName("small bold"),
                        new MLabel(item.getContent())
                                .withContentMode(ContentMode.HTML)
                                .withFullWidth()
                                .withStyleName("tiny bold")
                )
        );
        box.getVlContent().setHeight(175, Unit.PIXELS);
        return box;
    }

    private VerticalLayout createArchitectureColumn2(String opgaver, String color, String description) {
        MVerticalLayout column = new MVerticalLayout(
                new MLabel(opgaver).withFullWidth().withStyleName("align-center large bold")
        ).withWidth(100, Unit.PERCENTAGE).withMargin(false);

        gridRow.addColumn()
                .withDisplayRules(12, 12, 2, 2)
                .withStyleName(color)
                .withComponent(column);
        return column;
    }

}