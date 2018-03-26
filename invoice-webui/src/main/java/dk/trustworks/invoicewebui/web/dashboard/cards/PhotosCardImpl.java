package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.jobs.DashboardPreloader;

import java.io.ByteArrayInputStream;

/**
 * Created by hans on 11/08/2017.
 */
public class PhotosCardImpl extends PhotosCardDesign implements Box {

    private DashboardPreloader dashboardPreloader;
    private int priority;
    private int boxWidth;
    private String name;

    public PhotosCardImpl(DashboardPreloader dashboardPreloader, int priority, int boxWidth, String name) {
        this.dashboardPreloader = dashboardPreloader;
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;
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

    public void loadPhoto() {
        byte[] randomPhoto = dashboardPreloader.getRandomPhoto();
        System.out.println("randomPhoto.length = " + randomPhoto.length);
        String randomText = dashboardPreloader.getRandomText();
        System.out.println("randomText = " + randomText);
        getLblPhotoText().setVisible(true);
        getPhoto().setSource(new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(randomPhoto),"logo.jpg"));
        getLblPhotoText().setValue(randomText);
        if(randomText.equals("")) getLblPhotoText().setVisible(false);
    }
}
