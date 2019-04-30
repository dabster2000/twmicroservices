package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.repositories.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CollectCurrencies {

    private static final Logger log = LoggerFactory.getLogger(CountEmployeesJob.class);

    private final int exampleLength = 22;

    @Autowired
    private CurrencyRepository currencyRepository;

    @PostConstruct
    public void init() {
        //collectCryptoCompare();
    }

    //@Scheduled(fixedRate = 60000)
    /*
    public void collect() {
        log.info("CollectCurrencies.collect");

        RestTemplate restTemplate = new RestTemplate();
        CryptoCompare ether = restTemplate.getForObject("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=BTC,USD,EUR", CryptoCompare.class);
        log.debug(ether.stringIt());
        currencyRepository.save(new Currency("ETH", ether.getUsd()));

        CryptoCompare bitcoin = restTemplate.getForObject("https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=BTC,USD,EUR", CryptoCompare.class);
        log.debug(bitcoin.stringIt());
        currencyRepository.save(new Currency("BTC", bitcoin.getUsd()));

        CryptoCompare bitcoinCash = restTemplate.getForObject("https://min-api.cryptocompare.com/data/price?fsym=BCH&tsyms=BTC,USD,EUR", CryptoCompare.class);
        log.debug(bitcoinCash.stringIt());
        currencyRepository.save(new Currency("BCH", bitcoinCash.getUsd()));
    }

    //@Scheduled(fixedRate = 60000)
    public void collect2() {
        RestTemplate restTemplate = new RestTemplate();
        CryptoCompare ether = restTemplate.getForObject("https://www.quandl.com/api/v3/datasets/WIKI/FB/data.json", CryptoCompare.class);
        log.debug(ether.stringIt());
    }

    //@Scheduled(fixedRate = 1000)
    public void collectCryptoCompare() throws IOException {
        log.info("CollectCurrencies.collectCryptoCompare");

        RestTemplate restTemplate = new RestTemplate();
        CryptoCompareRoot bitcoins = restTemplate.getForObject("https://min-api.cryptocompare.com/data/histohour?fsym=BTC&tsym=USD&limit=2000&aggregate=1", CryptoCompareRoot.class);



        //Logger.getLogger("org").setLevel(Level.OFF); // shut down log info.

        //String file = new ClassPathResource("prices-split-adjusted.csv").getFile().getAbsolutePath();
        String symbol = "BTC"; // stock name
        int batchSize = 64; // mini-batch size
        double splitRatio = 0.9; // 90% for training, 10% for testing
        int epochs = 100; // training epochs
/*
        log.info("Create dataSet iterator...");
        PriceCategory category = PriceCategory.CLOSE; // CLOSE: predict close price
        StockDataSetIterator iterator = new StockDataSetIterator(bitcoins, symbol, batchSize, exampleLength, splitRatio, category);
        log.info("Load test dataset...");
        List<Pair<INDArray, INDArray>> test = iterator.getTestDataSet();

        log.info("Build lstm networks...");
        MultiLayerNetwork net = RecurrentNets.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());

        log.info("Training...");
        for (int i = 0; i < epochs; i++) {
            while (iterator.hasNext()) net.fit(iterator.next()); // fit model using mini-batch data
            iterator.reset(); // reset iterator
            net.rnnClearPreviousState(); // clear previous state
        }

        log.info("Saving model...");
        File locationToSave = new File("StockPriceLSTM_".concat(String.valueOf(category)).concat(".zip"));
        // saveUpdater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this to train your network more in the future
        //ModelSerializer.writeModel(net, locationToSave, true);

        log.info("Load model...");
        //net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);

        log.info("Testing...");
        if (category.equals(PriceCategory.ALL)) {
            INDArray max = Nd4j.create(iterator.getMaxArray());
            INDArray min = Nd4j.create(iterator.getMinArray());
            predictAllCategories(net, test, max, min);
        } else {
            double max = iterator.getMaxNum(category);
            double min = iterator.getMinNum(category);
            predictPriceOneAhead(net, test, max, min, category);
        }
        log.info("Done...");
    }

    private void predictPriceOneAhead (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min, PriceCategory category) {
        double[] predicts = new double[testData.size()];
        double[] actuals = new double[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getDouble(exampleLength - 1) * (max - min) + min;
            actuals[i] = testData.get(i).getValue().getDouble(0);
        }
        log.info("Print out Predictions and Actual Values...");
        log.info("Predict,Actual");
        for (int i = 0; i < predicts.length; i++) log.info(predicts[i] + "," + actuals[i]);
        log.info("Plot...");
        //PlotUtil.plot(predicts, actuals, String.valueOf(category));
    }

    private void predictPriceMultiple (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min) {
        // TODO
    }

    private void predictAllCategories (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min) {
        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(exampleLength - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }
        log.info("Print out Predictions and Actual Values...");
        log.info("Predict\tActual");
        for (int i = 0; i < predicts.length; i++) log.info(predicts[i] + "\t" + actuals[i]);
        log.info("Plot...");
        for (int n = 0; n < 5; n++) {
            double[] pred = new double[predicts.length];
            double[] actu = new double[actuals.length];
            for (int i = 0; i < predicts.length; i++) {
                pred[i] = predicts[i].getDouble(n);
                actu[i] = actuals[i].getDouble(n);
            }
            String name;
            switch (n) {
                case 0: name = "Stock OPEN Price"; break;
                case 1: name = "Stock CLOSE Price"; break;
                case 2: name = "Stock LOW Price"; break;
                case 3: name = "Stock HIGH Price"; break;
                case 4: name = "Stock VOLUME Amount"; break;
                default: throw new NoSuchElementException();
            }
            //PlotUtil.plot(pred, actu, name);
        }
    }
*/
}
