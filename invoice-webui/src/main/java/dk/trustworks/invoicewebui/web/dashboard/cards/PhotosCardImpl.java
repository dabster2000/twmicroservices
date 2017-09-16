package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.EventType;
import dk.trustworks.invoicewebui.model.TrustworksEvent;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.TrustworksEventRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by hans on 11/08/2017.
 */
public class PhotosCardImpl extends PhotosCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;

    public PhotosCardImpl(DropboxAPI dropboxAPI, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;
        byte[] randomPhoto = dropboxAPI.getRandomFile("/Shared/TrustWorks/Billeder/Intranet/photos");

        getPhoto().setSource(new StreamResource((StreamResource.StreamSource) () ->
                new ByteArrayInputStream(randomPhoto),"logo.jpg"));

        ///Users/hans/Dropbox (TrustWorks ApS)/Shared/TrustWorks/Billeder/Intranet/photos
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }

}
