package dk.trustworks.invoicewebui.network.clients;

import com.fasterxml.jackson.databind.SerializationFeature;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.network.clients.model.economics.Collection;
import dk.trustworks.invoicewebui.network.clients.model.economics.EconomicsInvoice;
import dk.trustworks.invoicewebui.repositories.ExpenseDetailsRepository;
import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EconomicsAPI {

    public static final int[] OMSAETNING = {2101, 2102, 2103, 2105, 2110, 2115, 2120, 2130, 2140, 2150, 2160};
    public static final int[] PRODUKTION = {3010, 3030, 3040, 3050, 3080};
    public static final int[] LOENNINGER = {3502, 3503, 3504, 3510, 3530, 3531, 3556, 3578, 3591, 3597, 359};
    public static final int[] PERSONALE = {3505, 3560, 3570, 3575, 3580, 3583, 3585, 3586, 3589, 3594};
    public static final int[] VARIABEL = {3601, 3608};
    public static final int[] LOKALE = {3701, 3709, 3717, 3729, 3730, 3735, 3737, 3738, 3797};
    public static final int[] SALGSFREMMENDE = {4001, 4003, 4006, 4007, 4008, 4020, 4030, 4040, 4042, 4050, 4055, 4066, 4090};
    public static final int[] ADMINISTRATION = {5214, 5216, 5218, 5219, 5222, 5224, 5228, 5229, 5233, 5237, 5241, 5242, 5247, 5250, 5254, 5258, 5259, 5262, 5266, 5268, 5271, 5275, 5298};


    public static final Range<Integer> OMSAETNING_ACCOUNTS = Range.between(2100, 2199);
    public static final Range<Integer> PRODUKTION_ACCOUNTS = Range.between(3000, 3099);
    public static final Range<Integer> LOENNINGER_ACCOUNTS = Range.between(3500, 3599);
    public static final Range<Integer> PERSONALE_ACCOUNTS = Range.between(10000, 10100);
    public static final Range<Integer> VARIABEL_ACCOUNTS = Range.between(3600, 3699);
    public static final Range<Integer> LOKALE_ACCOUNTS = Range.between(3700, 3799);
    public static final Range<Integer> SALG_ACCOUNTS = Range.between(4000, 4099);
    public static final Range<Integer> ADMINISTRATION_ACCOUNTS = Range.between(5200, 5299);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExpenseDetailsRepository expenseDetailsRepository;

    @Value("${XAppSecretToken}")
    private String xAppSecretToken;

    @Value("${XAgreementGrantToken}")
    private String xAgreementGrantToken;
/*
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

 */
/*
    private List<Collection> getCollections(String date, int account) {
        List<Collection> collectionResultList = new ArrayList<>();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("X-AppSecretToken", xAppSecretToken);
        requestHeaders.add("X-AgreementGrantToken", xAgreementGrantToken);

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

 */

    public Map<Range<Integer>, List<Collection>> getAllEntries(String date) {
        Map<Range<Integer>, List<Collection>> collectionResultMap = new HashMap<>();
        collectionResultMap.put(OMSAETNING_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(PRODUKTION_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(LOENNINGER_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(PERSONALE_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(VARIABEL_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(LOKALE_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(SALG_ACCOUNTS, new ArrayList<>());
        collectionResultMap.put(ADMINISTRATION_ACCOUNTS, new ArrayList<>());


        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("X-AppSecretToken", xAppSecretToken);
        System.out.println("xAppSecretToken = " + xAppSecretToken);
        requestHeaders.add("X-AgreementGrantToken", xAgreementGrantToken);
        System.out.println("xAgreementGrantToken = " + xAgreementGrantToken);

        HttpEntity request = new HttpEntity(requestHeaders);

        int page = 0;
        String url = "https://restapi.e-conomic.com/accounting-years/" + date + "/entries?pagesize=1000&skippages="+page;
        System.out.println("EconomicsAPI.getAllEntries");
        List<ExpenseDetails> expenseDetails = new ArrayList<>();

        do {
            ResponseEntity<EconomicsInvoice> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    EconomicsInvoice.class
            );

            EconomicsInvoice economicsInvoice = response.getBody();

            for (Collection collection : economicsInvoice.getCollection()) {
                int accountNumber = collection.getAccount().getAccountNumber();
                if(Arrays.binarySearch(PERSONALE, accountNumber) > -1) {
                    collection.getAccount().setAccountNumber(accountNumber-LOENNINGER_ACCOUNTS.getMinimum()+PERSONALE_ACCOUNTS.getMinimum());
                }
                collectionResultMap.keySet().forEach(integerRange -> {
                    if(integerRange.contains(collection.getAccount().getAccountNumber())) collectionResultMap.get(integerRange).add(collection);
                });
                expenseDetails.add(new ExpenseDetails(collection.getEntryNumber(), collection.getAccount().getAccountNumber(), collection.getAmount(), LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).withDayOfMonth(1), collection.getText()));
            }
            url = economicsInvoice.getPagination().getNextPage();
        } while (url != null);

        expenseDetailsRepository.save(expenseDetails);

        return collectionResultMap;
    }
}
