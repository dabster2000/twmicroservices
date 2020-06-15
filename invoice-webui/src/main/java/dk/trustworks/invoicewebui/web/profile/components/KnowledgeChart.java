package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.AmbitionCategory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.UserAmbitionDTO;
import dk.trustworks.invoicewebui.repositories.AmbitionCategoryRepository;
import dk.trustworks.invoicewebui.repositories.UserAmbitionDTORepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeChart {

    private final AmbitionCategoryRepository ambitionCategoryRepository;
    private final UserAmbitionDTORepository userAmbitionDTORepository;

    public KnowledgeChart(AmbitionCategoryRepository ambitionCategoryRepository, UserAmbitionDTORepository userAmbitionDTORepository) {
        this.ambitionCategoryRepository = ambitionCategoryRepository;
        this.userAmbitionDTORepository = userAmbitionDTORepository;
    }

    public Component getChart(User user) {
        final Chart chart = new Chart(ChartType.COLUMN);
        chart.setId("chart");

        final Configuration conf = chart.getConfiguration();

        //conf.setTitle(user.getFirstname()+"'s complete set of skills");
        conf.setTitle("");
        conf.setSubTitle("Click the columns to view individual skills. Click again to view categories.");
        conf.getLegend().setEnabled(false);

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Total skill points");
        conf.addyAxis(y);

        PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y +' points'");

        conf.setPlotOptions(column);

        Tooltip tooltip = new Tooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
        tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f} points</b> of total<br/>");
        conf.setTooltip(tooltip);

        DataSeries series = new DataSeries();
        series.setName("Skill categories");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        series.setPlotOptions(plotOptionsColumn);

        List<UserAmbitionDTO> userAmbitionList = userAmbitionDTORepository.findUserAmbitionByUseruuidAndActiveTrue(user.getUuid());
        for (AmbitionCategory ambitionCategory : ambitionCategoryRepository.findByActiveTrue()) {
            double total = userAmbitionList.stream().filter(u -> u.getCategory().equals(ambitionCategory.getAmbitionCategoryType())).count() * 4;
            double score = userAmbitionList.stream().filter(u -> u.getCategory().equals(ambitionCategory.getAmbitionCategoryType())).mapToInt(UserAmbitionDTO::getScore).sum();

            DataSeriesItem item = new DataSeriesItem(ambitionCategory.getName(), Math.round((score / total) * 100.0));
            DataSeries drillSeries = new DataSeries(ambitionCategory.getName());
            drillSeries.setId(ambitionCategory.getAmbitionCategoryType());

            List<UserAmbitionDTO> collect = userAmbitionList.stream().filter(u -> u.getCategory().equals(ambitionCategory.getAmbitionCategoryType())).sorted(Comparator.comparing(UserAmbitionDTO::getName)).collect(Collectors.toList());
            String[] categories = collect.stream().map(UserAmbitionDTO::getName).toArray(String[]::new);
            Number[] ys = collect.stream().map(UserAmbitionDTO::getScore).toArray(Number[]::new);
            drillSeries.setData(categories, ys);
            series.addItemWithDrilldown(item, drillSeries);
        }

        conf.addSeries(series);

        return chart;
    }
}
