package dk.trustworks.invoicewebui.web.common;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class ImageListItem extends VerticalLayout {

    public ImageListItem withComponents(Resource image, String headline, String subtext) {
        HorizontalLayout baseLayout = new HorizontalLayout();

        Image rowImage = new Image(null, image);
        rowImage.setHeight(65, Sizeable.Unit.PIXELS);
        baseLayout.addComponent(rowImage);

        Label lblHeadline = new MLabel(headline).withFullWidth().withStyleName("bold small");
        Label lblSubtext = new MLabel(subtext).withFullWidth().withStyleName("tiny grey-font");
        MVerticalLayout rowTitles = new MVerticalLayout(lblHeadline, lblSubtext).withMargin(false).withComponentAlignment(lblHeadline, Alignment.BOTTOM_LEFT);
        baseLayout.addComponent(rowTitles);

        this.addComponent(baseLayout);

        return this;
    }

}
