package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Service
public class AccountingRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public AccountingRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public UserAccount findUserAccountByUseruuid(String useruuid) {
        String url = apiGatewayUrl +"/accounting/user-accounts/"+useruuid;
        return (UserAccount) systemRestService.secureCall(url, GET, UserAccount.class).getBody();
    }

    public UserAccount getUserAccountByAccountNumber(int accountNumber) {
        try {
            String url = apiGatewayUrl + "/accounting/user-accounts/search/findByAccountNumber?account=" + accountNumber;
            return (UserAccount) systemRestService.secureCall(url, GET, UserAccount.class).getBody();
        } catch (Exception ignored) { }
        return null;
    }

    public void saveUserAccount(UserAccount userAccount) {
        String url = apiGatewayUrl +"/accounting/user-accounts";
        systemRestService.secureCall(url, POST, Void.class, userAccount);
    }
}
