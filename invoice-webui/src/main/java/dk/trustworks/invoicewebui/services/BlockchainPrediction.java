package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Currency;
import dk.trustworks.invoicewebui.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class BlockchainPrediction {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public BlockchainPrediction(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public Currency[] forecast(String currencyType) {
        System.out.println("BlockchainPrediction.forecast");
        long begin = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION Timestamps\n");
        sb.append("\n");
        sb.append("@ATTRIBUTE timestamp DATE \"yyyy-MM-dd HH:mm:ss\"\n");
        sb.append("@ATTRIBUTE price  NUMERIC\n");
        sb.append("\n");
        sb.append("@DATA\n");

        for (Currency currency : currencyRepository.findByCurrencytypeOrderByCollectedAsc(currencyType)) {
            sb.append("\"").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currency.getCollected())).append("\",").append(currency.getPrice()).append("\n");
        }

        System.out.println("ARFF created = " + (begin - System.currentTimeMillis()));
        System.out.println("sb = " + sb);
/*
        Instances data = new Instances(new BufferedReader(new StringReader(sb.stringIt())));
        if (data.classIndex() == -1)  data.setClassIndex(data.numAttributes() - 1);


        WekaForecaster forecaster = new WekaForecaster();
        forecaster.setFieldsToForecast("price");
        forecaster.setBaseForecaster(new SMOreg());

        forecaster.getTSLagMaker().setTimeStampField("timestamp");
        forecaster.buildForecaster(data, System.out);
        System.out.println("forecaster build = " + (begin - System.currentTimeMillis()));
        forecaster.primeForecaster(data);
        System.out.println("forecaster primed = " + (begin - System.currentTimeMillis()));

        List<List<NumericPrediction>> forecast = forecaster.forecast(forecastPeriod, System.out);
        System.out.println("forecasted = " + (begin - System.currentTimeMillis()));

        // output the predictions. Outer list is over the steps; inner list is over
        // the targets

        LocalDateTime now = LocalDateTime.now();
        System.out.println("now = " + now);
        */
        int forecastPeriod = 500;
        /*
        for (int i = 0; i < forecastPeriod; i++) {
            List<NumericPrediction> predsAtStep = forecast.get(i);
            NumericPrediction predForTarget = predsAtStep.get(0);
            currencies[i] = new Currency(
                    currencyType,
                    ((predForTarget.predicted() < 0.0) ? 0.0 : predForTarget.predicted()),
                    now.plus(Minutes.minutes(i)).toDate()
            );
        }
        System.out.println("Array created = " + (begin - System.currentTimeMillis()));
        */
        return new Currency[forecastPeriod];
    }
}
