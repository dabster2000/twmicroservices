package dk.trustworks.invoicewebui.network.clients;

import com.fasterxml.jackson.databind.SerializationFeature;
import dk.trustworks.invoicewebui.network.clients.model.economics.Collection;
import dk.trustworks.invoicewebui.network.clients.model.economics.EconomicsInvoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EconomicsAPI {

    public static final int[] OMSAETNING = {2101};
    public static final int[] PRODUKTION = {3010, 3030, 3040, 3050, 3080};
    public static final int[] LOENNINGER = {3502, 3503, 3504, 3510, 3531, 3578, 3591, 3597};
    public static final int[] PERSONALE = {3505, 3530, 3560, 3570, 3575, 3580, 3583, 3585, 3586, 3589, 3594};
    public static final int[] VARIABEL = {3601, 3608};
    public static final int[] LOKALE = {3701, 3709, 3717, 3729, 3730, 3735, 3737, 3738, 3797};
    public static final int[] SALGSFREMMENDE = {4001, 4003, 4006, 4007, 4008, 4020, 4030, 4040, 4042, 4050, 4055, 4066, 4090};
    public static final int[] ADMINISTRATION = {5214, 5216, 5218, 5219, 5222, 5224, 5228, 5229, 5233, 5237, 5241, 5242, 5247, 5250, 5254, 5258, 5259, 5262, 5266, 5268, 5271, 5275, 5298};

    @Autowired
    RestTemplate restTemplate;

    public List<Collection> getInvoices(int[] accounts, String date) {
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        List<Collection> collectionList = new ArrayList<>();
        for (int account : accounts) {
            collectionList.addAll(getCollections(date, account));
        }
        return collectionList;
    }

    private List<Collection> getCollections(String date, int account) {
        List<Collection> collectionResultList = new ArrayList<>();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("X-AppSecretToken", "GCCmf2TIXfrY3D9jEiqss8gUPa59rvBFbYAEjF1h7zQ1");
        requestHeaders.add("X-AgreementGrantToken", "B03oSVDidmk53uOIdMV9ptnI2hlVQykGdTvmisrtFq01");

        HttpEntity request = new HttpEntity(requestHeaders);

        int page = 0;
        String url = "https://restapi.e-conomic.com/accounts/" + account + "/accounting-years/" + date + "/entries?pagesize=1000&skippages="+page;
        do {
            ResponseEntity<EconomicsInvoice> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    EconomicsInvoice.class
            );

            EconomicsInvoice economicsInvoice = response.getBody();
            collectionResultList.addAll(economicsInvoice.getCollection());
            url = economicsInvoice.getPagination().getNextPage();
        } while (url != null);

        return collectionResultList;
    }
}
