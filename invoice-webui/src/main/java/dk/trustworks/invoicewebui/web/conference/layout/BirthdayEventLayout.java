package dk.trustworks.invoicewebui.web.conference.layout;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.services.EmailSender;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;

import java.util.regex.Pattern;

@SpringComponent
@SpringUI
public class BirthdayEventLayout {

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Value("${motherSlackBotToken}")
    private String slackToken;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSender emailSender;

    private SlackWebApiClient motherWebApiClient;

    public BirthdayEventLayout() {

    }

    public Component init() {
        motherWebApiClient = SlackClientFactory.createWebApiClient(slackToken);

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

        ImageCardDesign cardDesign = new ImageCardDesign();

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

                    //Notification.show("NU HAR JEG SENDT EN MAIL...", Notification.Type.HUMANIZED_MESSAGE);

                    cardDesign.getVlContent().removeComponent(cardResponsiveLayout);
                    cardDesign.getVlContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
                    cardDesign.getVlContent().addComponent(new MLabel(""));
                    if(birthdayFormData.getBirthdayApplicationType().isGoing()) {
                        cardDesign.getVlContent().addComponent(new MLabel(
                                "<h3>Bekr&aelig;ftelse p&aring; din tilmelding</h3>\n" +
                                        "<p>Du har nu tilmeldt dig Trustworks 5 &aring;rs f&oslash;dselsdag.</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p><strong>Tid og sted<br /></strong>D. 5. april 2019 fra 15.00-20.00</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p><strong>Adresse:<br /></strong>Amagertorv 29a, 3. sal<br />1160 K&oslash;benhavn K, Denmark</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p>Vi ser frem til at fejre dagen sammen med dig.</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p>Mange hilsner fra</p>\n" +
                                        "<p>Trustworks</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<br /><br /><br/>" +
                                        "<p><i>Ved tilmelding bliver du bedt om at oplyse navn og e-mail. Informationerne bruger vi, s&aring; vi ved, hvem der deltager i receptionen, til navneskilte, og s&aring; vi kan kontakte Jer, hvis der skulle ske &aelig;ndringer til arrangementet. Vi opbevarer informationer indtil arrangementet er afholdt, hvorefter vi sletter informationerne igen.</i></p>"
                        ).withContentMode(ContentMode.HTML).withWidth(90, Sizeable.Unit.PERCENTAGE));
                    } else {
                        cardDesign.getVlContent().addComponent(new MLabel(
                                "<h3>Bekr&aelig;ftelse p&aring; din afmelding</h3>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p>Vi har nu noteret os, at du ikke deltager i Trustworks f&oslash;dselsdag.</p>\n" +
                                        "<p>Det er vi selvf&oslash;lgelig kede af.</p>\n" +
                                        "<p>Vi h&aring;ber, at du har mulighed for at deltage en anden gang.</p>\n" +
                                        "<p>&nbsp;</p>\n" +
                                        "<p>Mange hilsner fra</p>\n" +
                                        "<p>Trustworks</p>"
                        ).withContentMode(ContentMode.HTML).withWidth(90, Sizeable.Unit.PERCENTAGE));
                    }
                    cardDesign.getVlContent().addComponent(new MLabel(""));

                    emailSender.sendBirthdayInvitation(birthdayFormData.getEmail(), birthdayFormData.getName(), birthdayFormData.getBirthdayApplicationType().isGoing());

                    ChatPostMessageMethod textMessage1 = new ChatPostMessageMethod("@hans", birthdayFormData.getName() + " har " + (birthdayFormData.getBirthdayApplicationType().isGoing()?"tilmeldt":"afmeldt") + " sig med " + birthdayFormData.getEmail());
                    textMessage1.setAs_user(true);
                    motherWebApiClient.postMessage(textMessage1);

                    ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@gisla", birthdayFormData.getName() + " har " + (birthdayFormData.getBirthdayApplicationType().isGoing()?"tilmeldt":"afmeldt") + " sig med " + birthdayFormData.getEmail());
                    textMessage3.setAs_user(true);
                    motherWebApiClient.postMessage(textMessage3);

                    ChatPostMessageMethod textMessage4 = new ChatPostMessageMethod("UG170B2DN", birthdayFormData.getName() + " har " + (birthdayFormData.getBirthdayApplicationType().isGoing()?"tilmeldt":"afmeldt") + " sig med " + birthdayFormData.getEmail());
                    textMessage4.setAs_user(true);
                    motherWebApiClient.postMessage(textMessage4);

                    ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("UC4HHBRQW", birthdayFormData.getName() + " har " + (birthdayFormData.getBirthdayApplicationType().isGoing()?"tilmeldt":"afmeldt") + " sig med " + birthdayFormData.getEmail());
                    textMessage2.setAs_user(true);
                    motherWebApiClient.postMessage(textMessage2);

                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Fejl i indtastede felter", Notification.Type.ERROR_MESSAGE);
            }
        }).withFullWidth();

        Label gdpr = new MLabel("Ved tilmelding bliver du bedt om at oplyse navn og e-mail. Informationerne bruger vi, så vi ved, hvem der deltager i receptionen, til navneskilte, og så vi kan kontakte Jer, hvis der skulle ske ændringer til arrangementet. Vi opbevarer informationer indtil arrangementet er afholdt, hvorefter vi sletter informationerne igen.").withStyleName("small", "align-center").withWidth(90, Sizeable.Unit.PERCENTAGE);

        cardDesign.getImgTop().setSource(new ThemeResource("images/cards/birthday4.jpg"));
        cardDesign.getVlContent().addComponent(cardResponsiveLayout);
        ResponsiveRow mainRow = mainResponsiveLayout.addRow();
        mainRow.addColumn().withDisplayRules(0, 0, 3, 4).withComponent(new Label());
        mainRow.addColumn().withDisplayRules(0, 0, 6, 4).withComponent(cardDesign);
        mainRow.addColumn().withDisplayRules(0, 0, 3, 4).withComponent(new Label());
        mainRow.addColumn().withDisplayRules(0, 0, 3, 4).withComponent(new Label());
        Image logo = new Image(null, new ThemeResource("images/logo.png"));
        logo.setWidth(50, Sizeable.Unit.PERCENTAGE);
        mainRow.addColumn().withDisplayRules(0, 0, 6, 4).withComponent(logo, ResponsiveColumn.ColumnComponentAlignment.RIGHT);

        createFormItemColumn(cardResponsiveLayout, headline1);
        createFormItemColumn(cardResponsiveLayout, headline2);
        createFormItemColumn(cardResponsiveLayout, email);
        createFormItemColumn(cardResponsiveLayout, fuldeNavn);
        createFormItemColumn(cardResponsiveLayout, buttonGroup);
        //createFormItemColumn(cardResponsiveLayout, afmelding);
        createFormItemColumn(cardResponsiveLayout, send);
        createFormItemColumn(cardResponsiveLayout, new Label());
        createFormItemColumn(cardResponsiveLayout, new Label());
        createFormItemColumn(cardResponsiveLayout, gdpr);
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