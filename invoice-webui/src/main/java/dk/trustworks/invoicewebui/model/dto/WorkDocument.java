package dk.trustworks.invoicewebui.model.dto;

import java.time.LocalDate;
import java.util.Arrays;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

public class WorkDocument {

    private final LocalDate month;
    private final String taskId;
    private final String userId;
    private String contractId;
    private final double[] workHours;
    private final double[] rates;

    public WorkDocument(LocalDate localDate, String taskId, String userId, String contractId) {
        this.month = localDate.withDayOfMonth(1);
        this.taskId = taskId;
        this.userId = userId;
        this.contractId = contractId;

        workHours = new double[31];
        rates = new double[31];
    }

    public LocalDate getMonth() {
        return month;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContractId() {
        return contractId;
    }

    public double getMonthHours() {
        return Arrays.stream(workHours).sum();
    }

    public double getMonthRevenue() {
        double sum = 0.0;
        for (int i = 0; i < 31; i++) {
            sum += (workHours[i]* rates[i]);
        }
        return sum;
    }

    public void fillRates(double rate) {
        Arrays.fill(rates, rate);
    }

    public void setRate(double rate, int day) {
        rates[day-1] = rate;
    }

    public String getKey() {
        return new StringBuilder().append(stringIt(month)).append(getContractId()).append(getUserId()).append(getTaskId()).toString();
    }
}
