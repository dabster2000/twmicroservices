package dk.trustworks.invoicewebui.web.blockchain.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Currency;
import dk.trustworks.invoicewebui.repositories.CurrencyRepository;
import dk.trustworks.invoicewebui.services.BlockchainPrediction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@SpringComponent
@SpringUI
public class CurrencyGraphs extends VerticalLayout {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private BlockchainPrediction blockchainPrediction;

    public CurrencyGraphs init() {
        this.removeAllComponents();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        ChartCard bitcoinCard = new ChartCard();
        bitcoinCard.getLblTitle().setValue("Bitcoin Price");
        List<Currency> btcList = currencyRepository.findByCurrencytypeOrderByCollectedAsc("BTC");
        DataProvider<Currency, ?> dataProvider = new ListDataProvider<>(btcList);
        Chart btcChart = createCurrencyChart(dataProvider);
        bitcoinCard.getContent().addComponent(btcChart);
        chartRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(bitcoinCard);

        bitcoinCard.getBtnForecast().addClickListener(event -> {
            try {
                btcList.addAll(Arrays.asList(blockchainPrediction.forecast("BTC")));
                dataProvider.refreshAll();
                event.getButton().setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ChartCard bitcoinCashCard = new ChartCard();
        bitcoinCashCard.getLblTitle().setValue("Bitcoin Cash Price");
        List<Currency> bchList = currencyRepository.findByCurrencytypeOrderByCollectedAsc("BCH");
        DataProvider<Currency, ?> dataProviderBCH = new ListDataProvider<>(bchList);
        Chart bchChart = createCurrencyChart(dataProviderBCH);
        bitcoinCashCard.getContent().addComponent(bchChart);
        chartRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(bitcoinCashCard);

        bitcoinCashCard.getBtnForecast().addClickListener(event -> {
            try {
                bchList.addAll(Arrays.asList(blockchainPrediction.forecast("BCH")));
                dataProviderBCH.refreshAll();
                event.getButton().setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ChartCard etheriumCard = new ChartCard();
        etheriumCard.getLblTitle().setValue("Etherium Price");
        List<Currency> ethList = currencyRepository.findByCurrencytypeOrderByCollectedAsc("ETH");
        DataProvider<Currency, ?> dataProviderETH = new ListDataProvider<>(ethList);
        Chart ethChart = createCurrencyChart(dataProviderETH);
        etheriumCard.getContent().addComponent(ethChart);
        chartRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(etheriumCard);

        etheriumCard.getBtnForecast().addClickListener(event -> {
            try {
                ethList.addAll(Arrays.asList(blockchainPrediction.forecast("ETH")));
                dataProviderETH.refreshAll();
                event.getButton().setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.addComponent(responsiveLayout);

        return this;
    }
/*
    private DataSeries createForecast(String coin) {
        DataSeries googSeries = new DataSeries();
        googSeries.setName(coin+" forecast");
        try {
            for (Currency currency : blockchainPrediction.forecast(coin)) {
                DataSeriesItem item = new DataSeriesItem();
                item.setX(currency.getCollected().toInstant());
                item.setY(currency.getPrice());
                googSeries.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googSeries;
    }
*/
    private DataSeries createForecast(String coin) {
        DataSeries googSeries = new DataSeries();
        googSeries.setName(coin+" forecast");
        try {
            for (Currency currency : blockchainPrediction.forecast(coin)) {
                DataSeriesItem item = new DataSeriesItem();
                item.setX(currency.getCollected().toInstant());
                item.setY(currency.getPrice());
                googSeries.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googSeries;
    }

    public Chart createCurrencyChart(DataProvider<Currency, ?> dataProvider) {
        final Chart chart = new Chart();
        chart.setHeight("600px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();

        YAxis yAxis = new YAxis();

        PlotLine plotLine = new PlotLine();
        plotLine.setValue(2);
        plotLine.setWidth(2);
        plotLine.setColor(SolidColor.SILVER);
        yAxis.setPlotLines(plotLine);
        configuration.addyAxis(yAxis);
        configuration.getRangeSelector().setButtons(
                new RangeSelectorButton(RangeSelectorTimespan.DAY, "1d"),
                new RangeSelectorButton(RangeSelectorTimespan.WEEK, "7d"),
                new RangeSelectorButton(RangeSelectorTimespan.MONTH, "1m"),
                new RangeSelectorButton(RangeSelectorTimespan.YEAR, "1y"));

        //DataSeries aaplSeries = new DataSeries();
        //aaplSeries.setName(coin);
        DataProviderSeries<Currency> series = new DataProviderSeries<>(dataProvider);
        series.setY(Currency::getPrice);
        series.setX(Currency::getCollected);
        configuration.addSeries(series);
/*
        for (Currency data : currencyRepository.findByCurrencytypeOrderByCollectedAsc(coin)) {
            DataSeriesItem item = new DataSeriesItem();
            item.setX(data.getCollected().toInstant());
            item.setY(data.getPrice());
            aaplSeries.add(item);
        }
*/
        //configuration.setSeries(aaplSeries); //, googSeries, msftSeries

        chart.drawChart(configuration);
        return chart;
    }
}
