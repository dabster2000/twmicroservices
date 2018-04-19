package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.IncomeForcastRepository;
import dk.trustworks.invoicewebui.repositories.TaskworkerconstraintRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CountEmployeesJob {

    private static final Logger log = LoggerFactory.getLogger(CountEmployeesJob.class);

    private final IncomeForcastRepository incomeForcastRepository;

    private final WorkRepository workRepository;

    private final TaskworkerconstraintRepository taskworkerconstraintRepository;

    private final UserRepository userRepository;

    private Map<LocalDate, List<User>> usersByLocalDate;

    private final LocalDate startDate;

    private final List<Double> dailyForecast;

    @Autowired
    public CountEmployeesJob(IncomeForcastRepository incomeForcastRepository, WorkRepository workRepository, TaskworkerconstraintRepository taskworkerconstraintRepository, UserRepository userRepository) {
        this.incomeForcastRepository = incomeForcastRepository;
        this.workRepository = workRepository;
        this.taskworkerconstraintRepository = taskworkerconstraintRepository;
        this.userRepository = userRepository;
        usersByLocalDate = new HashMap<>();
        startDate = LocalDate.of(2014, 2, 1);
        dailyForecast = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        countEmployees();
        try {
            workGraph();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 4 5 1/1 ?")
    public void countEmployees() {
        log.info("CountEmployeesJob.countEmployees");
        usersByLocalDate.clear();
        for (User user : userRepository.findAll()) {
            LocalDate tryDate = startDate;
            StatusType currentStatus = StatusType.TERMINATED;
            while(tryDate.isBefore(LocalDate.now().plusMonths(1))) {
                if(!usersByLocalDate.containsKey(tryDate)) {
                    usersByLocalDate.put(tryDate, new ArrayList<>());
                }
                for (UserStatus userStatus : user.getStatuses().stream().sorted(Comparator.comparing(UserStatus::getStatusdate).reversed()).collect(Collectors.toList())) {
                    if(userStatus.getStatusdate().isEqual(tryDate) || userStatus.getStatusdate().isBefore(tryDate)) {
                        currentStatus = userStatus.getStatus();
                        break;
                    }
                }
                if(currentStatus.equals(StatusType.ACTIVE)) {
                    usersByLocalDate.get(tryDate).add(user);
                }
                tryDate = tryDate.plusMonths(1);
            }
        }
        for (LocalDate localDate : usersByLocalDate.keySet()) {
            log.debug("-------------------------");
            log.debug("localDate = " + localDate);
            log.debug("users = " + usersByLocalDate.get(localDate).size());
            if(localDate.getYear() == 2017) {
                for (User user : usersByLocalDate.get(localDate)) {
                    log.debug(user.getUsername()+", ");
                }
                log.debug("");
            }
            log.debug("-------------------------");
        }
    }

    // http://wiki.pentaho.com/display/DATAMINING/Time+Series+Analysis+and+Forecasting+with+Weka
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void workGraph() throws Exception {
        log.info("CountEmployeesJob.workGraph");
        LocalDate now = LocalDate.now().minusDays(7);
        dailyForecast.clear();

        incomeForcastRepository.deleteByCreated(Date.valueOf(LocalDate.now()));

        String pattern = "yyyy-MM-dd";
        Map<String, Double> workByDate = new HashMap<>();
        for (Work work : workRepository.findByPeriod("2014-02-01", now.format(DateTimeFormatter.ofPattern(pattern)))) {
            String dateString = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay()).format(DateTimeFormatter.ofPattern(pattern));
            if(!workByDate.containsKey(dateString)) {
                workByDate.put(dateString, new Double(0.0));
            }
            List<Taskworkerconstraint> taskworkerconstraintList = taskworkerconstraintRepository.findByTaskAndUser(work.getTask(), work.getUser());
            if(taskworkerconstraintList.size() > 0 && taskworkerconstraintList.get(0).getPrice() > 0.0) {
                double income = workByDate.get(dateString) + (work.getWorkduration() * taskworkerconstraintList.get(0).getPrice());
                workByDate.put(dateString, income);
            }
        }
        log.debug("dailyForecast.size() = " + dailyForecast.size());

        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION Timestamps\n");
        sb.append("\n");
        sb.append("@ATTRIBUTE timestamp DATE \"yyyy-MM-dd HH:mm:ss\"\n");
        sb.append("@ATTRIBUTE hours  NUMERIC\n");
        sb.append("\n");
        sb.append("@DATA\n");
        LocalDate localDate = startDate;
        while(localDate.isBefore(now)) {
            String dateString = localDate.format(DateTimeFormatter.ofPattern(pattern));
            if(workByDate.containsKey(dateString)) {
                sb.append(patternizer(dateString)+","+workByDate.get(dateString)+"\n");
                dailyForecast.add(workByDate.get(dateString));
            } else {
                dailyForecast.add(0.0);
                sb.append(patternizer(dateString)+",0.0\n");
            }
            localDate = localDate.plusDays(1);
        }

        //System.out.println("sb = " + sb);
        //System.out.println(sb.toString());
        //System.out.println("localDate = " + localDate);
        //System.out.println("dailyForecast.size() = " + dailyForecast.size());


        Instances data = new Instances(new BufferedReader(new StringReader(sb.toString())));
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1)  data.setClassIndex(data.numAttributes() - 1);

        WekaForecaster forecaster = new WekaForecaster();

        // set the targets we want to forecast. This method calls
        // setFieldsToLag() on the lag maker object for us
        forecaster.setFieldsToForecast("hours");

        // default underlying classifier is SMOreg (SVM) - we'll use
        // gaussian processes for regression instead
        forecaster.setBaseForecaster(new GaussianProcesses());

        forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
        //forecaster.getTSLagMaker().setMinLag(1);
        //forecaster.getTSLagMaker().setMaxLag(12); // monthly data

        // add a month of the year indicator field
        forecaster.getTSLagMaker().setAddMonthOfYear(true);
        forecaster.getTSLagMaker().setAddDayOfWeek(true);
        forecaster.getTSLagMaker().setAddWeekendIndicator(true);
        forecaster.getTSLagMaker().setAddDayOfMonth(true);
        forecaster.getTSLagMaker().setAdjustForTrends(true);
        forecaster.getTSLagMaker().setAdjustForVariance(true);

        // add a quarter of the year indicator field
        //forecaster.getTSLagMaker().setAddQuarterOfYear(true);

        // build the model
        forecaster.buildForecaster(data, System.out);

        // prime the forecaster with enough recent historical data
        // to cover up to the maximum lag. In our case, we could just supply
        // the 12 most recent historical instances, as this covers our maximum
        // lag period
        forecaster.primeForecaster(data);

        //int daysInCurrentYear = Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.of(now.getYear(), 7, 1), now));

        // forecast for 12 units (months) beyond the end of the
        // training data
        List<List<NumericPrediction>> forecast = forecaster.forecast(800, System.out);

        // output the predictions. Outer list is over the steps; inner list is over
        // the targets

        double sum = 0.0;
        for (int i = 0; i < 800; i++) {
            List<NumericPrediction> predsAtStep = forecast.get(i);
            NumericPrediction predForTarget = predsAtStep.get(0);
            sum += predForTarget.predicted();
            //System.out.println("predForTarget.predicted() = " + predForTarget.predicted());
            Double amount = new Double((predForTarget.predicted() < 0.0) ? 0.0 : predForTarget.predicted());
            dailyForecast.add(amount);
            incomeForcastRepository.save(new IncomeForecast(i, amount));
        }

        log.debug("dailyForecast.size() = " + dailyForecast.size());
        log.debug("forecast = " + sum);
    }

    public List<User> getUsersByLocalDate(LocalDate localDate) {
        log.info("CountEmployeesJob.getUsersByLocalDate");
        log.info("localDate = [" + localDate + "]");
        List<User> users = usersByLocalDate.get(localDate);
        if(users == null) {
            log.warn("no users found: "+localDate);
            return new ArrayList<>();
        }
        return users;
    }


    private String patternizer(String date) {
        return "\""+date+" 00:00:00\"";
    }

    public List<Double> getDailyForecast() {
        if(dailyForecast.size() == 0) try {
            workGraph();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dailyForecast;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
