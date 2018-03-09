package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.network.clients.VimeoAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DashboardPreloader {

    private final DropboxAPI dropboxAPI;

    private final VimeoAPI vimeoAPI;

    @PostConstruct
    public void onStartup() {
        loadTrustworksStatus();
    }

    @Autowired
    public DashboardPreloader(DropboxAPI dropboxAPI, VimeoAPI vimeoAPI) {
        this.dropboxAPI = dropboxAPI;
        this.vimeoAPI = vimeoAPI;
    }

    private byte[] randomPhoto;

    @Scheduled(fixedRate = 120000)
    public void loadNewPhoto() {
        randomPhoto = dropboxAPI.getRandomFile("/Shared/Administration/Intra/Billeder/Intranet/photos");
    }

    public byte[] getRandomPhoto() {
        return randomPhoto;
    }

    private String trustworksStatus;

    @Scheduled(cron = "0 1 1 * * ?")
    public void loadTrustworksStatus() {
        trustworksStatus = vimeoAPI.getTrustworksStatus();
    }

    public String getTrustworksStatus() {
        return trustworksStatus;
    }

    private String[] trips;

    @Scheduled(fixedRate = 200000)
    void loadTrustworksTrips() {
        trips = vimeoAPI.getTrustworksTrips();
    }

    public String[] getTrips() {
        if(trips==null || trips.length==0) {
            String[] empty = {""};
            return empty;
        }
        return trips;
    }
}
