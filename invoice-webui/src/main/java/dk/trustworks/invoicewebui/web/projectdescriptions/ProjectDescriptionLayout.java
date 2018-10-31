package dk.trustworks.invoicewebui.web.projectdescriptions;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
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
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.projectdescriptions.components.ProjectDescriptionDesign;
import dk.trustworks.invoicewebui.web.projectdescriptions.components.ProjectDescriptionFormDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SpringComponent
@SpringUI
public class ProjectDescriptionLayout extends VerticalLayout {

    private UserRepository userRepository;
    private ClientRepository clientRepository;
    private PhotoService photoService;
    private ProjectDescriptionRepository projectDescriptionRepository;
    private ProjectDescriptionUserRepository projectDescriptionUserRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow bubblesRow;

    @Autowired
    public ProjectDescriptionLayout(UserRepository userRepository, ClientRepository clientRepository, PhotoService photoService, ProjectDescriptionRepository projectDescriptionRepository, ProjectDescriptionUserRepository projectDescriptionUserRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.projectDescriptionRepository = projectDescriptionRepository;
        this.photoService = photoService;
        this.projectDescriptionUserRepository = projectDescriptionUserRepository;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public ProjectDescriptionLayout init() {
        bubblesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadProjectDescriptions();
        return this;
    }

    private void loadProjectDescriptions() {
        bubblesRow.removeAllComponents();

        Iterable<ProjectDescription> allProjDesc = projectDescriptionRepository.findAll();

        for (ProjectDescription projectDescription : allProjDesc) {
            ProjectDescriptionDesign projectDescriptionDesign = new ProjectDescriptionDesign();

            projectDescriptionDesign.getLblHeading().setValue(projectDescription.getName());
            projectDescriptionDesign.getLblDescription().setValue(projectDescription.getDescription());

            projectDescriptionDesign.getBtnEdit().addClickListener(event -> {
                createProjectDescriptionForm(Optional.of(projectDescription));
            });

            List<Image> triangleImageList = new ArrayList<>();

            for (ProjectDescriptionUser projectDescriptionUser : projectDescriptionUserRepository.findByProjectDescription(projectDescription)) {
                Image image = photoService.getRoundMemberImage(projectDescriptionUser.getUser(), false);
                Image triangle = new Image(null, new ThemeResource("images/triangle-medium-light-blue.png"));
                triangle.setVisible(false);

                triangleImageList.add(triangle);

                projectDescriptionDesign.getPhotoContainer().addComponent(
                        new MVerticalLayout(image, triangle)
                                .withMargin(false)
                                //.withHeight(75+30, Unit.PIXELS)
                                .withAlign(triangle, Alignment.BOTTOM_CENTER));

                projectDescriptionDesign.getLblUserDescription().setValue("");
                image.addClickListener(event -> {
                    projectDescriptionDesign.getLblUserDescription().setValue(projectDescriptionUser.getDescription());
                    for (Image t : triangleImageList) {
                        t.setVisible(false);
                    }
                    projectDescriptionDesign.getUserTextContentHolder().setStyleName("v-scrollable medium-light-blue");
                    triangle.setVisible(true);
                });
            }
            Resource resource = photoService.getRelatedPhoto(projectDescription.getClient().getUuid());

            projectDescriptionDesign.getImgTop().setSource(resource);


            bubblesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(projectDescriptionDesign);
        }

        bubblesRow.addColumn().withDisplayRules(12, 12, 6 ,4).withComponent(
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
            loadProjectDescriptions();
        });

        window.setContent(formDesign);
        UI.getCurrent().addWindow(window);
    }

    private void saveExistingProjectDescription(ProjectDescription projectDescription, HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        System.out.println("ProjectDescriptionLayout.saveExistingProjectDescription");
        projectDescription = projectDescriptionRepository.findOne(projectDescription.getId());
        System.out.println("projectDescription = " + projectDescription);

        projectDescription.setClient(formDesign.getCbClient().getSelectedItem().get());
        projectDescription.setName(formDesign.getTxtName().getValue());
        projectDescription.setDescription(formDesign.getTxtDescription().getValue());
        projectDescription = projectDescriptionRepository.save(projectDescription);

        projectDescriptionUserRepository.deleteByProjectDescription(projectDescription);

        for (User user : userStorieMap.keySet()) {
            ProjectDescriptionUser desctiptionUser = new ProjectDescriptionUser(user, projectDescription, userStorieMap.get(user).getValue());
            projectDescriptionUserRepository.save(desctiptionUser);
        }
    }

    private void saveNewProjectDescription(HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        System.out.println("ProjectDescriptionLayout.saveNewProjectDescription");
        ProjectDescription projectDescription = new ProjectDescription(
                formDesign.getCbClient().getSelectedItem().get(),
                formDesign.getTxtName().getValue(),
                formDesign.getTxtDescription().getValue()
        );
        projectDescription = projectDescriptionRepository.save(projectDescription);

        for (User user : userStorieMap.keySet()) {
            ProjectDescriptionUser desctiptionUser = new ProjectDescriptionUser(user, projectDescription, userStorieMap.get(user).getValue());
            projectDescriptionUserRepository.save(desctiptionUser);
        }
    }

    private void addUserStorieEvent(HashMap<User, TextArea> userStorieMap, ProjectDescriptionFormDesign formDesign) {
        ComboBox<User> selectUser = new ComboBox<>();
        selectUser.setWidth(100, Unit.PERCENTAGE);
        selectUser.setEmptySelectionAllowed(false);
        selectUser.setEmptySelectionCaption("Select Consultant");
        selectUser.setPlaceholder("Select Consultant");
        selectUser.setItemCaptionGenerator(User::getUsername);
        selectUser.setItems(userRepository.findByOrderByUsername());
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
