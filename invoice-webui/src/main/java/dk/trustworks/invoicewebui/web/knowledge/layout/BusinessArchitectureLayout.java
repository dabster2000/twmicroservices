package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.ArchitecturePerspectiveColumn;
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
        mainBox.getContent().addComponent(mainLayout);
        mainBox.getContent().setMargin(false);
        this.addComponent(mainBox);

        gridRow = mainLayout.addRow();

        /*
        ArchitecturePerspectiveColumn taskColumn = createArchitectureColumn("Opgaver", "dark-blue");
        ArchitecturePerspectiveColumn informationColumn = createArchitectureColumn("Information", "bg-secondary-1-2");
        ArchitecturePerspectiveColumn applicationColumn = createArchitectureColumn("Applikation", "bg-secondary-2-0");
        ArchitecturePerspectiveColumn strategyColumn = createArchitectureColumn("Strategi", "yellow");
        ArchitecturePerspectiveColumn controlColumn = createArchitectureColumn("Styring", "turquoise");
        */

        gridRow.addColumn()
                .withStyleName("bg-grey")
                .withDisplayRules(12, 12, 2, 2)
                .withComponent(
                        new MVerticalLayout(
                                new MLabel("Description").withFullWidth().withStyleName("large bold align-center"),
                                new MLabel("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                                        .withStyleName("small").withFullWidth()
                        )
        );
        VerticalLayout taskColumn = createArchitectureColumn2("Opgaver", "dark-blue");
        VerticalLayout informationColumn = createArchitectureColumn2("Information", "bg-secondary-1-2");
        VerticalLayout applicationColumn = createArchitectureColumn2("Applikation", "bg-secondary-2-0");
        VerticalLayout strategyColumn = createArchitectureColumn2("Strategi", "yellow");
        VerticalLayout controlColumn = createArchitectureColumn2("Styring", "turquoise");

        taskColumn.addComponent(createCard(1, "Forretningsstruktur"));
        taskColumn.addComponent(createCard(2, "Processer"));
        taskColumn.addComponent(createCard(3, "Arbejdstilrettelæggelse"));


        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Forretningsobjekter og begreber\n"));
            box.getContent().setHeight(150, Unit.PIXELS);
            //informationColumn.getContent().addComponent(box);
            informationColumn.addComponent(box);
        }

        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Opgave-/Servicekatalog"));
            box.getContent().setHeight(150, Unit.PIXELS);
            //applicationColumn.getContent().addComponent(box);
            applicationColumn.addComponent(box);
        }

        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Opgave-/Servicekatalog"));
            box.getContent().setHeight(150, Unit.PIXELS);
            //strategyColumn.getContent().addComponent(box);
            strategyColumn.addComponent(box);
        }

        for (int i = 0; i < 3; i++) {
            Box box = new Box();
            box.getContent().addComponent(new MLabel("Opgave-/Servicekatalog"));
            box.getContent().setHeight(150, Unit.PIXELS);
            //controlColumn.getContent().addComponent(box);
            controlColumn.addComponent(box);
        }

        return this;
    }

    private ImageCardDesign createCard(int i, String title) {
        ImageCardDesign box = new ImageCardDesign();
        box.getImgTop().setSource(new ThemeResource("images/cards/architecture/opgaver-"+i+".png"));
        box.getVlContent().addComponent(new VerticalLayout(new MLabel(title).withFullWidth().withStyleName("small bold")));
        box.getVlContent().setHeight(150, Unit.PIXELS);
        return box;
    }

    private ArchitecturePerspectiveColumn createArchitectureColumn(String opgaver, String color) {
        ArchitecturePerspectiveColumn architecturePerspectiveColumn = new ArchitecturePerspectiveColumn();
        architecturePerspectiveColumn.getLblHeading().setValue(opgaver);
        architecturePerspectiveColumn.getCardHolder().addStyleName(color);
        gridRow.addColumn()
                .withDisplayRules(12, 12, 2, 2)
                .withStyleName("color")
                .withComponent(architecturePerspectiveColumn);
        return architecturePerspectiveColumn;
    }

    private VerticalLayout createArchitectureColumn2(String opgaver, String color) {
        MVerticalLayout column = new MVerticalLayout(new MLabel(opgaver).withFullWidth().withStyleName("align-center large bold")).withWidth(100, Unit.PERCENTAGE);

        gridRow.addColumn()
                .withDisplayRules(12, 12, 2, 2)
                .withStyleName(color)
                .withComponent(column);
        return column;
    }

}
