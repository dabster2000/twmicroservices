package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.AmbitionCategory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.UserAmbitionDTO;
import dk.trustworks.invoicewebui.model.enums.AmbitionType;
import dk.trustworks.invoicewebui.repositories.UserAmbitionDTORepository;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.employee.components.parts.UserAmbitionTableImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.alump.materialicons.MaterialIcons;

import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.AmbitionType.*;

@Service
public class AmbitionSpiderChart {

    private final UserAmbitionTableImpl userAmbitionTableImpl;

    private final UserAmbitionDTORepository userAmbitionDTORepository;

    @Autowired
    public AmbitionSpiderChart(UserAmbitionTableImpl userAmbitionTableImpl, UserAmbitionDTORepository userAmbitionDTORepository) {
        this.userAmbitionTableImpl = userAmbitionTableImpl;
        this.userAmbitionDTORepository = userAmbitionDTORepository;
    }

    public Component getOrganisationChart(final User user, final AmbitionCategory ambitionCategory) {
        final Card card = new Card();
        card.getContent().addComponent(createChart(user, ambitionCategory));
        card.getBtnAlt1().setVisible(true);
        card.getLblTitle().setValue("Competence: "+ambitionCategory.getName());
        card.getBtnAlt1().setCaption("");
        card.getBtnAlt1().setIcon(MaterialIcons.EDIT);
        card.getBtnAlt1().addClickListener(event -> {
            final Window window = new Window("Edit "+ambitionCategory.getName());
            window.setWidth(600.0f, Sizeable.Unit.PIXELS);
            window.setModal(true);
            //window.setHeight(300, Sizeable.Unit.PIXELS);
            window.setClosable(true);
            window.addCloseListener(e -> {
                card.getContent().removeAllComponents();
                card.getContent().addComponent(createChart(user, ambitionCategory));
            });

            window.setContent(userAmbitionTableImpl.getUserAmbitionTable(user, ambitionCategory));

            UI.getCurrent().addWindow(window);
        });

        return card;
    }

    private Chart createChart(User user, AmbitionCategory ambitionCategory) {
        Chart chart = new Chart(ChartType.LINE);

        Configuration conf = chart.getConfiguration();
        conf.getChart().setPolar(true);
        conf.setTitle("");

        Pane pane = new Pane();
        pane.setSize(80, Sizeable.Unit.PERCENTAGE);
        conf.addPane(pane);

        XAxis axis = new XAxis();

        //List<Ambition> ambitionList = ambitionRepository.findAmbitionByActiveIsTrue();
        List<UserAmbitionDTO> userAmbitionList = userAmbitionDTORepository.findUserAmbitionByUseruuidAndCategoryAndActiveTrue(user.getUuid(), ambitionCategory.getAmbitionCategoryType());
        for (UserAmbitionDTO ambition : userAmbitionList) {
            axis.addCategory(ambition.getName());
        }

        axis.setTickmarkPlacement(TickmarkPlacement.ON);
        axis.setLineWidth(0);

        YAxis yaxs = new YAxis();
        yaxs.setGridLineInterpolation("polygon");
        yaxs.setMin(0);
        yaxs.setMax(4);
        yaxs.setLineWidth(0);
        conf.addxAxis(axis);
        conf.addyAxis(yaxs);

        conf.getTooltip().setShared(true);
        conf.getTooltip().setValuePrefix("");

        conf.getLegend().setEnabled(false);//.setAlign(HorizontalAlign.RIGHT);
        conf.getLegend().setVerticalAlign(VerticalAlign.TOP);
        conf.getLegend().setY(100);
        conf.getLegend().setLayout(LayoutDirection.VERTICAL);

        //List<UserAmbition> userAmbitionList = userAmbitionRepository.findByUser(user);

        ListSeries line1 = new ListSeries();
        ListSeries line2 = new ListSeries();

        for (UserAmbitionDTO userAmbition : userAmbitionList) {
            // TODO: Filtrér dem der er ikke er aktive
            line1.addData(userAmbition.getScore());
            AmbitionType ambition = AmbitionType.values()[userAmbition.getAmbition()];
            if(ambition == IMPROVE && userAmbition.getScore()==4) {
                line2.addData(userAmbition.getScore());
            } else if (ambition == IMPROVE && userAmbition.getScore()<4) {
                line2.addData(userAmbition.getScore()+1);
            } else if (ambition == STATUS_QUO) {
                line2.addData(userAmbition.getScore());
            } else if (ambition == SLACK && userAmbition.getScore() > 1) {
                line2.addData(userAmbition.getScore()-1);
            } else {
                line2.addData(userAmbition.getScore());
            }
        }

        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setColor(new SolidColor("#236F1F"));
        line1.setPlotOptions(plotOptions);
        line1.setName("Current Knowledge Level");

        plotOptions = new PlotOptionsLine();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setColor(new SolidColor("#bec4c8"));
        line2.setPlotOptions(plotOptions);
        line2.setName("Target Knowledge Level");

        conf.setSeries(line2, line1);

        chart.drawChart(conf);

        return chart;
    }

}
