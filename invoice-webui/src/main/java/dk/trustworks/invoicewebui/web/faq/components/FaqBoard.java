package dk.trustworks.invoicewebui.web.faq.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Faq;
import dk.trustworks.invoicewebui.repositories.FaqRepository;
import dk.trustworks.invoicewebui.web.common.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.labelbutton.LabelButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

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
        for (Faq faq : faqRepository.findByOrderByFaqgroup()) {
            if(!faq.getFaqgroup().equals(group)) {
                group = faq.getFaqgroup();
                column = row.addColumn().withDisplayRules(12, 6, 4, 4);
                vl = new MVerticalLayout();
                column.withComponent(new MVerticalLayout().withHeight(300, Unit.PIXELS).withStyleName("v-scrollable").withComponent(vl));
                vl.addComponent(new MLabel(faq.getFaqgroup()).withStyleName("h4").withFullWidth());
            }
            vl.addComponent(new LabelButton(faq.getTitle(), labelClickEvent -> {
                Notification.show("CLOCK", Notification.Type.HUMANIZED_MESSAGE);
            }));
        }

        this.addComponent(card);
        return this;

    }
}
