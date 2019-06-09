package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.common.Box;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class TalentPassionResultImpl {

    @Autowired
    private PhotoService photoService;

    Map<String, Image> userImages = new HashMap<>();

    AbsoluteLayout layout;

    public TalentPassionResultImpl() {
    }

    public Component getInstance() {
        Box box = new Box();

        layout = new AbsoluteLayout();
        layout.setWidth(800, PIXELS);
        layout.setHeight(700, PIXELS);
        Image image = new Image(null, new ThemeResource("images/talent-passion-matrix.png"));
        image.setWidth(800, PIXELS);
        image.setHeight(700, PIXELS);
        layout.addComponent(image, "top: 0px; left: 0px");

        box.getContent().addComponent(layout);
        return box;
    }

    public void updateUser(User user, double perfomance, double potential) {
        int xMargin = 187;
        int yMargin = 87;
        int fieldSize = 200;
        int spriteSize = 50;

        userImages.putIfAbsent(user.getUuid(), photoService.getRoundMemberImage(user, false, spriteSize, PIXELS));
        Image image = userImages.get(user.getUuid());

        double x = ((potential * fieldSize) + xMargin) - spriteSize;
        double y = ((perfomance * fieldSize) + yMargin) - spriteSize;

        if(x < xMargin) x = xMargin;
        if(y < yMargin) y = yMargin;
        if(x > xMargin + 3 * fieldSize) x = xMargin + 3 * fieldSize;
        if(y > yMargin + 3 * fieldSize) y = yMargin + 3 * fieldSize;

        layout.addComponent(image, "left: "+(x)+"px; bottom: "+(y)+"px");
    }
}
