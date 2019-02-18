package dk.trustworks.invoicewebui.web.conference.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;

import java.util.regex.Pattern;

@SpringComponent
@SpringUI
public class BirthdayEventLayout {

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public BirthdayEventLayout() {

    }

    public Component init() {
        ResponsiveLayout mainResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveLayout cardResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        cardResponsiveLayout.setSpacing();

        Binder<BirthdayFormData> binder = new Binder<>();
        binder.readBean(new BirthdayFormData());

        Label headline1 = new MLabel("Trustworks' 5 års fødselsdag").withStyleName("h3", "align-center").withFullWidth();
        Label headline2 = new MLabel("D. 5. april 2019 fra kl. 15.00-20.00").withStyleName("h5", "align-center").withFullWidth();

        TextField email = new MTextField().withPlaceholder("Skriv e-mail").withFullWidth();
        TextField fuldeNavn = new MTextField().withPlaceholder("Skriv fulde navn").withWidth(100, Sizeable.Unit.PERCENTAGE);
        RadioButtonGroup<BirthdayApplicationType> buttonGroup = new RadioButtonGroup<>();
        buttonGroup.setItemCaptionGenerator(BirthdayApplicationType::getText);
        buttonGroup.setSizeFull();
        BirthdayApplicationType defaultButton = new BirthdayApplicationType("Jeg vil meget gerne komme og fejre dagen med Jer", true);
        buttonGroup.setItems(
                defaultButton,
                new BirthdayApplicationType("Desværre, har jeg alligevel ikke mulighed for at deltage", false));
        buttonGroup.setSelectedItem(defaultButton);


        binder.forField(email).bind(BirthdayFormData::getEmail, BirthdayFormData::setEmail);
        binder.forField(fuldeNavn).bind(BirthdayFormData::getName, BirthdayFormData::setName);
        binder.forField(buttonGroup).bind(BirthdayFormData::getBirthdayApplicationType, BirthdayFormData::setBirthdayApplicationType);

        MButton send = new MButton("send", event -> {
            if(binder.isValid()) {
                BirthdayFormData birthdayFormData = new BirthdayFormData();
                try {
                    binder.writeBean(birthdayFormData);

                    Pattern pat = Pattern.compile(emailRegex);
                    if (birthdayFormData.getEmail() == null || birthdayFormData.getEmail().length() == 0) {
                        Notification.show("E-mail skal udfyldes", Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    if(!pat.matcher(birthdayFormData.getEmail()).matches()) {
                        Notification.show("E-mail skal udfyldes korrekt", Notification.Type.ERROR_MESSAGE);
                        return;
                    };
                    if (birthdayFormData.getName() == null || birthdayFormData.getName().length() == 0) {
                        Notification.show("Navn skal udfyldes", Notification.Type.ERROR_MESSAGE);
                        return;
                    }

                    Notification.show("NU HAR JEG SENDT EN MAIL...", Notification.Type.HUMANIZED_MESSAGE);
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Fejl i indtastede felter", Notification.Type.ERROR_MESSAGE);
            }
        }).withFullWidth();

        ImageCardDesign cardDesign = new ImageCardDesign();
        cardDesign.getImgTop().setSource(new ThemeResource("images/cards/birthday4.jpg"));
        cardDesign.getVlContent().addComponent(cardResponsiveLayout);
        ResponsiveRow mainRow = mainResponsiveLayout.addRow();
        mainRow.addColumn().withDisplayRules(0, 0, 3, 4).withComponent(new Label());
        mainRow.addColumn().withDisplayRules(0, 0, 6, 4).withComponent(cardDesign);

        createFormItemColumn(cardResponsiveLayout, headline1);
        createFormItemColumn(cardResponsiveLayout, headline2);
        createFormItemColumn(cardResponsiveLayout, email);
        createFormItemColumn(cardResponsiveLayout, fuldeNavn);
        createFormItemColumn(cardResponsiveLayout, buttonGroup);
        //createFormItemColumn(cardResponsiveLayout, afmelding);
        createFormItemColumn(cardResponsiveLayout, send);
        createFormItemColumn(cardResponsiveLayout, new Label());

        return mainResponsiveLayout;
    }

    private void createFormItemColumn(ResponsiveLayout responsiveLayout, Component component) {
        ResponsiveRow row = responsiveLayout.addRow().withHorizontalSpacing(ResponsiveRow.SpacingSize.NORMAL, true).withVerticalSpacing(ResponsiveRow.SpacingSize.NORMAL, true);
        row.addColumn().withDisplayRules(1, 1, 1, 1);
        row.addColumn()
                .withDisplayRules(10, 10, 10, 10)
                //.withOffset(ResponsiveLayout.DisplaySize.LG, 2)
                //.withOffset(ResponsiveLayout.DisplaySize.MD, 2)
                //.withOffset(ResponsiveLayout.DisplaySize.SM, 4)
                //.withOffset(ResponsiveLayout.DisplaySize.XS, 4)
                .withComponent(component, ResponsiveColumn.ColumnComponentAlignment.CENTER);
    }
}

class BirthdayApplicationType {

    private String text;
    private boolean isGoing;

    public BirthdayApplicationType(String text, boolean isGoing) {
        this.text = text;
        this.isGoing = isGoing;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }
}

class BirthdayFormData {

    private String name;
    private String email;
    private BirthdayApplicationType birthdayApplicationType;

    public BirthdayFormData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BirthdayApplicationType getBirthdayApplicationType() {
        return birthdayApplicationType;
    }

    public void setBirthdayApplicationType(BirthdayApplicationType birthdayApplicationType) {
        this.birthdayApplicationType = birthdayApplicationType;
    }
}