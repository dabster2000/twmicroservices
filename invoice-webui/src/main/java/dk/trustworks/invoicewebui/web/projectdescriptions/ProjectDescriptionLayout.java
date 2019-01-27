package dk.trustworks.invoicewebui.web.projectdescriptions;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.HasValue;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.ProjectDescription;
import dk.trustworks.invoicewebui.model.ProjectDescriptionUser;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ProjectDescriptionRepository;
import dk.trustworks.invoicewebui.repositories.ProjectDescriptionUserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.projectdescriptions.components.ProjectDescriptionDesign;
import dk.trustworks.invoicewebui.web.projectdescriptions.components.ProjectDescriptionFormDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.*;
import java.util.stream.Collectors;

@SpringComponent
@SpringUI
public class ProjectDescriptionLayout extends VerticalLayout {

    private final UserService userService;
    private final ClientRepository clientRepository;
    private final PhotoService photoService;
    private final ProjectDescriptionRepository projectDescriptionRepository;
    private final ProjectDescriptionUserRepository projectDescriptionUserRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow projectDescriptionsRow;
    private ResponsiveRow filterRow;

    private final List<ResponsiveColumn> projectDescriptionDesignList = new ArrayList<>();

    private final Map<Client, List<ResponsiveColumn>> clientList = new HashMap();
    private final Map<User, List<ResponsiveColumn>> userList = new HashMap<>();

    @Autowired
    public ProjectDescriptionLayout(UserService userService, ClientRepository clientRepository, PhotoService photoService, ProjectDescriptionRepository projectDescriptionRepository, ProjectDescriptionUserRepository projectDescriptionUserRepository) {
        this.userService = userService;
        this.clientRepository = clientRepository;
        this.projectDescriptionRepository = projectDescriptionRepository;
        this.photoService = photoService;
        this.projectDescriptionUserRepository = projectDescriptionUserRepository;

        filterRow = responsiveLayout.addRow();
        projectDescriptionsRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public ProjectDescriptionLayout init() {
        refreshPage();
        return this;
    }

    private void refreshPage() {
        projectDescriptionsRow.removeAllComponents();
        projectDescriptionDesignList.clear();
        filterRow.removeAllComponents();
        clientList.clear();
        userList.clear();

        updateClientAndUserList();
        createFilterRow();
        loadProjectDescriptions();
    }

    private void createFilterRow() {
        ComboBox<Client> clientComboBox = new ComboBox<>("Client filter");
        ComboBox<User> userComboBox = new ComboBox<>("Consultant filter");

        clientComboBox.setStyleName("floating");
        clientComboBox.setItems(clientList.keySet());
        clientComboBox.setEmptySelectionAllowed(true);
        clientComboBox.setEmptySelectionCaption("No filter");
        clientComboBox.setItemCaptionGenerator(Client::getName);
        clientComboBox.setWidth(100, Unit.PERCENTAGE);
        clientComboBox.addValueChangeListener(event -> {
            filterDesigns(userComboBox, event, clientList);
        });
        filterRow.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(new MVerticalLayout(clientComboBox));

        userComboBox.setStyleName("floating");
        userComboBox.setItems(userList.keySet());
        userComboBox.setEmptySelectionAllowed(true);
        userComboBox.setEmptySelectionCaption("No filter");
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setWidth(100, Unit.PERCENTAGE);
        userComboBox.addValueChangeListener(event -> {
            filterDesigns(clientComboBox, event, userList);
        });
        filterRow.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(new MVerticalLayout(userComboBox));
    }

    private void updateClientAndUserList() {
        for (ProjectDescription projectDescription : projectDescriptionRepository.findAll()) {
            Client client = projectDescription.getClient();
            System.out.println("client = " + client);
            clientList.putIfAbsent(client, new ArrayList<>());

            for (ProjectDescriptionUser projectDescriptionUser : projectDescriptionUserRepository.findByProjectDescription(projectDescription)) {
                userList.putIfAbsent(projectDescriptionUser.getUser(), new ArrayList<>());
            }
        }
    }

    private void filterDesigns(ComboBox comboBox, HasValue.ValueChangeEvent event, Map map) {
        if(!event.isUserOriginated()) return;
        comboBox.clear();
        if (event.getValue()!=null) {
            for (ResponsiveColumn design : projectDescriptionDesignList) {
                design.setVisible(false);
            }
            for (ResponsiveColumn design : (List<ResponsiveColumn>) map.get(event.getValue())) {
                design.setVisible(true);
            }
        } else {
            for (ResponsiveColumn design : projectDescriptionDesignList) {
                design.setVisible(true);
            }
        }
    }

    private void loadProjectDescriptions() {
        List<ProjectDescription> allProjDesc = projectDescriptionRepository.findAll().stream().sorted(Comparator.comparing(o1 -> o1.getClient().getName())).collect(Collectors.toList());

        for (ProjectDescription projectDescription : allProjDesc) {
            ProjectDescriptionDesign projectDescriptionDesign = new ProjectDescriptionDesign();

            ResponsiveColumn column = projectDescriptionsRow.addColumn();
            column.withDisplayRules(12, 12, 6,4).withComponent(projectDescriptionDesign);
            projectDescriptionDesignList.add(column);

            clientList.get(projectDescription.getClient()).add(column);

            projectDescriptionDesign.getLblHeading().setValue(projectDescription.getName());
            projectDescriptionDesign.getLblDescription().setValue(projectDescription.getDescription());

            projectDescriptionDesign.getBtnEdit().addClickListener(event -> {
                createProjectDescriptionForm(Optional.of(projectDescription));
            });

            List<Image> triangleImageList = new ArrayList<>();

            boolean firstUser = true;
            for (ProjectDescriptionUser projectDescriptionUser : projectDescriptionUserRepository.findByProjectDescription(projectDescription).stream().sorted(Comparator.comparing(o -> o.getUser().getUsername())).collect(Collectors.toCollection(ArrayList::new))) {
                userList.get(projectDescriptionUser.getUser()).add(column);

                Image image = photoService.getRoundMemberImage(projectDescriptionUser.getUser(), false);
                Image triangle = new Image(null, new ThemeResource("images/triangle-medium-light-blue.png"));
                triangle.setVisible(firstUser);

                triangleImageList.add(triangle);

                projectDescriptionDesign.getPhotoContainer().addComponent(
                        new MVerticalLayout(image, triangle)
                                .withMargin(false)
                                //.withHeight(75+30, Unit.PIXELS)
                                .withAlign(triangle, Alignment.BOTTOM_CENTER));

                if(firstUser) projectDescriptionDesign.getLblUserDescription().setValue(projectDescriptionUser.getDescription());
                //else projectDescriptionDesign.getLblUserDescription().setValue("");
                projectDescriptionDesign.getUserTextContentHolder().setStyleName("v-scrollable medium-light-blue");
                image.addClickListener(event -> {
                    projectDescriptionDesign.getLblUserDescription().setValue(projectDescriptionUser.getDescription());
                    for (Image t : triangleImageList) {
                        t.setVisible(false);
                    }
                    triangle.setVisible(true);
                });
                firstUser = false;
            }
            //if(!triangleImageList.isEmpty()) triangleImageList.get(0).setVisible(true);
            Resource resource = photoService.getRelatedPhoto(projectDescription.getClient().getUuid());

            projectDescriptionDesign.getImgTop().setSource(resource);
        }

        projectDescriptionsRow.addColumn().withDisplayRules(12, 12, 6 ,4).withComponent(
                new MButton("ADD").withListener(event -> createProjectDescriptionForm(Optional.empty()))
        );
    }

    private void createProjectDescriptionForm(Optional<ProjectDescription> projectDescription) {
        HashMap<User, TextArea> userStorieMap = new HashMap<>();

        Window window = new Window("Add project description");
        window.setWidth(400, Unit.PIXELS);
        window.setModal(true);

        ProjectDescriptionFormDesign formDesign = new ProjectDescriptionFormDesign();
        formDesign.getCbClient().setItemCaptionGenerator(Client::getName);
        formDesign.getCbClient().setItems(clientRepository.findByOrderByName());

        projectDescription.ifPresent(projectDescription1 -> {
            List<ProjectDescriptionUser> desctiptionUsers = projectDescriptionUserRepository.findByProjectDescription(projectDescription1);
            for (ProjectDescriptionUser desctiptionUser : desctiptionUsers) {
                TextArea userDesc = createUserStorieForm(userStorieMap, formDesign, desctiptionUser.getUser());
                userDesc.setValue(desctiptionUser.getDescription());
                userStorieMap.put(desctiptionUser.getUser(), userDesc);
            }

            //formDesign.getCbClient().setVisible(false);
            formDesign.getCbClient().setSelectedItem(projectDescription1.getClient());
            formDesign.getTxtName().setValue(projectDescription1.getName());
            formDesign.getTxtDescription().setValue(projectDescription1.getDescription());
        });


        formDesign.getBtnAddUserStorie().addClickListener(event1 -> addUserStorieEvent(userStorieMap, formDesign));
        formDesign.getBtnSave().addClickListener(event1 -> {
            // TODO: MISSING VALIDATION
            if(!projectDescription.isPresent()) saveNewProjectDescription(userStorieMap, formDesign);
            else saveExistingProjectDescription(projectDescription.get(), userStorieMap, formDesign);

            window.close();
            UI.getCurrent().removeWindow(window);

            refreshPage();
        });

        window.setContent(formDesign);
        UI.getCurrent().addWindow(window);
    }

    private void saveExistingProjectDescription(ProjectDescription projectDescription, HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        projectDescription = projectDescriptionRepository.findOne(projectDescription.getId());

        projectDescription.setClient(formDesign.getCbClient().getSelectedItem().get());
        projectDescription.setName(formDesign.getTxtName().getValue());
        projectDescription.setDescription(formDesign.getTxtDescription().getValue());
        projectDescription = projectDescriptionRepository.save(projectDescription);

        projectDescriptionUserRepository.deleteByProjectDescription(projectDescription);

        for (User user : userStorieMap.keySet()) {
            ProjectDescriptionUser descriptionUser = new ProjectDescriptionUser(user, projectDescription, userStorieMap.get(user).getValue());
            projectDescriptionUserRepository.save(descriptionUser);
        }
    }

    private void saveNewProjectDescription(HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        ProjectDescription projectDescription = new ProjectDescription(
                formDesign.getCbClient().getSelectedItem().get(),
                formDesign.getTxtName().getValue(),
                formDesign.getTxtDescription().getValue()
        );
        projectDescription = projectDescriptionRepository.save(projectDescription);

        for (User user : userStorieMap.keySet()) {
            ProjectDescriptionUser descriptionUser = new ProjectDescriptionUser(user, projectDescription, userStorieMap.get(user).getValue());
            projectDescriptionUserRepository.save(descriptionUser);
        }
    }

    private void addUserStorieEvent(HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        ComboBox<User> selectUser = new ComboBox<>();
        selectUser.setWidth(100, Unit.PERCENTAGE);
        selectUser.setEmptySelectionAllowed(false);
        selectUser.setEmptySelectionCaption("Select Consultant");
        //selectUser.setPlaceholder("Select Consultant");
        selectUser.setItemCaptionGenerator(User::getUsername);
        selectUser.setItems(userService.findAll());
        selectUser.addValueChangeListener(event2 -> {
            if(userStorieMap.containsKey(event2.getValue())) return;
            formDesign.getVlUserStories().removeComponent(selectUser);
            formDesign.getBtnAddUserStorie().setVisible(true);

            TextArea userDesc = createUserStorieForm(userStorieMap, formDesign, event2.getValue());
            userStorieMap.put(event2.getValue(), userDesc);
        });
        formDesign.getBtnAddUserStorie().setVisible(false);
        formDesign.getVlUserStories().addComponent(selectUser);
    }

    private TextArea createUserStorieForm(HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign, User user) {
        MHorizontalLayout horizontalLayout = new MHorizontalLayout().withFullWidth();
        Image image = photoService.getRoundMemberImage(user, false);
        TextArea userDesc = new TextArea();
        userDesc.setHeight(75, Unit.PIXELS);
        userDesc.setWidth(100, Unit.PERCENTAGE);
        Button btnRemove = new MButton(MaterialIcons.DELETE)
                .withStyleName("icon-only")
                .withListener(event3 -> {
                    formDesign.getVlUserStories().removeComponent(horizontalLayout);
                    userStorieMap.remove(user);
                });

        horizontalLayout.with(image, userDesc, btnRemove).withExpand(userDesc, 1.0f);
        formDesign.getVlUserStories().addComponent(horizontalLayout);
        return userDesc;
    }
}
