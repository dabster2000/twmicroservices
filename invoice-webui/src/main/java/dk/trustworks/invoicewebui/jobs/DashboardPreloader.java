package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.network.clients.VimeoAPI;
import dk.trustworks.invoicewebui.network.clients.model.DropboxFile;
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
    private String randomText;

    @Scheduled(fixedRate = 120000)
    public void loadNewPhoto() {
        DropboxFile randomBinaryFile = dropboxAPI.getRandomBinaryFile("/Shared/Administration/Intra/Billeder/Intranet/photos");
        randomPhoto = randomBinaryFile.getFileAsByteArray();
        if(randomBinaryFile.getFilename().contains(".")) {
            randomText = dropboxAPI.getSpecificTextFile(randomBinaryFile.getFilename().substring(0, randomBinaryFile.getFilename().lastIndexOf('.')) + ".html");
        }

    }

    public byte[] getRandomPhoto() {
        return randomPhoto;
    }

    public String getRandomText() {
        return randomText;
    }

    private String trustworksStatus;

    @Scheduled(cron = "0 0 * * * *")
    //@Scheduled(fixedRate = 600000)
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
            return new String[]{""};
        }
        return trips;
    }
}
