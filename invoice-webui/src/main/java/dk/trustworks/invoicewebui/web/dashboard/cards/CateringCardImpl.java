package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.EmailSender;
import dk.trustworks.invoicewebui.web.model.CateringEntry;

import java.util.List;

/**
 * Created by hans on 11/08/2017.
 */
public class CateringCardImpl extends CateringCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;
    private Binder<CateringEntry> binder = new Binder<>();


    public CateringCardImpl(List<User> users, EmailSender emailSender, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        getImgTop().setSource(new ThemeResource("images/cards/coffee-snacks.jpg"));
        getImgTop().setSizeFull();

        getContact().setItems(users);
        getContact().setItemCaptionGenerator(item -> item.getUsername());

        binder.bind(getMeetingName(), CateringEntry::getName, CateringEntry::setName);
        binder.bind(getContact(), CateringEntry::getContact, CateringEntry::setContact);
        binder.bind(getStart(), CateringEntry::getStart, CateringEntry::setStart);
        binder.bind(getEnd(), CateringEntry::getEnd, CateringEntry::setEnd);
        binder.forField(getPeople()).withConverter(new StringToIntegerConverter("Must enter a number"))
                .bind(CateringEntry::getPeople, CateringEntry::setPeople);
        binder.bind(getOrderTypes(), CateringEntry::getOrderTypes, CateringEntry::setOrderTypes);
        binder.bind(getDetails(), CateringEntry::getDetails, CateringEntry::setDetails);
        binder.bind(getQuality(), CateringEntry::getQuality, CateringEntry::setQuality);
        binder.bind(getAccount(), CateringEntry::getAccount, CateringEntry::setAccount);

        getBtnOrder().addClickListener(event -> {
            try {
                CateringEntry cateringEntry = new CateringEntry();
                binder.writeBean(cateringEntry);
                emailSender.sendCateringOrder(cateringEntry);
                binder.readBean(new CateringEntry());
            } catch (ValidationException e) {
                Notification.show("Person could not be saved, please check error messages for each field.");
            }
        });
    }

    public void init() {
        binder.readBean(new CateringEntry());
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }

}
