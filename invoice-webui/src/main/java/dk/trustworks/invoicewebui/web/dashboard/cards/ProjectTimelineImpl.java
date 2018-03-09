package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.TrustworksColor;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static com.vaadin.addon.charts.model.ChartType.COLUMNRANGE;
import static java.time.LocalDate.now;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;

public class ProjectTimelineImpl extends ProjectTimelineDesign implements Box {

    private ProjectRepository projectRepository;
    private int priority;
    private int boxWidth;
    private String name;

    public ProjectTimelineImpl(ProjectRepository projectRepository, int priority, int boxWidth, String name) {
        this.projectRepository = projectRepository;
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;
    }

    public ProjectTimelineImpl init() {
        List<Project> projects = projectRepository.findAllByActiveTrueOrderByNameAsc();
        Component chart = getChart(projects);
        getContainer().addComponent(chart);
        return this;
    }

    private Component getChart(List<Project> projects) {
        Chart chart = new Chart(COLUMNRANGE);
        sort(projects, comparing(o -> o.getClient().getName()));

        Configuration conf = chart.getConfiguration();
        conf.getChart().setInverted(true);

        LocalDate startDate = now().minusMonths(6).withDayOfMonth(1);
        LocalDate endDate = now().plusMonths(6).withDayOfMonth(1);

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        for (Project project : projects) {
            if(project.getStartdate().isAfter(endDate)) continue;
            if(project.getEnddate().isBefore(startDate)) continue;
            xAxis.addCategory(project.getName());
        }

        //xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("");

        while(startDate.isBefore(endDate) || startDate.isEqual(endDate)){
            yAxis.addCategory(startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            startDate = startDate.plusMonths(1);
        }
        startDate = now().minusMonths(6).withDayOfMonth(1);

        conf.addyAxis(yAxis);
/*
        Tooltip tooltip = new Tooltip();
        tooltip.setValueSuffix("");
        conf.setTooltip(tooltip);

        PlotOptionsColumnrange columnRange = new PlotOptionsColumnrange();
        //columnRange.setDataLabels(new DataLabelsRange(true));
        columnRange.getDataLabels().setFormatter("function() {return this.y + '';}");
        conf.setPlotOptions(columnRange);

        conf.getLegend().setEnabled(false);
*/
        conf.getLegend().setEnabled(false);
        // RangeSeries has some helper constructors of which example below, but
        // here we use the raw DataSeries API
        // RangeSeries data = new RangeSeries("Temperatures", getRawData());

        DataSeries data = new DataSeries();
        TrustworksColor trustworksColor = TrustworksColor.getNextColor();
        String clientUUID = "";
        for (Project project : projects) {
            if(!clientUUID.equals(project.getClient().getUuid())) {
                clientUUID = project.getClient().getUuid();
                trustworksColor = TrustworksColor.getNextColor();
            }
            if(project.getStartdate().isAfter(endDate)) continue;
            if(project.getEnddate().isBefore(startDate)) continue;

            LocalDate projectStartDate = project.getStartdate().withDayOfMonth(1);
            LocalDate projectEndDate = project.getEnddate().withDayOfMonth(1);

            if(projectStartDate.isBefore(startDate)) projectStartDate = startDate;
            if(projectEndDate.isAfter(endDate)) projectEndDate = endDate;

            DataSeriesItem item = new DataSeriesItem();
            long xStartValue = ChronoUnit.MONTHS.between(startDate, projectStartDate);
            long xEndValue = ChronoUnit.MONTHS.between(startDate, projectEndDate);

            System.out.println("project = " + project);
            System.out.println("projectStartDate = " + projectStartDate);
            System.out.println("projectEndDate = " + projectEndDate);
            System.out.println("xStartValue, xEndValue = " + xStartValue + ", "+xEndValue);
            System.out.println("-------------------------------");

            item.setColor(new SolidColor(trustworksColor.getR(), trustworksColor.getG(), trustworksColor.getB()));
            item.setLow(xStartValue);
            item.setHigh(xEndValue);

            data.add(item);
        }
        conf.addSeries(data);

        return chart;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }
}
