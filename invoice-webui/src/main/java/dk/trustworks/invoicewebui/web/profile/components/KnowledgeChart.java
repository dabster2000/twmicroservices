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

        conf.setTitle(user.getFirstname()+"'s complete set of skills");
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

            //List<Ambition> ambitionList = ambitionRepository.findAmbitionByActiveIsTrueAndCategory(ambitionCategory.getAmbitionCategoryType()).stream().sorted(Comparator.comparing(Ambition::getName)).collect(Collectors.toList());
            //String[] categories = ambitionList.stream().map(Ambition::getName).toArray(String[]::new);

            List<UserAmbitionDTO> collect = userAmbitionList.stream().filter(u -> u.getCategory().equals(ambitionCategory.getAmbitionCategoryType())).sorted(Comparator.comparing(UserAmbitionDTO::getName)).collect(Collectors.toList());
            String[] categories = collect.stream().map(UserAmbitionDTO::getName).toArray(String[]::new);
            Number[] ys = collect.stream().map(UserAmbitionDTO::getScore).toArray(Number[]::new);
            drillSeries.setData(categories, ys);
            series.addItemWithDrilldown(item, drillSeries);
        }
/*

        DataSeriesItem item = new DataSeriesItem("MSIE", 55.11);
        DataSeries drillSeries = new DataSeries("MSIE versions");
        drillSeries.setId("MSIE");
        String[] categories = new String[] { "MSIE 6.0", "MSIE 7.0",
                "MSIE 8.0", "MSIE 9.0" };
        Number[] ys = new Number[] { 10.85, 7.35, 33.06, 2.81 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Firefox", 21.63);
        drillSeries = new DataSeries("Firefox versions");
        drillSeries.setId("Firefox");
        categories = new String[] { "Firefox 2.0", "Firefox 3.0",
                "Firefox 3.5", "Firefox 3.6", "Firefox 4.0" };
        ys = new Number[] { 0.20, 0.83, 1.58, 13.12, 5.43 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Chrome", 11.94);
        drillSeries = new DataSeries("Chrome versions");
        drillSeries.setId("Chrome");
        categories = new String[] { "Chrome 5.0", "Chrome 6.0", "Chrome 7.0",
                "Chrome 8.0", "Chrome 9.0", "Chrome 10.0", "Chrome 11.0",
                "Chrome 12.0" };
        ys = new Number[] { 0.12, 0.19, 0.12, 0.36, 0.32, 9.91, 0.50, 0.22 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Safari", 7.15);
        drillSeries = new DataSeries("Safari versions");
        drillSeries.setId("Safari");
        categories = new String[] { "Safari 5.0", "Safari 4.0",
                "Safari Win 5.0", "Safari 4.1", "Safari/Maxthon", "Safari 3.1",
                "Safari 4.1" };
        ys = new Number[] { 4.55, 1.42, 0.23, 0.21, 0.20, 0.19, 0.14 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Opera", 2.14);
        drillSeries = new DataSeries("Opera versions");
        drillSeries.setId("Opera");
        categories = new String[] { "Opera 9.x", "Opera 10.x", "Opera 11.x" };
        ys = new Number[] { 0.12, 0.37, 1.65 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        */
        conf.addSeries(series);

        return chart;
    }
}
