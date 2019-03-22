package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.ai.riskminimizer.RiverWaves;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ForecastJob {

    private static final Logger log = LoggerFactory.getLogger(ForecastJob.class);

    private final WorkRepository workRepository;

    private final ContractService contractService;

    private final LocalDate startDate;

    private final List<Double> dailyForecast;

    @Autowired
    public ForecastJob(WorkRepository workRepository, ContractService contractService) {
        this.workRepository = workRepository;
        this.contractService = contractService;
        startDate = LocalDate.of(2014, 2, 1);
        dailyForecast = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        //countEmployees();
        //forecastIncome();
    }

    // 15:11:17.612 [main, ,] INFO  d.t.invoicewebui.jobs.ForecastJob - values: [103100.0, 93100.0, 226235.0, 294270.0, 326537.0, 193307.5, 397940.0, 423932.5, 579376.0, 641171.5, 567568.0, 718686.5, 681644.5, 686475.0, 750067.5, 850977.5, 912532.5, 469970.0, 879850.0, 1279122.5, 1111975.0, 1441647.5, 1104470.0, 1280620.0, 1296282.5, 1166315.0, 1393735.0, 1288690.0, 1522190.0, 777435.0, 1301140.0, 1762920.0, 1681095.0, 1818043.75, 1434672.5, 2207337.5, 1828650.0, 2147575.0, 1376800.0, 2181482.25000004, 2287435.750000088, 898967.5000000244, 2121318.000000005, 2571438.5, 2188882.5, 2685892.25, 2132579.75,
    // 2580428.5, 2201324.0, 2252372.5, 2524896.5, 2754978.75, 0.0

    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void forecastIncome() {
        log.info("CountEmployeesJob.forecastIncome");
        LocalDate now = LocalDate.now().minusDays(7);
        dailyForecast.clear();

        //incomeForcastRepository.deleteByCreatedAndItemtype(Date.valueOf(LocalDate.now()), "INCOME");

        String pattern = "yyyy-MM-dd";
        Map<String, Double> workByDate = new HashMap<>();
        for (Work work : workRepository.findByPeriod(startDate.format(DateTimeFormatter.ofPattern(pattern)), now.format(DateTimeFormatter.ofPattern(pattern)))) {
            String dateString = DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern(pattern));
            if(!workByDate.containsKey(dateString)) {
                workByDate.put(dateString, 0.0);
            }
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0.0) {
                double income = workByDate.get(dateString) + (work.getWorkduration() * rate);
                workByDate.put(dateString, income);
            }
        }
        System.out.println("workByDate.size() = " + workByDate.size());
        double[] y = new double[workByDate.size()-5];
        List<String> collect = workByDate.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (int i = 0, collectSize = collect.size(); i < collectSize-5; i++) {
            String key = collect.get(i);
            y[i] = workByDate.get(key);
        }

        log.info("dailyForecast.size() = " + y.length);
        log.info("values: " + Arrays.toString(y));

        double[] resultArray = new double[12];
        for (int i = 0; i < 12; i++) {
            RiverWaves td = new RiverWaves();
            double result = td.autodetection(ArrayUtils.clone(y));
            resultArray[i] = result * RiverWaves.daxmax;
            System.out.println("Autopredicted Value is " + result);
            y = ArrayUtils.add( y, result);
        }

        log.info("dailyForecast.size() = " + dailyForecast.size());
        log.info("forecast = " + Arrays.toString(resultArray));
    }
}
