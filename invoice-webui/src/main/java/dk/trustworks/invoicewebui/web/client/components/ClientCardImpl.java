package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.network.dto.Client;
import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.OLMapOptions;
import org.vaadin.addon.vol3.OLView;
import org.vaadin.addon.vol3.OLViewOptions;
import org.vaadin.addon.vol3.client.Projections;
import org.vaadin.addon.vol3.client.style.OLIconStyle;
import org.vaadin.addon.vol3.client.style.OLStyle;
import org.vaadin.addon.vol3.feature.OLFeature;
import org.vaadin.addon.vol3.feature.OLPoint;
import org.vaadin.addon.vol3.layer.OLLayer;
import org.vaadin.addon.vol3.layer.OLTileLayer;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLOSMSource;
import org.vaadin.addon.vol3.source.OLSource;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;

import java.io.ByteArrayInputStream;


/**
 * Created by hans on 12/08/2017.
 */

@SpringComponent
@SpringUI
public class ClientCardImpl extends ClientCardDesign {

    protected OLMap map;
    protected OLVectorLayer vectorLayer;

    public ClientCardImpl(Client client) {

        if(client.getLogo()!=null && client.getLogo().length > 0) {
            getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(client.getLogo()),
                            "logo.jpg"));
        } else {
            getImgTop().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        getImgBlackStripe().setSource(new ThemeResource("images/black-stripe.png"));

        //getImgTop().setSource(new ThemeResource("images/clients/appension.jpg"));
        getImgTop().setSizeFull();
        getLblHeading().setValue(client.getName());
        //getLblContact().setValue(contactName);
        //map=createMap(client.getLongitude(), client.getLatitude());
        //getCssMapHolder().addComponent(map);
    }

    protected OLMap createMap(double longitude, double latitude){
        OLMap map=new OLMap(new OLMapOptions().setShowOl3Logo(true).setInputProjection(Projections.EPSG4326));

        OLVectorSourceOptions vectorOptions=new OLVectorSourceOptions();
        OLVectorSource vectorSource=new OLVectorSource(vectorOptions);
        vectorSource.addFeature(createPointFeature("APPension", longitude, latitude));
        vectorLayer=new OLVectorLayer(vectorSource);
        vectorLayer.setLayerVisible(true);
        map.addLayer(vectorLayer);

        OLLayer layer=createLayer(createSource());
        layer.setTitle("MapQuest OSM");
        map.addLayer(layer);
        map.setView(createView(longitude, latitude));
        map.setSizeFull();
        return map;
    }

    protected OLSource createSource(){
        return new OLOSMSource();
    }

    protected OLLayer createLayer(OLSource source){
        return new OLTileLayer(source);
    }

    protected OLView createView(double longitude, double latitude){
        OLViewOptions opts=new OLViewOptions();
        opts.setInputProjection(Projections.EPSG4326);
        OLView view=new OLView(opts);
        view.setZoom(17);
        view.setCenter(longitude, latitude);
        return view;
    }

    protected OLFeature createPointFeature(String id, double x, double y) {
        OLFeature testFeature=new OLFeature(id);
        testFeature.setStyle(new OLStyle());
        testFeature.setGeometry(new OLPoint(x,y));
        OLFeature pointFeature = testFeature;
        OLStyle style= new OLStyle();
        style.iconStyle=new OLIconStyle();
        style.iconStyle.size=new double[]{16.0,16.0};
        style.iconStyle.src = "VAADIN/img/hans.lassen.png";

        pointFeature.setStyle(style);
        return pointFeature;
    }


}
