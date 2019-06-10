package dk.trustworks.invoicewebui.web.admin.components;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.common.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.HashMap;
import java.util.Map;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class TalentPassionResultImpl {

    @Autowired
    private PhotoService photoService;

    private Map<String, Image> userImages = new HashMap<>();

    private AbsoluteLayout boardLayout;

    private VerticalLayout descriptionLayout;

    private static final double[] performanceConverter = {0.0, 0.75, 1.5, 2.25, 3.0};
    private static final double[] potentialConverter = {0.0, 0.20, 0.4, 0.6, 0.8, 1.0, 1.33, 1.66, 2.0, 3.0};

    private static final String[] texts = {
            "This person is not contributing well to TW, neither are they displaying signs of potential to develop further. Advise is to deselect after further evaluation.",
            "This person is contributing well to the organisation. They need to be motivated and retained, to feel valued, offered the chance to develop in role.",
            "Good potential but underperforming. The immediate priority is to understand what is the cause of current under performance. They could be new to role, in the wrong role, or clashing with their Peers or Manager. The fact they are moderate potential means that Managers  should invest some time with this individual to either raise performance or find  a different position within TW or deselect.",
            "This person is delivering exceptional or highly effective performance, and they are valuable to TW. As role models of performance, they need to be respected, retained, valued and recognised. They may be key advisers or consultants – ensure time is invested in them to keep their performance levels high.",
            "Good potential for development. This person is delivering effective performance and demonstrates  the potential to develop  into broader responsibilities. They need to be stretched and developed into more demanding work, or offered opportunities to expand their current working environment.",
            "Emerging high potential, but currently under performing. The immediate priority is to understand what  is the cause  of under performance. They could be new to role, in the wrong role, or clashing with their peers or Manager. The fact they are high potential means that we would invest more time with this individual to either raise performance or find  a new position within TW.",
            "Good potential, urgent development. This person is delivering exceptional or highly effective performance and also has potential to develop into broader responsibilities. They should focus on up skilling and coaching others to free them up to take on more demanding projects or roles.",
            "High potential for development. This person is delivering effective performance, combined with the high potential to make a leap into much more challenging responsibilities. They need special attention and are best developed by working on their strengths at a different level or in a different area, rather than refining skills on the job.",
            "Fast track potential, urgent development. This person is delivering exceptional or highly effective performance. They also have high potential for taking a leap into much more challenging responsibilities. They need special attention and focus to move from their current work into work at a different level or in another area to broaden experience."
        };

    private final Table<Integer, Integer, Integer> scoringConverter = HashBasedTable.create();

    public TalentPassionResultImpl() {
        scoringConverter.put(0, 0, 1);
        scoringConverter.put(1, 0, 2);
        scoringConverter.put(2, 0, 4);
        scoringConverter.put(3, 0, 4);

        scoringConverter.put(0, 1, 3);
        scoringConverter.put(1, 1, 5);
        scoringConverter.put(2, 1, 7);
        scoringConverter.put(3, 1, 7);

        scoringConverter.put(0, 2, 6);
        scoringConverter.put(1, 2, 8);
        scoringConverter.put(2, 2, 9);
        scoringConverter.put(3, 2, 9);

        scoringConverter.put(0, 3, 6);
        scoringConverter.put(1, 3, 8);
        scoringConverter.put(2, 3, 9);
        scoringConverter.put(3, 3, 9);
    }

    public Component getResultBoard() {
        Box box = new Box();
        boardLayout = new AbsoluteLayout();
        boardLayout.setWidth(800, PIXELS);
        boardLayout.setHeight(700, PIXELS);
        Image image = new Image(null, new ThemeResource("images/talent-passion-matrix.png"));
        image.setWidth(800, PIXELS);
        image.setHeight(700, PIXELS);
        boardLayout.addComponent(image, "top: 0px; left: 0px");

        descriptionLayout = new MVerticalLayout().withMargin(true).withSpacing(true).withStyleName("very-light-grey").withFullWidth();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFullSize();
        ResponsiveRow row = responsiveLayout.addRow();

        row.addColumn().withComponent(boardLayout).withDisplayRules(12, 12, 8,9);
        row.addColumn().withComponent(descriptionLayout).withDisplayRules(12, 12, 4, 3).withVisibilityRules(false, false, true, true);
        box.getContent().addComponent(responsiveLayout);

        //box.getContent().addComponent(new MHorizontalLayout(boardLayout, descriptionLayout).withMargin(false).withSpacing(true).withFullWidth());
        return box;
    }

    public void showResultDescription() {
        System.out.println("TalentPassionResultImpl.showResultDescription");
        descriptionLayout.setVisible(true);
    }

    public void updateUser(User user, double perfomance, double potential) {
        System.out.println("perfomance = [" + perfomance + "], potential = [" + potential + "]");
        int xMargin = 187;
        int yMargin = 87;
        int fieldSize = 200;
        int spriteSize = 50;

        userImages.putIfAbsent(user.getUuid(), photoService.getRoundMemberImage(user, false, spriteSize, PIXELS));
        Image image = userImages.get(user.getUuid());
        image.addClickListener(event -> {
            if(!descriptionLayout.isVisible()) return;
            descriptionLayout.removeAllComponents();

            Integer scoreMajor = scoringConverter.get((int) (perfomance), (int) (potential));

            descriptionLayout.addComponent(new MLabel("").withFullWidth());
            descriptionLayout.addComponent(new MLabel("Name:",user.getFirstname()+" "+user.getLastname()).withFullWidth());
            descriptionLayout.addComponent(new MLabel("Score:", ""+(scoreMajor)).withFullWidth());
            descriptionLayout.addComponent(new MLabel("Recommendation:", texts[scoreMajor-1]).withFullWidth());
            descriptionLayout.addComponent(new MLabel("Abnormalities:", "").withFullWidth());

        });

        double x = ((potential * fieldSize) + xMargin);
        double y = ((perfomance * fieldSize) + yMargin);

        if(x < xMargin) x = xMargin;
        if(y < yMargin) y = yMargin;
        if(x > xMargin + 3 * fieldSize - spriteSize) x = xMargin + 3 * fieldSize - spriteSize;
        if(y > yMargin + 3 * fieldSize - spriteSize) y = yMargin + 3 * fieldSize - spriteSize;

        boardLayout.addComponent(image, "left: "+(x)+"px; bottom: "+(y)+"px");
    }

    public static double performanceConverter(int performance) {
        return performanceConverter[performance];
    }

    public static double potentialConverter(int potential) {
        return potentialConverter[potential];
    }
}
