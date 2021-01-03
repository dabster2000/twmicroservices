package dk.trustworks.invoicewebui.web.faq.components;

import com.itextpdf.kernel.xmp.impl.Base64;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Faq;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.FaqRepository;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.labelbutton.LabelButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@SpringComponent
@SpringUI
public class FaqBoard extends VerticalLayout {

    private final FaqRepository faqRepository;

    @Autowired
    public FaqBoard(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Transactional
    public FaqBoard init() {
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        this.removeAllComponents();

        Box card = new Box();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        card.getContent().addComponent(responsiveLayout);
        ResponsiveRow row = responsiveLayout.addRow();

        SearchDesign searchDesign = new SearchDesign();

        String group = "";
        VerticalLayout vl = null;
        boolean firstGroup = true;

        Accordion accFaq = searchDesign.getAccFaq();

        row.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(searchDesign);

        Map<String, LabelButton> questionMap = new HashMap<>();

        for (Faq faq : faqRepository.findByOrderByFaqgroupAscTitleAsc()) {
            if(!faq.getFaqgroup().equals(group)) {
                if(!firstGroup) {
                    if(user.getRoleList().stream().anyMatch(role -> role.getRole().equals(RoleType.EDITOR)))
                        createAddQuestionButton(vl, group);
                }
                group = faq.getFaqgroup();
                firstGroup = false;
                vl = new MVerticalLayout();
                MVerticalLayout scrollLayout = new MVerticalLayout().withHeight(600, Unit.PIXELS).withStyleName("v-scrollable").withComponent(vl);
                accFaq.addTab(new MVerticalLayout(scrollLayout), faq.getFaqgroup());
            }
            LabelButton question = createLabelButton(faq, faqRepository.findByOrderByFaqgroupAscTitleAsc().stream().map(Faq::getFaqgroup).distinct().collect(Collectors.toList()));
            questionMap.put(faq.getContent(), question);
            Objects.requireNonNull(vl).addComponent(question);
        }
        if(user.getRoleList().stream().anyMatch(role -> role.getRole().equals(RoleType.EDITOR)))
            createAddQuestionButton(Objects.requireNonNull(vl), group);

        searchDesign.getTxtSearchField().setVisible(true);
        searchDesign.getTxtSearchField().addValueChangeListener(event -> {
            for (String key : questionMap.keySet()) {
                if(event.getValue().isEmpty()) questionMap.get(key).setVisible(true);
                if(Base64.decode(key).toLowerCase().contains(event.getValue().toLowerCase())) questionMap.get(key).setVisible(true);
                else questionMap.get(key).setVisible(false);
            }

        });

        this.addComponent(card);
        return this;

    }

    private LabelButton createLabelButton(Faq faq, List<String> groups) {
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        return new LabelButton(faq.getTitle(), labelClickEvent -> {
            Window window = new Window(faq.getTitle());
            window.setModal(true);
            window.setWidth(600, Unit.PIXELS);
            ComboBox<String> cbGroup = new ComboBox<>("Group: ", groups);
            cbGroup.setSelectedItem(faq.getFaqgroup());
            cbGroup.setEmptySelectionAllowed(false);
            cbGroup.setWidth(100, Unit.PERCENTAGE);
            cbGroup.setVisible(false);
            TextField txtTitle = new MTextField("Title:").withFullWidth().withVisible(false).withValue(faq.getTitle());
            Label lblDescription = new MLabel(new String(getDecoder().decode(faq.getContent()), StandardCharsets.UTF_8)).withFullWidth().withContentMode(ContentMode.HTML);
            RichTextArea txtDescription = new RichTextArea();
            txtDescription.setValue(new String(getDecoder().decode(faq.getContent()), StandardCharsets.UTF_8));
            txtDescription.setVisible(false);
            txtDescription.setWidth(100, Unit.PERCENTAGE);
            txtDescription.setHeight(500, Unit.PIXELS);
            final Button btnSave = new MButton("Save", event -> {
                faq.setTitle(txtTitle.getValue());
                faq.setFaqgroup(cbGroup.getValue());
                faq.setContent(getEncoder().encodeToString(txtDescription.getValue().getBytes(StandardCharsets.UTF_8)));
                faqRepository.save(faq);
                window.close();
                init();
            }).withVisible(false);
            final Button btnEdit = new MButton("Edit").withListener(event -> {
                lblDescription.setVisible(false);
                event.getButton().setVisible(false);
                cbGroup.setVisible(true);
                txtTitle.setVisible(true);
                txtDescription.setVisible(true);
                btnSave.setVisible(true);
            });
            final Button btnDelete = new MButton("Delete").withListener(event -> {
                faqRepository.delete(faq.getUuid());
                window.close();
                init();
            });

            if(user.getRoleList().stream().noneMatch(role -> role.getRole().equals(RoleType.EDITOR))) {
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
                btnSave.setVisible(false);
            }

            window.setContent(new MVerticalLayout(cbGroup, txtTitle, txtDescription, lblDescription, btnSave, btnEdit, btnDelete).withFullWidth());
            UI.getCurrent().addWindow(window);
        });
    }

    private void createAddQuestionButton(VerticalLayout vl, String group) {
        vl.addComponent(new MButton("Add Question", event -> {
            Window addQuestionWindow = new Window("Add question");
            addQuestionWindow.setModal(true);
            addQuestionWindow.setWidth(600, Unit.PIXELS);
            RichTextArea description = new RichTextArea("Description");
            description.setWidth(100, Unit.PERCENTAGE);
            description.setHeight(500, Unit.PIXELS);
            MTextField title = new MTextField("Title").withFullWidth();
            addQuestionWindow.setContent(new MFormLayout(title, description, new MButton("Add", event1 -> {
                Faq faq1 = new Faq();
                faq1.setFaqgroup(group);
                faq1.setTitle(title.getValue());
                faq1.setContent(getEncoder().encodeToString(description.getValue().getBytes(StandardCharsets.UTF_8)));
                faqRepository.save(faq1);
                addQuestionWindow.close();
                init();
            })));
            UI.getCurrent().addWindow(addQuestionWindow);
        }));
    }
}
