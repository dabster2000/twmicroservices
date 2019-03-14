package dk.trustworks.invoicewebui.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserBooking {

    private String username;

    private double m1AmountItemsPerProjekts;
    private double m1AmountItemsPerPrebooking;
    private double m1BookingPercentage;
    private double m1MonthNorm;

    private double m2AmountItemsPerProjekts;
    private double m2AmountItemsPerPrebooking;
    private double m2BookingPercentage;
    private double m2MonthNorm;

    private double m3AmountItemsPerProjekts;
    private double m3AmountItemsPerPrebooking;
    private double m3BookingPercentage;
    private double m3MonthNorm;

    private double[] amountItemsPerProjects;
    private double[] amountItemsPerPrebooking;
    private double[] bookingPercentage;
    private double[] monthNorm;

    private List<UserBooking> subProjects = new ArrayList<>();

    public UserBooking() {
    }

    public UserBooking(String username, int monthsInFuture) {
        this.username = username;
        amountItemsPerProjects = new double[monthsInFuture];
        amountItemsPerPrebooking = new double[monthsInFuture];
        bookingPercentage = new double[monthsInFuture];
        monthNorm = new double[monthsInFuture];
    }

    public UserBooking(String username, double m1AmountItemsPerProjekts, double m1AmountItemsPerPrebooking, double m1BookingPercentage, double m1MonthNorm, double m2AmountItemsPerProjekts, double m2AmountItemsPerPrebooking, double m2BookingPercentage, double m2MonthNorm, double m3AmountItemsPerProjekts, double m3AmountItemsPerPrebooking, double m3BookingPercentage, double m3MonthNorm) {
        this.username = username;
        this.m1AmountItemsPerProjekts = m1AmountItemsPerProjekts;
        this.m1AmountItemsPerPrebooking = m1AmountItemsPerPrebooking;
        this.m1BookingPercentage = m1BookingPercentage;
        this.m1MonthNorm = m1MonthNorm;
        this.m2AmountItemsPerProjekts = m2AmountItemsPerProjekts;
        this.m2AmountItemsPerPrebooking = m2AmountItemsPerPrebooking;
        this.m2BookingPercentage = m2BookingPercentage;
        this.m2MonthNorm = m2MonthNorm;
        this.m3AmountItemsPerProjekts = m3AmountItemsPerProjekts;
        this.m3AmountItemsPerPrebooking = m3AmountItemsPerPrebooking;
        this.m3BookingPercentage = m3BookingPercentage;
        this.m3MonthNorm = m3MonthNorm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getM1AmountItemsPerProjekts() {
        return m1AmountItemsPerProjekts;
    }

    public void setM1AmountItemsPerProjekts(double m1AmountItemsPerProjekts) {
        this.m1AmountItemsPerProjekts = m1AmountItemsPerProjekts;
    }

    public void addM1AmountItemsPerProjects(double m1AmountItemsPerProjekts) {
        this.m1AmountItemsPerProjekts += m1AmountItemsPerProjekts;
    }

    public double getM1AmountItemsPerPrebooking() {
        return m1AmountItemsPerPrebooking;
    }

    public void setM1AmountItemsPerPrebooking(double m1AmountItemsPerPrebooking) {
        this.m1AmountItemsPerPrebooking = m1AmountItemsPerPrebooking;
    }

    public double getM1BookingPercentage() {
        return m1BookingPercentage;
    }

    public void setM1BookingPercentage(double m1BookingPercentage) {
        this.m1BookingPercentage = m1BookingPercentage;
    }

    public double getM1MonthNorm() {
        return m1MonthNorm;
    }

    public void setM1MonthNorm(double m1MonthNorm) {
        this.m1MonthNorm = m1MonthNorm;
    }

    public double getAmountItemsPerProjects(int i) {
        return amountItemsPerProjects[i];
    }

    public void setAmountItemsPerProjects(double amountItemsPerProjects, int i) {
        this.amountItemsPerProjects[i] = amountItemsPerProjects;
    }

    public void addAmountItemsPerProjects(double amountItemsPerProjects, int i) {
        this.amountItemsPerProjects[i] += amountItemsPerProjects;
    }

    public double getAmountItemsPerPrebooking(int i) {
        return amountItemsPerPrebooking[i];
    }

    public void setAmountItemsPerPrebooking(double amountItemsPerPrebooking, int i) {
        this.amountItemsPerPrebooking[i] = amountItemsPerPrebooking;
    }

    public double getBookingPercentage(int i) {
        return bookingPercentage[i];
    }

    public void setBookingPercentage(double bookingPercentage, int i) {
        this.bookingPercentage[i] = bookingPercentage;
    }

    public double getMonthNorm(int i) {
        return monthNorm[i];
    }

    public void setMonthNorm(double monthNorm, int i) {
        this.monthNorm[i] = monthNorm;
    }

    public double getM2AmountItemsPerProjekts() {
        return m2AmountItemsPerProjekts;
    }

    public void setM2AmountItemsPerProjekts(double m2AmountItemsPerProjekts) {
        this.m2AmountItemsPerProjekts = m2AmountItemsPerProjekts;
    }

    public void addM2AmountItemsPerProjects(double m2AmountItemsPerProjekts) {
        this.m2AmountItemsPerProjekts += m2AmountItemsPerProjekts;
    }

    public double getM2AmountItemsPerPrebooking() {
        return m2AmountItemsPerPrebooking;
    }

    public void setM2AmountItemsPerPrebooking(double m2AmountItemsPerPrebooking) {
        this.m2AmountItemsPerPrebooking = m2AmountItemsPerPrebooking;
    }

    public double getM2BookingPercentage() {
        return m2BookingPercentage;
    }

    public void setM2BookingPercentage(double m2BookingPercentage) {
        this.m2BookingPercentage = m2BookingPercentage;
    }

    public double getM2MonthNorm() {
        return m2MonthNorm;
    }

    public void setM2MonthNorm(double m2MonthNorm) {
        this.m2MonthNorm = m2MonthNorm;
    }

    public double getM3AmountItemsPerProjekts() {
        return m3AmountItemsPerProjekts;
    }

    public void setM3AmountItemsPerProjekts(double m3AmountItemsPerProjekts) {
        this.m3AmountItemsPerProjekts = m3AmountItemsPerProjekts;
    }

    public void addM3AmountItemsPerProjects(double m3AmountItemsPerProjekts) {
        this.m3AmountItemsPerProjekts += m3AmountItemsPerProjekts;
    }

    public double getM3AmountItemsPerPrebooking() {
        return m3AmountItemsPerPrebooking;
    }

    public void setM3AmountItemsPerPrebooking(double m3AmountItemsPerPrebooking) {
        this.m3AmountItemsPerPrebooking = m3AmountItemsPerPrebooking;
    }

    public double getM3BookingPercentage() {
        return m3BookingPercentage;
    }

    public void setM3BookingPercentage(double m3BookingPercentage) {
        this.m3BookingPercentage = m3BookingPercentage;
    }

    public double getM3MonthNorm() {
        return m3MonthNorm;
    }

    public void setM3MonthNorm(double m3MonthNorm) {
        this.m3MonthNorm = m3MonthNorm;
    }

    public List<UserBooking> getSubProjects() {
        return subProjects;
    }

    public void setSubProjects(List<UserBooking> subProjects) {
        this.subProjects = subProjects;
    }

    public void addSubProject(UserProjectBooking subProject) {
        subProjects.add(subProject);
    }

    public int getHoursDone() {
        return getSubProjects().stream()
                .map(project -> project.getHoursDone())
                .reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        return "UserBooking{" +
                "username='" + username + '\'' +
                ", m1AmountItemsPerProjekts=" + m1AmountItemsPerProjekts +
                ", m1AmountItemsPerPrebooking=" + m1AmountItemsPerPrebooking +
                ", m1BookingPercentage=" + m1BookingPercentage +
                ", m1MonthNorm=" + m1MonthNorm +
                ", m2AmountItemsPerProjekts=" + m2AmountItemsPerProjekts +
                ", m2AmountItemsPerPrebooking=" + m2AmountItemsPerPrebooking +
                ", m2BookingPercentage=" + m2BookingPercentage +
                ", m2MonthNorm=" + m2MonthNorm +
                ", m3AmountItemsPerProjekts=" + m3AmountItemsPerProjekts +
                ", m3AmountItemsPerPrebooking=" + m3AmountItemsPerPrebooking +
                ", m3BookingPercentage=" + m3BookingPercentage +
                ", m3MonthNorm=" + m3MonthNorm +
                ", subProjects=" + subProjects.stream().map(UserBooking::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
