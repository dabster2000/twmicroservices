package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.Ambition;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserAmbition;
import dk.trustworks.invoicewebui.repositories.AmbitionRepository;
import dk.trustworks.invoicewebui.repositories.UserAmbitionRepository;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.profile.components.UserAmbitionTableImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.alump.materialicons.MaterialIcons;

import java.util.List;

@Service
public class AmbitionSpiderChart {

    private final UserAmbitionTableImpl userAmbitionTableImpl;

    private final AmbitionRepository ambitionRepository;

    private final UserAmbitionRepository userAmbitionRepository;

    @Autowired
    public AmbitionSpiderChart(UserAmbitionTableImpl userAmbitionTableImpl, AmbitionRepository ambitionRepository, UserAmbitionRepository userAmbitionRepository) {
        this.userAmbitionTableImpl = userAmbitionTableImpl;
        this.ambitionRepository = ambitionRepository;
        this.userAmbitionRepository = userAmbitionRepository;
    }

    public Component getOrganisationChart(User user) {
        Chart chart = new Chart(ChartType.LINE);

        Configuration conf = chart.getConfiguration();
        conf.getChart().setPolar(true);
        conf.setTitle("");

        Pane pane = new Pane();
        pane.setSize(80, Sizeable.Unit.PERCENTAGE);
        conf.addPane(pane);

        XAxis axis = new XAxis();

        List<Ambition> ambitionList = ambitionRepository.findAmbitionByActiveIsTrue();
        for (Ambition ambition : ambitionList) {
            axis.addCategory(ambition.getName());
        }

        axis.setTickmarkPlacement(TickmarkPlacement.ON);
        axis.setLineWidth(0);

        YAxis yaxs = new YAxis();
        yaxs.setGridLineInterpolation("polygon");
        yaxs.setMin(0);
        yaxs.setLineWidth(0);
        conf.addxAxis(axis);
        conf.addyAxis(yaxs);

        conf.getTooltip().setShared(true);
        conf.getTooltip().setValuePrefix("$");

        conf.getLegend().setAlign(HorizontalAlign.RIGHT);
        conf.getLegend().setVerticalAlign(VerticalAlign.TOP);
        conf.getLegend().setY(100);
        conf.getLegend().setLayout(LayoutDirection.VERTICAL);

        List<UserAmbition> userAmbitionList = userAmbitionRepository.findByUser(user);

        ListSeries line1 = new ListSeries();

        for (UserAmbition userAmbition : userAmbitionList) {
            // TODO: Filtrér dem der er ikke er aktive
            line1.addData(userAmbition.getScore());
        }


        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setPointPlacement(PointPlacement.ON);
        line1.setPlotOptions(plotOptions);
        line1.setName("Allocated Budget");

        plotOptions = new PlotOptionsLine();
        plotOptions.setPointPlacement(PointPlacement.ON);
        //line2.setPlotOptions(plotOptions);
        //line2.setName("Actual Spending");

        //conf.setSeries(line1, line2);

        chart.drawChart(conf);

        Card card = new Card();
        card.getContent().addComponent(chart);
        card.getBtnAlt1().setVisible(true);
        card.getLblTitle().setValue("Competence Chart");
        card.getBtnAlt1().setCaption("");
        card.getBtnAlt1().setIcon(MaterialIcons.EDIT);
        card.getBtnAlt1().addClickListener(event -> {
            final Window window = new Window("Window");
            window.setWidth(600.0f, Sizeable.Unit.PIXELS);
            window.setModal(true);
            window.setCaption("Competence Table");
            //window.setHeight(300, Sizeable.Unit.PIXELS);
            window.setClosable(true);

            window.setContent(userAmbitionTableImpl.getUserAmbitionTable(user));

            UI.getCurrent().addWindow(window);
        });

        return card;
    }

}
