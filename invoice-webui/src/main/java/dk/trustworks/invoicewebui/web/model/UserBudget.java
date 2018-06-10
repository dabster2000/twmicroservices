package dk.trustworks.invoicewebui.web.model;

/**
 * Created by hans on 19/12/2016.
 */
public class UserBudget {

    public String uuid;
    public String name;
    public String date;
    public double budget;

    public UserBudget() {
    }

    public UserBudget(String uuid, String name, String date, double budget) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "UserBudget{" + "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", budget=" + budget +
                '}';
    }
}
