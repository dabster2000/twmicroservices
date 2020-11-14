package dk.trustworks.invoicewebui.web.knowledge.components;

import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import lombok.*;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import java.util.*;

public class CertificationTable {

    private final CKOExpenseRepository ckoExpenseRepository;
    private final UserService userService;

    public CertificationTable(CKOExpenseRepository ckoExpenseRepository, UserService userService) {
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.userService = userService;
    }

    public VerticalLayout createCertificationsTable() {
        final boolean[] usernameIsRoot = {true};
        final TreeGrid<GridItem>[] gridItems = new TreeGrid[]{createGridItems(true)};
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponents(
                new MLabel("Consultant certifications").withStyleName("H2"),
                new MButton("Ordered by Consultant").withListener(event -> {
                    usernameIsRoot[0] = !usernameIsRoot[0];
                    verticalLayout.removeComponent(gridItems[0]);
                    gridItems[0] = createGridItems(usernameIsRoot[0]);
                    verticalLayout.addComponent(gridItems[0]);
                    event.getButton().setCaption(usernameIsRoot[0] ? "Ordered by Consultants" : "Ordered by Certification");
                }).withFullWidth(),
                gridItems[0]
        );
        return verticalLayout;
    }

    private TreeGrid<GridItem> createGridItems(boolean usernameIsRoot) {
        TreeGrid<GridItem> grid = new TreeGrid<>();
        Grid.Column<GridItem, String> textColumn = grid.addColumn(GridItem::getText);
        textColumn.setCaption(usernameIsRoot?"Consultant":"Certification");
        textColumn.setExpandRatio(1);
        Grid.Column<GridItem, String> dateColumn = grid.addColumn(GridItem::getDate);
        dateColumn.setCaption("Date");
        dateColumn.setWidth(150);
        grid.setSizeFull();

        Map<String, List<GridItem>> ckoExpenseMap = new HashMap<>();
        ckoExpenseRepository.findAll().stream().filter(ckoExpense -> ckoExpense.getCertified() == 1).forEach(ckoExpense -> {
            String username = userService.findByUUID(ckoExpense.getUseruuid(), true).getUsername();
            String description = ckoExpense.getDescription();
            GridItem gridItem = new GridItem(new ArrayList<>(), !usernameIsRoot?username:description, DateUtils.stringIt(ckoExpense.getEventdate()));
            ckoExpenseMap.putIfAbsent(usernameIsRoot?username:description, new ArrayList<>());
            ckoExpenseMap.get(usernameIsRoot?username:description).add(gridItem);
        });
        List<GridItem> gridItems = new ArrayList<>();
        ckoExpenseMap.keySet().forEach(key -> {
            gridItems.add(new GridItem(ckoExpenseMap.get(key), key, ""));
        });
        grid.setItems(gridItems, GridItem::getGridItems);

        return grid;
    }
}


@Data
@NoArgsConstructor
@RequiredArgsConstructor
class GridItem {
    private String id = UUID.randomUUID().toString();
    @NonNull private List<GridItem> gridItems;
    @NonNull private String text;
    @NonNull private String date;
}