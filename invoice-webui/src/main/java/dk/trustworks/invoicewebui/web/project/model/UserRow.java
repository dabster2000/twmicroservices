package dk.trustworks.invoicewebui.web.project.model;

/**
 * Created by hans on 21/08/2017.
 */
public class UserRow extends TaskRow {

    private String userUUID;
    private String username;
    private double rate;

    public UserRow(int month) {
        super(month);
    }

    public UserRow(String taskUUID, String taskName, int months, String userUUID, String username, double rate) {
        super(taskUUID, taskName, months);
        this.userUUID = userUUID;
        this.username = username;
        this.rate = rate;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserRow{");
        sb.append(super.toString());
        sb.append("userUUID='").append(userUUID).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }
}
