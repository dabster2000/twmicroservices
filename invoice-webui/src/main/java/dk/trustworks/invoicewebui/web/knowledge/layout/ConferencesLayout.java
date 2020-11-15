package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.LocalDateRenderer;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringComponent
@SpringUI
public class ConferencesLayout extends VerticalLayout {

    @Autowired
    private CKOExpenseRepository ckoExpenseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    private static Double getR(ConferenceGridItem conferenceGridItem) {
        return conferenceGridItem.rating;
    }

    public ConferencesLayout init() {
        this.removeAllComponents();

        MButton backButton = new MButton("Back").withVisible(false);
        this.addComponent(backButton);

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        this.addComponent(new BoxImpl().instance(mainLayout));

        ResponsiveRow detailViewRow = mainLayout.addRow();
        ResponsiveRow masterViewRow = mainLayout.addRow();
        ResponsiveColumn masterViewColumn = masterViewRow.addColumn().withDisplayRules(12, 12, 12, 12);

        Grid<ConferenceGridItem> grid = createGrid();

        backButton.withListener(event -> {
            masterViewRow.setVisible(true);
            detailViewRow.setVisible(false);
            backButton.setVisible(false);
            detailViewRow.removeAllComponents();
        });

        grid.addItemClickListener(event -> {
            if(!event.getMouseEventDetails().isDoubleClick()) return;
            masterViewRow.setVisible(false);
            detailViewRow.setVisible(true);
            backButton.setVisible(true);
            createDetailRow(detailViewRow, event.getItem());
        });

        masterViewColumn.withComponent(grid);

        return this;
    }

    private void createDetailRow(ResponsiveRow detailViewRow, ConferenceGridItem ckoExpense) {
        List<CKOExpense> similarCkoExpenses = getSimilarCkoExpenses(ckoExpense.name);

        // Get day range
        double minDays = 0;
        double maxDays = 0;
        for (CKOExpense similarCkoExpense : similarCkoExpenses) {
            if(minDays==0) minDays = similarCkoExpense.getDays();
            if(maxDays==0) maxDays = similarCkoExpense.getDays();
            if(similarCkoExpense.getDays()<minDays) minDays = similarCkoExpense.getDays();
            if(similarCkoExpense.getDays()>maxDays) maxDays = similarCkoExpense.getDays();
        }
        MLabel lblDays = new MLabel(minDays == maxDays ? ("Length: " + minDays + " days") : ("Length: From " + minDays + " - " + maxDays + " days"));

        // Get price range
        double minPrice = 0;
        double maxPrice = 0;
        for (CKOExpense similarCkoExpense : similarCkoExpenses) {
            if(minPrice==0) minPrice = similarCkoExpense.getPrice();
            if(maxPrice==0) maxPrice = similarCkoExpense.getPrice();
            if(similarCkoExpense.getPrice()<minPrice) minPrice = similarCkoExpense.getPrice();
            if(similarCkoExpense.getPrice()>maxPrice) maxPrice = similarCkoExpense.getPrice();
        }
        MLabel lblPrice = new MLabel(minPrice == maxPrice ? ("Price: " + minPrice + " kr") : ("Price: From " + minPrice + " - " + maxPrice + " kr"));

        Image icon = new Image("", new ThemeResource("images/icons/" + ckoExpense.getType().name().toLowerCase() + "-icon.png"));
        icon.setWidth(100, PERCENTAGE);
        icon.setHeight(100, PERCENTAGE);
        detailViewRow.addColumn().withDisplayRules(12,12,2,2).withComponent(icon).setAlignment(ResponsiveColumn.ColumnComponentAlignment.CENTER);

        ResponsiveColumn nameColumn = detailViewRow.addColumn().withDisplayRules(12, 12, 8, 8);
        nameColumn.withComponent(
                new MVerticalLayout(
                        new MLabel(ckoExpense.getName()).withStyleName("huge"),
                        lblDays,
                        lblPrice)
                        .withFullWidth()
        );

        detailViewRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel("Participants:").withStyleName("bold"));

        similarCkoExpenses.forEach(similarExpense ->  createConsultantDetailsColumn(detailViewRow, similarExpense));
    }

    private List<CKOExpense> getSimilarCkoExpenses(String description) {
        return ckoExpenseRepository.findByDescription(description);
    }

    private void createConsultantDetailsColumn(ResponsiveRow detailViewRow, CKOExpense ckoExpense) {
        ResponsiveLayout expenseItemLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        BoxImpl expenseItemBox = new BoxImpl().instance(expenseItemLayout);
        detailViewRow.addColumn().withDisplayRules(12,12,12,12).withComponent(expenseItemBox);

        // ROW 1
        ResponsiveRow expenseItemRow = expenseItemLayout.addRow();

        // COLUMN 1.1
        ResponsiveColumn leftColumn = expenseItemRow.addColumn().withDisplayRules(12, 12, 2, 2);
        ResponsiveLayout leftContentLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        leftColumn.withComponent(leftContentLayout);

        // COLUMN 2.1
        Image icon2 = photoService.getRoundImage(ckoExpense.getUseruuid(), false, 100, PIXELS);
        leftContentLayout.addRow().addColumn().withComponent(icon2).setAlignment(ResponsiveColumn.ColumnComponentAlignment.CENTER);

        // COLUMN 1.2
        ResponsiveColumn rightContentColumn = expenseItemRow.addColumn().withDisplayRules(12, 12, 10, 10);
        ResponsiveLayout rightContentLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        rightContentColumn.withComponent(rightContentLayout);

        // ROW 2
        ResponsiveRow detailRow = rightContentLayout.addRow();

        // COLUMNS 2
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel("Date:").withStyleName("bold"));
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel("Rating:").withStyleName("bold"));
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel("Purpose: ").withStyleName("bold"));
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel("Price: ").withStyleName("bold"));

        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel(DateUtils.stringIt(ckoExpense.getEventdate(), "dd. MMM yyyy")));
        if(ckoExpense.getRating()>0)
            detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(getRatingStars(ckoExpense.getRating()));
        else
            detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel("no rating"));
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel(ckoExpense.getPurpose().getCaption()));
        detailRow.addColumn().withDisplayRules(12,12,3,3).withComponent(new MLabel(ckoExpense.getPrice()+" kr"));
        if(ckoExpense.getRating_comment()!=null && !ckoExpense.getRating_comment().isEmpty()) {
            detailRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel("Review:").withStyleName("bold"));
            detailRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel(ckoExpense.getRating_comment()));
        }
    }

    private Grid<ConferenceGridItem> createGrid() {
        List<CKOExpense> ckoExpenses = ckoExpenseRepository.findAll().stream().filter(ckoExpense -> ckoExpense.getStatus().equals(CKOExpenseStatus.COMPLETED) &&
                (ckoExpense.getType().equals(CKOExpenseType.CONFERENCE) || ckoExpense.getType().equals(CKOExpenseType.COURSE))).collect(Collectors.toList());

        Set<String> strings = ckoExpenses.stream().map(CKOExpense::getDescription).collect(Collectors.toSet());

        List<ConferenceGridItem> gridItemList = new ArrayList<>();
        strings.forEach(s -> {
            List<CKOExpense> collect = ckoExpenses.stream().filter(ckoExpense -> ckoExpense.getDescription().equals(s)).collect(Collectors.toList());
            double average = collect.stream().filter(ckoExpense -> ckoExpense.getRating()>0).mapToDouble(CKOExpense::getRating).average().orElse(0.0);
            ConferenceGridItem conferenceGridItem = new ConferenceGridItem(collect.get(0).getDescription(), collect.get(0).getType(), collect.get(0).getCertification(), average, collect.stream().map(CKOExpense::getEventdate).max(LocalDate::compareTo).get());
            gridItemList.add(conferenceGridItem);
        });

        Grid<ConferenceGridItem> grid = new Grid<>("", gridItemList.stream().sorted(Comparator.comparing(ConferenceGridItem::getName)).collect(Collectors.toList()));
        grid.setWidth(100, PERCENTAGE);


        Grid.Column<ConferenceGridItem, String> itemNameGridCol = grid.addColumn(ConferenceGridItem::getName);
        itemNameGridCol.setCaption("Name");

        Grid.Column<ConferenceGridItem, String> itemTypeGridCol = grid.addColumn(ckoExpense -> ckoExpense.getType().getCaption());
        itemTypeGridCol.setCaption("Type");

        //Grid.Column<ConferenceGridItem, Integer> itemCertificationGridCol = grid.addColumn(ConferenceGridItem::getCertification);
        //itemCertificationGridCol.setCaption("Certification");
        grid.addComponentColumn(this::getCertificationImage).setStyleGenerator(item -> "center-label").setCaption("Certification").setSortable(false);

        grid.addComponentColumn(this::getRatingStars).setCaption("Rating");

        Grid.Column<ConferenceGridItem, LocalDate> itemDateGridCol = grid.addColumn(ConferenceGridItem::getDate, new LocalDateRenderer("dd. MMM yyyy"));
        itemDateGridCol.setCaption("Date");
        return grid;
    }

    private Image getCertificationImage(ConferenceGridItem conferenceGridItem) {
        Image image = new Image(null, new ThemeResource("images/icons/trustworks_icon_viden.svg"));
        image.setHeight(20, PIXELS);
        return conferenceGridItem.getCertification()==1?image:new Image();
    }

    private RatingStars getRatingStars(ConferenceGridItem conferenceGridItem) {
        RatingStars ratingStars = new RatingStars();
        ratingStars.setMaxValue(5);
        ratingStars.setValue(conferenceGridItem.getRating());
        ratingStars.setEnabled(false);
        return ratingStars;
    }

    private RatingStars getRatingStars(double rating) {
        RatingStars ratingStars = new RatingStars();
        ratingStars.setMaxValue(5);
        ratingStars.setValue(rating);
        ratingStars.setEnabled(false);
        return ratingStars;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ConferenceGridItem {
    String name;
    CKOExpenseType type;
    int certification;
    double rating;
    LocalDate date;
}