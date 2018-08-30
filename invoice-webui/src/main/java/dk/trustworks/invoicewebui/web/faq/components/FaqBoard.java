package dk.trustworks.invoicewebui.web.faq.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Faq;
import dk.trustworks.invoicewebui.repositories.FaqRepository;
import dk.trustworks.invoicewebui.web.common.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.labelbutton.LabelButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.UnsupportedEncodingException;

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
        this.removeAllComponents();

        Card card = new Card();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        card.getContent().addComponent(responsiveLayout);
        ResponsiveRow row = responsiveLayout.addRow();

        String group = "";
        ResponsiveColumn column;
        VerticalLayout vl = null;
        boolean firstGroup = true;
        for (Faq faq : faqRepository.findByOrderByFaqgroup()) {
            if(!faq.getFaqgroup().equals(group)) {
                group = faq.getFaqgroup();
                if(!firstGroup) {
                    createAddQuestionButton(vl, group);
                }
                firstGroup = false;
                column = row.addColumn().withDisplayRules(12, 6, 4, 4);
                vl = new MVerticalLayout();
                column.withComponent(new MVerticalLayout().withHeight(300, Unit.PIXELS).withStyleName("v-scrollable").withComponent(vl));
                vl.addComponent(new MLabel(faq.getFaqgroup()).withStyleName("h4").withFullWidth());
            }
            vl.addComponent(createLabelButton(faq));
        }
        createAddQuestionButton(vl, group);

        this.addComponent(card);
        return this;

    }

    private LabelButton createLabelButton(Faq faq) {
        return new LabelButton(faq.getTitle(), labelClickEvent -> {
            Window window = new Window(faq.getTitle());
            window.setModal(true);
            window.setWidth(600, Unit.PIXELS);
            try {
                TextField txtTitle = new MTextField().withFullWidth().withVisible(false).withValue(faq.getTitle());
                Label lblDescription = new MLabel(new String(getDecoder().decode(faq.getContent()), "utf-8")).withFullWidth().withContentMode(ContentMode.HTML);
                RichTextArea txtDescription = new RichTextArea();
                txtDescription.setValue(new String(getDecoder().decode(faq.getContent()), "utf-8"));
                txtDescription.setVisible(false);
                txtDescription.setWidth(100, Unit.PERCENTAGE);
                txtDescription.setHeight(500, Unit.PIXELS);
                final Button btnSave = new MButton("Save", event -> {
                    faq.setTitle(txtTitle.getValue());
                    try {
                        faq.setContent(getEncoder().encodeToString(txtDescription.getValue().getBytes("utf-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    faqRepository.save(faq);
                    window.close();
                    init();
                }).withVisible(false);
                final Button btnEdit = new MButton("Edit").withListener(event -> {
                    lblDescription.setVisible(false);
                    event.getButton().setVisible(false);
                    txtTitle.setVisible(true);
                    txtDescription.setVisible(true);
                    btnSave.setVisible(true);
                });
                final Button btnDelete = new MButton("Delete").withListener(event -> {
                    faqRepository.delete(faq.getUuid());
                    window.close();
                    init();
                });

                window.setContent(new MVerticalLayout(txtTitle, txtDescription, lblDescription, btnSave, btnEdit, btnDelete).withFullWidth());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                try {
                    faq1.setContent(getEncoder().encodeToString(description.getValue().getBytes("utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                faqRepository.save(faq1);
                addQuestionWindow.close();
                init();
            })));
            UI.getCurrent().addWindow(addQuestionWindow);
        }));
    }
}
