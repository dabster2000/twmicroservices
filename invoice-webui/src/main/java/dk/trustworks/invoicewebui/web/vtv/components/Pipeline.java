package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.ui.Grid;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.vtv.model.PipelineGridItem;

import java.util.ArrayList;

public class Pipeline extends Card {

    public Pipeline() {
        super();
        this.getLblTitle().setValue("Pipeline forecast");
        redraw();
    }

    private void redraw() {
        this.getContent().removeAllComponents();

        Grid<PipelineGridItem> grid = new Grid<>(PipelineGridItem.class);
        grid.getColumns().get(1).setStyleGenerator(item -> {
            switch (item.getCol1()) {
                case 1: return "yellow";
                case 2: return "orange";
                default: return "dark-green";
            }
        });
        this.getContent().addComponent(grid);

        ArrayList<PipelineGridItem> items = new ArrayList<>();
        items.add(new PipelineGridItem("test1", 1,2,3,4,5,6,7,8,9,10,11,12));
        items.add(new PipelineGridItem("test2", 12,11,10,9,8,7,6,5,4,3,2,1));

        grid.setItems(items);
    }

}

