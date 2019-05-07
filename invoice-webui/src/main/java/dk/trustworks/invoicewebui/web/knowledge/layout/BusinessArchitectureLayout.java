package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.SideBannerDesign;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

@SpringComponent
@SpringUI
public class BusinessArchitectureLayout extends VerticalLayout {

    private ResponsiveLayout mainLayout;
    private ResponsiveRow gridRow;

    public BusinessArchitectureLayout init() {

        this.removeAllComponents();

        mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        Box mainBox = new Box();
        mainBox.getCardHolder().setWidthUndefined();
        mainBox.getContent().setWidthUndefined();
        mainBox.getContent().addComponent(mainLayout);
        mainBox.getContent().setMargin(false);
        this.addComponent(mainBox);

        gridRow = mainLayout.addRow();

        VerticalLayout firstColumn = createArchitectureColumn2("-", "bg-grey", "");
        firstColumn.addComponents(
                createVerticalHeadline("Konceptuel", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Logisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                createVerticalHeadline("Fysisk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));

        VerticalLayout taskColumn = createArchitectureColumn2("Opgaver", "blue-bg", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        VerticalLayout informationColumn = createArchitectureColumn2("Information", "bg-secondary-1-2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        VerticalLayout applicationColumn = createArchitectureColumn2("Applikation", "bg-secondary-2-0", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        VerticalLayout strategyColumn = createArchitectureColumn2("Strategi", "yellow", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        VerticalLayout controlColumn = createArchitectureColumn2("Styring", "turquoise", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        createRows(taskColumn, "opgaver",
                "Forretningsstruktur",
                "<ul style='list-style-type:square;'><li>Opgave-/Servicekatalog</li>" +
                "<li>Domænemodel</li>" +
                "<li>Proceslandskab</li></ul>",

                "Processer",
                "- Aktører/roller<br/>" +
                "- Procesmodel<br/>" +
                "- Brugerrejse<br/>" +
                "- Servicemodel<br/>" +
                "- Use case / user story",

                "Arbejdstilrettelæggelse",
                "- Arbejdsgang / -beskrivelse");

        createRows(informationColumn, "information",
                "Forretningsobjekter og begreber",
                "- Centrale forretningsobjekter<br/>" +
                "- Begrebsliste / model",

                "Logiske datamodeller",
                "- Informationsmodel<br/>" +
                        "- Logisk datamodel<br/>" +
                        "- Masterdata<br/>" +
                        "- Datakvalitet<br/>",

                "Fysiske datamodeller",
                "- Datasæt<br/>" +
                                "- Dataudvekslings-format");

        createRows(applicationColumn,"applikation",
                "Applikationsstruktur og integrationsmønstre",
                "- Systemlandskab / kontekstdiagram",

                "Applikationslandskab og integrationer",
                "- Applikationslandskab / +integration<br/>" +
                        "- Applikationer mappet til forretning<br/>" +
                        "- Applikationer mappet til information",

                "Applikationsdesign og konfiguration",
                "- Applikationsdesign<br/>" +
                        "- Løsningskomponent<br/>" +
                        "- Snitfladebeskrivelser<br/>" +
                        "- Testscenarier");


        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Opgave-/Servicekatalog"));
            box.getContent().setHeight(150, Unit.PIXELS);
            strategyColumn.addComponent(box);
        }

        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Opgave-/Servicekatalog"));
            box.getContent().setHeight(150, Unit.PIXELS);
            controlColumn.addComponent(box);
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

    private void createRows(VerticalLayout column, String type, String title1, String content1, String title2, String content2, String title3, String content3) {
        column.addComponent(createCard(type, 1, title1, content1));
        column.addComponent(createCard(type, 2, title2, content2));
        column.addComponent(createCard(type, 3, title3, content3));
    }

    private ImageCardDesign createCard(String type, int i, String title, String content) {
        ImageCardDesign box = new ImageCardDesign();
        box.addStyleName("semi-white-bg");
        box.getImgTop().setSource(new ThemeResource("images/cards/architecture/"+type+"-"+i+".png"));
        box.getVlContent().addComponent(
                new MVerticalLayout(
                        new MLabel(title)
                                .withFullWidth()
                                .withStyleName("small bold"),
                        new MLabel(content)
                                .withContentMode(ContentMode.HTML)
                                .withFullWidth()
                                .withStyleName("tiny bold")
                )
        );
        box.getVlContent().setHeight(175, Unit.PIXELS);
        return box;
    }

    private VerticalLayout createArchitectureColumn2(String opgaver, String color, String description) {
        MVerticalLayout column = new MVerticalLayout(
                new MLabel(opgaver).withFullWidth().withStyleName("align-center large bold")
                //new MLabel(description).withFullWidth().withStyleName("align-center small")
        ).withWidth(100, Unit.PERCENTAGE).withMargin(false);

        gridRow.addColumn()
                .withDisplayRules(12, 12, 2, 2)
                .withStyleName(color)
                .withComponent(column);
        return column;
    }

}
