package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.collections.api.tuple.Twin;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.KnowledgeArchitectureCard;
import dk.trustworks.invoicewebui.model.KnowledgeArchitectureCell;
import dk.trustworks.invoicewebui.model.KnowledgeArchitectureColumn;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.KnowArchiColumnRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.ArchitectureCell;
import dk.trustworks.invoicewebui.web.knowledge.components.SideBannerDesign;
import dk.trustworks.invoicewebui.web.knowledge.model.DocumentMetadata;
import dk.trustworks.invoicewebui.web.model.FileItem;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.vaadin.ui.themes.ValoTheme.COMBOBOX_BORDERLESS;

@SpringComponent
@SpringUI
public class BusinessArchitectureLayout extends VerticalLayout {

    private final String rootFilePath = "/Shared/Administration/Intra/knowledge_architecture/";

    private static final Map<String, ThemeResource> icons = new HashMap();

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DropboxAPI dropboxAPI;

    @Autowired
    private KnowArchiColumnRepository knowArchiColumnRepository;

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

        VerticalLayout firstColumn = getTitleComponent("bg-grey", new Label("-"));
        firstColumn.addComponents(
                createVerticalHeadline("Konceptuel", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Logisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Fysisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        );

        for (KnowledgeArchitectureColumn archiColumn : knowArchiColumnRepository.findAll()) {
            VerticalLayout column = createArchitectureColumn2(archiColumn);
            createRows(column, archiColumn);
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

    private void createRows(VerticalLayout column, KnowledgeArchitectureColumn archiColumn) {
        column.addComponent(createCard(archiColumn, 0));
        column.addComponent(createCard(archiColumn, 1));
        column.addComponent(createCard(archiColumn, 2));
    }

    private ImageCardDesign createCard(KnowledgeArchitectureColumn archiColumn, int i) {
        if(archiColumn.getCells().size()<=i) return new ImageCardDesign();
        KnowledgeArchitectureCell item = archiColumn.getCells().get(i);
        ImageCardDesign box = new ImageCardDesign();
        box.addLayoutClickListener(event -> {
            mainBox.getContent().removeAllComponents();

            ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            mainBox.getContent().addComponent(responsiveLayout);

            ResponsiveRow headerRow = responsiveLayout.addRow();
            headerRow.setStyleName(archiColumn.getColor(), true);

            /*
            VerticalLayout layout = new VerticalLayout(
                new MLabel(column.getName())
                        .withFullWidth()
                        .withStyleName("align-center large bold"));
        layout.addLayoutClickListener(layoutClickEvent -> {
            final Window window = new Window();
            MTextField title = new MTextField("title", column.getName(), valueChangeEvent -> {
                column.setName(valueChangeEvent.getValue());
                knowArchiColumnRepository.save(column);
                window.close();
            }).withValueChangeMode(ValueChangeMode.BLUR);
            window.setContent(title);
            window.setModal(true);
            window.setDraggable(false);
            window.setClosable(false);
            window.setResizable(false);
            UI.getCurrent().addWindow(window);
        });
             */

            VerticalLayout nameLayout = new VerticalLayout(
                    new MLabel(item.getName().toUpperCase()).withFullSize().withStyleName("h4")
            );
            nameLayout.addLayoutClickListener(layoutClickEvent -> {
                final Window window = new Window();
                MTextField title = new MTextField("title", item.getName(), valueChangeEvent -> {
                    item.setName(valueChangeEvent.getValue());
                    knowArchiColumnRepository.save(archiColumn);
                    window.close();
                }).withValueChangeMode(ValueChangeMode.BLUR);
                window.setContent(title);
                window.setModal(true);
                window.setDraggable(false);
                window.setClosable(false);
                window.setResizable(false);
                UI.getCurrent().addWindow(window);
            });

            VerticalLayout descLayout = new VerticalLayout(
                    new MLabel(item.getDescription()).withContentMode(ContentMode.HTML)
            );
            descLayout.addLayoutClickListener(layoutClickEvent -> {
                final Window window = new Window();
                MTextField title = new MTextField("description", item.getDescription(), valueChangeEvent -> {
                    item.setDescription(valueChangeEvent.getValue());
                    knowArchiColumnRepository.save(archiColumn);
                    window.close();
                }).withValueChangeMode(ValueChangeMode.BLUR);
                window.setContent(title);
                window.setModal(true);
                window.setDraggable(false);
                window.setClosable(false);
                window.setResizable(false);
                UI.getCurrent().addWindow(window);
            });

            headerRow.addColumn().withDisplayRules(12, 12, 6,6).withComponent(
                    new MVerticalLayout(
                            nameLayout,
                            descLayout
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

            for (KnowledgeArchitectureCard card : item.getCards()) {
                Map<FileItem, DocumentMetadata> fileItems = new HashMap<>();

                ArchitectureCell architectureCell1 = new ArchitectureCell();
                architectureCell1.getLblTitle().setValue(card.getName()+"1");
                architectureCell1.getCbFileSelector().setPlaceholder("");
                architectureCell1.getCbFileSelector().addStyleName(COMBOBOX_BORDERLESS);
                architectureCell1.getCbFileSelector().setItemCaptionGenerator(FileItem::getName);
                architectureCell1.getCbFileSelector().setItemIconGenerator(FileItem::getIcon);
                architectureCell1.getCbFileSelector().setEmptySelectionAllowed(false);
                architectureCell1.getLblTitle().setVisible(false);
                architectureCell1.getBtnAlt1().setVisible(false);
                architectureCell1.getLblAreaTitle().setValue(card.getName());
                if(isEditor()) {
                    architectureCell1.getBtnEditName().setVisible(true);
                    architectureCell1.getBtnEditName().setCaption("");
                    architectureCell1.getBtnEditName().addClickListener(clickEvent -> {
                        final Window window = new Window();
                        MTextField title = new MTextField("title", card.getName(), valueChangeEvent -> {
                            card.setName(valueChangeEvent.getValue());
                            knowArchiColumnRepository.save(archiColumn);
                            window.close();
                        }).withValueChangeMode(ValueChangeMode.BLUR);
                        window.setContent(title);
                        window.setModal(true);
                        window.setDraggable(false);
                        window.setClosable(false);
                        window.setResizable(false);
                        UI.getCurrent().addWindow(window);
                    });
                }

                architectureCell1.getCbFileSelector().addValueChangeListener(event1 -> {
                    DocumentMetadata documentMetadata = fileItems.get(event1.getValue());
                    architectureCell1.getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(dropboxAPI.getSpecificBinaryFile(rootFilePath+card.getFolder()+"/"+documentMetadata.getPreview())),
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

                for (String file : dropboxAPI.getFilesInFolder(rootFilePath+card.getFolder())) {
                    if(!FilenameUtils.getExtension(file).equals("json")) continue;
                    String documentMetadataJson = dropboxAPI.getSpecificTextFile(file, StandardCharsets.UTF_8);
                    ObjectMapper objectMapper = new ObjectMapper();
                    DocumentMetadata documentMetadata = null;
                    try {
                        documentMetadata = objectMapper.readValue(documentMetadataJson, DocumentMetadata.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FileItem fileItem = new FileItem("\t"+documentMetadata.getHeadline(), icons.get(documentMetadata.getFiletype()));

                    fileItems.put(fileItem, documentMetadata);
                }

                architectureCell1.getCbFileSelector().setItems(fileItems.keySet());
                architectureCell1.getCbFileSelector().setSelectedItem(fileItems.keySet().stream().findFirst().get());

                cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                        .withComponent(architectureCell1);

            }

            if(isEditor()) {
                MButton btn = new MButton(MaterialIcons.ADD).withStyleName("icon-only").withListener(clickEvent -> {
                    Window window = new Window();
                    KnowledgeArchitectureCard newCard = new KnowledgeArchitectureCard();
                    MVerticalLayout vl = new MVerticalLayout(
                            new MTextField("Name", "", valueChangeEvent -> {
                                newCard.setName(valueChangeEvent.getValue());
                            }).withValueChangeMode(ValueChangeMode.BLUR),
                            new TextArea("Folder", "", valueChangeEvent -> {
                                newCard.setFolder(valueChangeEvent.getValue());
                            }),
                            new MButton("Save", clickEvent1 -> {
                                item.getCards().add(newCard);
                                knowArchiColumnRepository.save(archiColumn);
                                window.close();
                            }));
                    window.setContent(vl);
                    window.setModal(true);
                    window.setDraggable(false);
                    window.setClosable(false);
                    window.setResizable(false);
                    UI.getCurrent().addWindow(window);
                });
                cardsRow.addColumn().withDisplayRules(12, 12, 4, 4)
                        .withComponent(btn);
            }



            /*
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


            }

             */
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

        box.getImgTop().setSource(photoService.getRelatedPhoto(item.getPhotoUuid()));
        /*
        box.getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                new ByteArrayInputStream(dropboxAPI.getSpecificBinaryFile(rootFilePath + "" + item.getKey() + ".png")),
                Math.random()+".png"));
         */
        box.getVlContent().addComponent(
                new MVerticalLayout(
                        new MLabel(item.getName())
                                .withFullWidth()
                                .withStyleName("small bold"),
                        new MLabel(item.getContent())
                                .withContentMode(ContentMode.HTML)
                                .withFullWidth()
                                .withStyleName("tiny bold"),
                        new MHorizontalLayout(
                            new MButton(MaterialIcons.PHOTO).withStyleName("icon-only tiny").withListener(clickEvent -> {
                                if(item.getPhotoUuid().equals("")) item.setPhotoUuid(UUID.randomUUID().toString());
                                knowArchiColumnRepository.save(archiColumn);
                                new PhotoUploader(item.getPhotoUuid(), 800, 400, "upload logo", PhotoUploader.Step.UPLOAD, photoRepository).getUploader();
                            }),
                            new MButton(MaterialIcons.EDIT).withStyleName("icon-only tiny").withListener(clickEvent -> {
                                Window window = new Window();
                                MVerticalLayout vl = new MVerticalLayout(
                                        new MTextField("Name", item.getName(), valueChangeEvent -> {
                                            item.setName(valueChangeEvent.getValue());
                                        }).withValueChangeMode(ValueChangeMode.BLUR),
                                        new RichTextArea("Content", item.getContent(), valueChangeEvent -> {
                                            item.setContent(valueChangeEvent.getValue());
                                        }),
                                        new MButton("Save", clickEvent1 -> {
                                            knowArchiColumnRepository.save(archiColumn);
                                            window.close();
                                        }));
                                window.setContent(vl);
                                window.setModal(true);
                                window.setDraggable(false);
                                window.setClosable(false);
                                window.setResizable(false);
                                UI.getCurrent().addWindow(window);
                            })
                        ).withVisible(isEditor())
                )
        );
        box.getVlContent().setHeight(175, Unit.PIXELS);
        return box;
    }

    private boolean isEditor() {
        return userService.getLoggedInUser().get().getUsername().equals("simon.gomez") || userService.getLoggedInUser().get().getUsername().equals("hans.lassen");
    }

    private VerticalLayout createArchitectureColumn2(KnowledgeArchitectureColumn column) {
        VerticalLayout layout = new VerticalLayout(
                new MLabel(column.getName())
                        .withFullWidth()
                        .withStyleName("align-center large bold"));
        layout.addLayoutClickListener(layoutClickEvent -> {
            final Window window = new Window();
            MTextField title = new MTextField("title", column.getName(), valueChangeEvent -> {
                column.setName(valueChangeEvent.getValue());
                knowArchiColumnRepository.save(column);
                window.close();
            }).withValueChangeMode(ValueChangeMode.BLUR);
            window.setContent(title);
            window.setModal(true);
            window.setDraggable(false);
            window.setClosable(false);
            window.setResizable(false);
            UI.getCurrent().addWindow(window);
        });

        return getTitleComponent(column.getColor(), layout);
    }

    private VerticalLayout getTitleComponent(String color, Component layout) {
        MVerticalLayout column = new MVerticalLayout(layout).withWidth(100, Unit.PERCENTAGE).withMargin(false);

        gridRow.addColumn()
                .withDisplayRules(12, 12, 2, 2)
                .withStyleName(color)
                .withComponent(column);
        return column;
    }

}