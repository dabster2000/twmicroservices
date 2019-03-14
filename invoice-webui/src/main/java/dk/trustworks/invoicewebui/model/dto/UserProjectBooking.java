package dk.trustworks.invoicewebui.model.dto;

public class UserProjectBooking extends UserBooking {

    public UserProjectBooking() {
    }

    public UserProjectBooking(String username, int i) {
        super(username, i);
    }

    public UserProjectBooking(String username, double m1AmountItemsPerProjekts, double m1AmountItemsPerPrebooking, double m1BookingPercentage, double m1MonthNorm, double m2AmountItemsPerProjekts, double m2AmountItemsPerPrebooking, double m2BookingPercentage, double m2MonthNorm, double m3AmountItemsPerProjekts, double m3AmountItemsPerPrebooking, double m3BookingPercentage, double m3MonthNorm) {
        super(username, m1AmountItemsPerProjekts, m1AmountItemsPerPrebooking, m1BookingPercentage, m1MonthNorm, m2AmountItemsPerProjekts, m2AmountItemsPerPrebooking, m2BookingPercentage, m2MonthNorm, m3AmountItemsPerProjekts, m3AmountItemsPerPrebooking, m3BookingPercentage, m3MonthNorm);
    }

    @Override
    public int getHoursDone() {
        return super.getHoursDone();
    }
}
