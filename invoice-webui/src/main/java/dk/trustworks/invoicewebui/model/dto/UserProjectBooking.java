package dk.trustworks.invoicewebui.model.dto;

public class UserProjectBooking extends UserBooking {

    public UserProjectBooking() {
    }

    public UserProjectBooking(String username, int i) {
        super(username, i);
    }

    @Override
    public int getHoursDone() {
        return super.getHoursDone();
    }
}
