package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.functions.TokenEventListener;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.KnowArchiColumnRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.ArchitectureCell;
import dk.trustworks.invoicewebui.web.knowledge.components.SideBannerDesign;
import dk.trustworks.invoicewebui.web.model.FileItem;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import dk.trustworks.invoicewebui.web.project.components.TokenListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.ui.themes.ValoTheme.COMBOBOX_BORDERLESS;

@SpringComponent
@SpringUI
public class BusinessArchitectureLayout extends VerticalLayout {

    private final String rootFilePath = "/Shared/Administration/Intra/knowledge_architecture/";

    private static final Map<String, ThemeResource> icons = new HashMap();

    private static boolean isEditor = false;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private KnowArchiColumnRepository knowArchiColumnRepository;

    @Autowired
    private DropboxAPI dropboxAPI;

    private ResponsiveLayout mainLayout;
    private ResponsiveRow gridRow;
    private Box mainBox;
    private Button backButton;

    public BusinessArchitectureLayout init() {
        icons.clear();
        icons.put("pptx", new ThemeResource("images/icons/powerpoint.png"));
        icons.put("docx", new ThemeResource("images/icons/word.png"));

        this.removeAllComponents();

        mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        mainBox = new Box();
        mainBox.getContent().addComponent(mainLayout);
        mainBox.getContent().setMargin(false);

        backButton = new MButton("back").withStyleName("floating").withListener(clickEvent -> {
            init();
        }).withVisible(false);

        Button editButton = new MButton("edit").withStyleName("floating").withVisible(false).withListener(clickEvent -> {
            isEditor = !isEditor;
            init();
        });

        if(userService.getLoggedInUser().get().getUsername().equals("simon.gomez") ||
                userService.getLoggedInUser().get().getUsername().equals("hans.lassen") ||
                userService.getLoggedInUser().get().getUsername().equals("elvi.nissen") ||
                userService.getLoggedInUser().get().getUsername().equals("regitze.carneiro") ||
                userService.getLoggedInUser().get().getUsername().equals("stephan.jensen") ||
                userService.getLoggedInUser().get().getUsername().equals("thomas.buchholdt") ||
                userService.getLoggedInUser().get().getUsername().equals("brian.flaskager") ||
                userService.getLoggedInUser().get().getUsername().equals("anna.mette.hansen") ||
                userService.getLoggedInUser().get().getUsername().equals("sebastian.frandsen") ||
                userService.getLoggedInUser().get().getUsername().equals("jan.borg") ||
                userService.getLoggedInUser().get().getUsername().equals("lars.albert")
        ) editButton.setVisible(true);

        this.addComponents(new VerticalLayout(new HorizontalLayout(backButton, editButton)), mainBox);

        gridRow = mainLayout.addRow();

        VerticalLayout firstColumn = getTitleComponent("bg-grey", new VerticalLayout(new MLabel("-").withStyleName("align-center large bold")));
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
            drawDetailPage(archiColumn, item);
        });

        box.addStyleName("semi-white-bg");

        box.getImgTop().setSource(photoService.getRelatedPhoto(item.getPhotoUuid()));

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

    private void drawDetailPage(KnowledgeArchitectureColumn archiColumn, KnowledgeArchitectureCell item) {
        mainBox.getContent().removeAllComponents();
        backButton.setVisible(true);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        mainBox.getContent().addComponent(responsiveLayout);

        ResponsiveRow headerRow = responsiveLayout.addRow();
        headerRow.setStyleName(archiColumn.getColor(), true);

        VerticalLayout nameLayout = new MVerticalLayout(
                new MLabel(item.getName().toUpperCase()).withStyleName("h4")
        ).withFullWidth();
        nameLayout.addLayoutClickListener(layoutClickEvent -> {
            final Window window = new Window();
            MTextField title = new MTextField("title", item.getName());
            window.setContent(new MVerticalLayout(title, new MButton("Save", clickEvent -> {
                item.setName(title.getValue());
                knowArchiColumnRepository.save(archiColumn);
                window.close();
            })));
            window.setModal(true);
            window.setDraggable(false);
            window.setClosable(false);
            window.setResizable(false);
            UI.getCurrent().addWindow(window);
        });

        VerticalLayout descLayout = new VerticalLayout(
                new MLabel(item.getDescription()).withFullWidth().withContentMode(ContentMode.HTML)
        );
        descLayout.addLayoutClickListener(layoutClickEvent -> {
            final Window window = new Window();
            TextArea title = new TextArea("description", item.getDescription());
            title.setWidth(300, Unit.PIXELS);
            window.setContent(new MVerticalLayout(title, new MButton("Save", clickEvent -> {
                item.setDescription(title.getValue());
                knowArchiColumnRepository.save(archiColumn);
                window.close();
            })));
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
                        .withMargin(true)
                        .withSpacing(true)
        );
        headerRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(
                new MVerticalLayout(
                        new MLabel("<b>Relevante kompetencer:</b>").withContentMode(ContentMode.HTML).withVisible(false),
                        new MLabel("- Proceskortlægning&nbsp;&nbsp;&nbsp;" +
                                "- Forretningsregler<br />" +
                                "<br />" +
                                "- User experience&nbsp;&nbsp;&nbsp;" +
                                "- Behovsafdækning").withContentMode(ContentMode.HTML).withVisible(false)
                        )
                        .withMargin(true)
                        .withSpacing(true)
        );

        ResponsiveRow cardsRow = responsiveLayout.addRow();

        for (KnowledgeArchitectureCard card : item.getCards()) {
            Map<FileItem, KnowledgeArchitectureFile> fileItems = new HashMap<>();

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
                    editNameWindow(archiColumn, card);
                });
            }

            List<Registration> registrationList = new ArrayList<>();

            architectureCell1.getCbFileSelector().addValueChangeListener(event1 -> {
                // Clear button events
                registrationList.forEach(Registration::remove);
                registrationList.clear();

                KnowledgeArchitectureFile cardFile = fileItems.get(event1.getValue());
                architectureCell1.getImgTop().setSource(photoService.getRelatedPhoto(cardFile.getPreview()));
                architectureCell1.getVlConsultants().removeAllComponents();

                for (String author : cardFile.getAuthors().split(",")) {
                    if(author.length()<2) continue;
                    architectureCell1.getVlConsultants().addComponent(photoService.getRoundMemberImage(userService.findByUUID(author), false, 50, Unit.PIXELS));
                }
                architectureCell1.getImgCustomer().setSource(photoService.getRelatedPhoto(cardFile.getCustomeruuid()));
                architectureCell1.getImgCustomer().setHeight(50, Unit.PIXELS);
                architectureCell1.getContent().removeAllComponents();
                architectureCell1.getContent().addComponent(new MLabel(cardFile.getDescription()).withContentMode(ContentMode.HTML).withFullWidth());

                byte[] file = dropboxAPI.getSpecificBinaryFile(rootFilePath + cardFile.getFilename());
                FileDownloader fileDownloader = new FileDownloader(new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(file), cardFile.getFilename()));
                fileDownloader.extend(architectureCell1.getBtnDownloadFile());
                if(isEditor()) {
                    architectureCell1.getVlAdminButtons().setVisible(true);

                    architectureCell1.getBtnAddFile().getListeners(Button.ClickListener.class);
                    registrationList.add(architectureCell1.getBtnAddFile().addClickListener(clickEvent -> {
                        KnowledgeArchitectureFile knowledgeArchitectureFile = new KnowledgeArchitectureFile();
                        knowledgeArchitectureFile.setHeadline("new file");
                        knowledgeArchitectureFile.setAuthors("");
                        card.getFiles().add(knowledgeArchitectureFile);
                        knowledgeArchitectureFile.setKnowledgeArchitectureCard(card);
                        knowArchiColumnRepository.save(archiColumn);
                        init();
                    }));

                    registrationList.add(architectureCell1.getBtnImgFile().addClickListener(clickEvent -> {
                        if(cardFile.getPreview() == null || cardFile.getPreview().equals("")) cardFile.setPreview(UUID.randomUUID().toString());
                        knowArchiColumnRepository.save(archiColumn);
                        new PhotoUploader(cardFile.getPreview(), 800, 400, "upload logo", PhotoUploader.Step.UPLOAD, photoRepository).getUploader();
                    }));

                    /*
                    architectureCell1.getVlAdminButtons().addComponent(new MButton("", event2 -> {
                        cardFile.getKnowledgeArchitectureCard().getFiles().remove(cardFile);
                        cardFile.setKnowledgeArchitectureCard(null);
                        knowArchiColumnRepository.save(archiColumn);
                        init();
                    }).withStyleName("icon-only tiny").withIcon(MaterialIcons.DELETE_FOREVER));
                    */
                    registrationList.add(architectureCell1.getBtnEditFile().addClickListener(clickEvent -> {
                        List<User> allUsers = userService.findAll();
                        List<User> selectedUsers = new ArrayList<>();
                        for (String useruuids : cardFile.getAuthors().split(",")) {
                            if(useruuids.length()<2) continue;
                            selectedUsers.add(userService.findByUUID(useruuids));
                        }

                        TokenListImpl tokenList = new TokenListImpl(
                                allUsers.stream().map(User::getUsername).sorted().collect(Collectors.toList()),
                                selectedUsers.stream().map(User::getUsername).sorted().collect(Collectors.toList()),
                                "select author"
                        );

                        tokenList.addTokenListener(new TokenEventListener() {
                            @Override
                            public void onTokenAdded(String token) {
                                selectedUsers.add(allUsers.stream().filter(user -> user.getUsername().equals(token)).findFirst().get());
                            }

                            @Override
                            public void onTokenRemoved(String token) {
                                selectedUsers.remove(selectedUsers.stream().filter(user -> user.getUsername().equals(token)).findFirst().orElse(new User()));
                            }
                        });

                        ComboBox<Client> clientComboBox = new ComboBox<>("Client: ", clientService.findAll());
                        clientComboBox.setItemCaptionGenerator(Client::getName);
                        ComboBox<Project> projectComboBox = new ComboBox<>("Project: ");
                        if(cardFile.getCustomeruuid()!=null) {
                            Client client = clientService.findOne(cardFile.getCustomeruuid());
                            clientComboBox.setSelectedItem(client);
                            projectComboBox.setItems(projectService.findByClientuuidOrderByNameAsc(client.getUuid()));
                        }
                        projectComboBox.setItemCaptionGenerator(Project::getName);
                        if(cardFile.getProjectuuid()!=null) projectComboBox.setSelectedItem(projectService.findOne(cardFile.getProjectuuid()));
                        clientComboBox.addValueChangeListener(valueChangeEvent -> {
                            projectComboBox.clear();
                            projectComboBox.setItems(projectService.findByClientuuidOrderByNameAsc(valueChangeEvent.getValue().getUuid()));
                        });

                        Window window = new Window();
                        MVerticalLayout vl = new MVerticalLayout(
                                new MTextField("Headline", (cardFile.getHeadline()!=null)?cardFile.getHeadline():"", valueChangeEvent -> {
                                    cardFile.setHeadline(valueChangeEvent.getValue());
                                }).withValueChangeMode(ValueChangeMode.BLUR),
                                new MTextField("Filetype", (cardFile.getFiletype()!=null)?cardFile.getFiletype():"", valueChangeEvent -> {
                                    cardFile.setFiletype(valueChangeEvent.getValue());
                                }).withValueChangeMode(ValueChangeMode.BLUR),
                                new MTextField("Filename", (cardFile.getFilename()!=null)?cardFile.getFilename():"", valueChangeEvent -> {
                                    cardFile.setFilename(valueChangeEvent.getValue());
                                }).withValueChangeMode(ValueChangeMode.BLUR),
                                new RichTextArea("Description", (cardFile.getDescription()!=null)?cardFile.getDescription():"", valueChangeEvent -> {
                                    cardFile.setDescription(valueChangeEvent.getValue());
                                }),
                                clientComboBox,
                                projectComboBox,
                                new DateField("Date", (cardFile.getDate()!=null)?cardFile.getDate(): LocalDate.now(), valueChangeEvent -> {
                                    cardFile.setDate(valueChangeEvent.getValue());
                                }),
                                tokenList,
                                new MButton("Save", clickEvent1 -> {
                                    cardFile.setAuthors(selectedUsers.stream().map(User::getUuid).collect(Collectors.joining(",")));
                                    cardFile.setCustomeruuid(clientComboBox.getSelectedItem().get().getUuid());
                                    cardFile.setProjectuuid(projectComboBox.getSelectedItem().get().getUuid());
                                    knowArchiColumnRepository.save(archiColumn);
                                    window.close();
                                }));
                        window.setContent(vl);
                        window.setModal(true);
                        window.setDraggable(false);
                        window.setClosable(false);
                        window.setResizable(false);
                        UI.getCurrent().addWindow(window);
                    }));
                }
            });

            for (KnowledgeArchitectureFile cardFile : card.getFiles()) {
                FileItem fileItem = new FileItem("\t"+cardFile.getHeadline(), icons.get(cardFile.getFiletype()));
                fileItems.put(fileItem, cardFile);
            }

            architectureCell1.getCbFileSelector().setItems(fileItems.keySet());
            architectureCell1.getCbFileSelector().setSelectedItem(fileItems.keySet().stream().findFirst().orElse(null));

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
                        new MTextField("Folder", "", valueChangeEvent -> {
                            newCard.setFolder(valueChangeEvent.getValue());
                        }),
                        new MButton("Save", clickEvent1 -> {
                            KnowledgeArchitectureFile file = new KnowledgeArchitectureFile();
                            file.setAuthors("");
                            newCard.getFiles().add(file);
                            file.setKnowledgeArchitectureCard(newCard);
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
    }

    private void editNameWindow(KnowledgeArchitectureColumn archiColumn, KnowledgeArchitectureCard card) {
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
    }

    private boolean isEditor() {
        return isEditor;
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