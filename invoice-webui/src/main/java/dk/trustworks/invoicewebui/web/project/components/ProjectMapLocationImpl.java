package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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
@SpringComponent
@SpringUI
public class ProjectMapLocationImpl extends ProjectMapLocationDesign {

    protected OLMap map;

    public ProjectMapLocationImpl() {

        map=createMap();
        //GeocodedLocation geocodedLocation = GeocodedLocation.newBuilder().withGeocodedAddress("Ålegårdsvænget 14, 2791 Dragør").build();

        final LocationTextField<GeocodedLocation> ltf = LocationTextField.newBuilder()
                .withCaption("Main project location:")
                .withDelayMillis(1200)
                .withLocationProvider(OpenStreetMapGeocoder.getInstance())
                .withMinimumQueryCharacters(5)
                .withWidth("100%")
                .withHeight("40px")
                .withDelayMillis(1000)
                .build();

        ltf.addLocationValueChangeListener(event -> {
            map.setView(createView(event.getSource().getValue().getLon(), event.getSource().getValue().getLat()));
        });

        getCardHolder().addComponents(map, new VerticalLayout(ltf));
        getCardHolder().setExpandRatio(map, 1.0f);
    }

    protected OLMap createMap(){
        OLMap map=new OLMap(new OLMapOptions().setShowOl3Logo(true).setInputProjection(Projections.EPSG4326));

        OLVectorSourceOptions vectorOptions=new OLVectorSourceOptions();
        OLVectorSource vectorSource=new OLVectorSource(vectorOptions);
        OLLayer layer=createLayer(createSource());
        layer.setTitle("MapQuest OSM");
        map.addLayer(layer);
        map.setView(createView(12.589604000000008,55.707043));
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
