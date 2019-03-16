package dk.trustworks.invoicewebui.model.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBooking {

    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void addAmountItemsPerPrebooking(double amountItemsPerPrebooking, int i) {
        this.amountItemsPerPrebooking[i] += amountItemsPerPrebooking;
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
                ", amountItemsPerProjects=" + Arrays.toString(amountItemsPerProjects) +
                ", amountItemsPerPrebooking=" + Arrays.toString(amountItemsPerPrebooking) +
                ", bookingPercentage=" + Arrays.toString(bookingPercentage) +
                ", monthNorm=" + Arrays.toString(monthNorm) +
                ", subProjects=" + subProjects.stream().map(UserBooking::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
