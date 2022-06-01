package dk.trustworks.invoicewebui.model;
import lombok.Data;


@Data
public class UserAccount {

    private String useruuid;
    private int account;
    private String username;

    public UserAccount() {
    }

    public UserAccount(String useruuid, int account, String username){
        this.useruuid = useruuid;
        this.account = account;
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "useruuid='" + useruuid + '\'' +
                ", account=" + account +
                '}';
    }

}

