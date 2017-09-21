package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.OLMapOptions;
import org.vaadin.addon.vol3.OLView;
import org.vaadin.addon.vol3.OLViewOptions;
import org.vaadin.addon.vol3.client.Projections;
import org.vaadin.addon.vol3.layer.OLLayer;
import org.vaadin.addon.vol3.layer.OLTileLayer;
import org.vaadin.addon.vol3.source.OLOSMSource;
import org.vaadin.addon.vol3.source.OLSource;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;
import org.vaadin.addons.locationtextfield.GeocodedLocation;
import org.vaadin.addons.locationtextfield.LocationTextField;
import org.vaadin.addons.locationtextfield.OpenStreetMapGeocoder;

/**
 * Created by hans on 13/08/2017.
 */
public class ProjectMapLocationImpl extends ProjectMapLocationDesign {

    protected OLMap map;

    private ProjectRepository projectRepository;

    public ProjectMapLocationImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectMapLocationImpl init(Project project) {

        map=createMap(project.getLongitude(), project.getLatitude());


        final LocationTextField<GeocodedLocation> ltf = LocationTextField.newBuilder()
                .withCaption("Main project location:")
                .withDelayMillis(1200)
                .withLocationProvider(OpenStreetMapGeocoder.getInstance())
                .withText(project.getAddress())
                .withMinimumQueryCharacters(5)
                .withWidth("100%")
                .withHeight("40px")
                .withDelayMillis(1000)
                .build();

        ltf.addLocationValueChangeListener(event -> {
            map.setView(createView(event.getSource().getValue().getLon(), event.getSource().getValue().getLat()));
            project.setLongitude(event.getSource().getValue().getLon());
            project.setLatitude(event.getSource().getValue().getLat());
            projectRepository.save(project);
        });

        getCardHolder().addComponents(map, new VerticalLayout(ltf));
        getCardHolder().setExpandRatio(map, 1.0f);
        return this;
    }

    protected OLMap createMap(double longitude, double latitude){
        OLMap map=new OLMap(new OLMapOptions().setShowOl3Logo(true).setInputProjection(Projections.EPSG4326));

        OLVectorSourceOptions vectorOptions=new OLVectorSourceOptions();
        OLVectorSource vectorSource=new OLVectorSource(vectorOptions);
        OLLayer layer=createLayer(createSource());
        layer.setTitle("MapQuest OSM");
        map.addLayer(layer);
        map.setView(createView(longitude,latitude));
        map.setSizeFull();
        return map;
    }

    protected OLSource createSource(){
        return new OLOSMSource();
    }

    protected OLLayer createLayer(OLSource source){
        return new OLTileLayer(source);
    }

    protected OLView createView(double xCoord, double yCoord){
        OLViewOptions opts=new OLViewOptions();
        opts.setInputProjection(Projections.EPSG4326);
        OLView view=new OLView(opts);
        view.setZoom(17);
        view.setCenter(xCoord, yCoord);
        return view;
    }
}
