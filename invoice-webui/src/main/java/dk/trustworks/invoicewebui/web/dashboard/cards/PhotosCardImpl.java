package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
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

    public PhotosCardImpl() {
    }

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

    public PhotosCardImpl loadResourcePhoto(String resource) {
        getPhoto().setSource(new ThemeResource(resource));
        getPhoto().setSizeFull();
        getLblPhotoText().setVisible(false);
        return this;
    }

    public PhotosCardImpl withFullWidth() {
        this.setWidth(100, Unit.PERCENTAGE);
        return this;
    }

    public void loadRandomPhoto() {
        byte[] randomPhoto = dashboardPreloader.getRandomPhoto();
        if(randomPhoto== null) return;
        String randomText = dashboardPreloader.getRandomText();
        getLblPhotoText().setVisible(true);
        getPhoto().setSource(new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(randomPhoto),(Math.random()*2000)+".jpg"));
        getLblPhotoText().setValue(randomText);
        if(randomText == null || randomText.equals("")) getLblPhotoText().setVisible(false);
    }
}
