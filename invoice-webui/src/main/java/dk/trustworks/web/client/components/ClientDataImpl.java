package dk.trustworks.web.client.components;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.network.clients.ClientdataClient;
import dk.trustworks.network.dto.Clientdata;
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

public class ClientDataImpl extends ClientDataDesign {

    protected OLMap map;

    public ClientDataImpl(ClientdataClient clientdataClient, Clientdata clientdata) {
        System.out.println("ClientDataImpl.ClientDataImpl");
        System.out.println("clientdata = [" + clientdata + "]");
        Binder<Clientdata> clientDataBinder = new Binder<>();
        clientDataBinder.forField(getTxtCity()).bind(Clientdata::getCity, Clientdata::setCity);
        clientDataBinder.forField(getTxtContactName()).bind(Clientdata::getContactperson, Clientdata::setContactperson);
        clientDataBinder.forField(getTxtCvr()).bind(Clientdata::getCvr, Clientdata::setCvr);
        clientDataBinder.forField(getTxtEan()).bind(Clientdata::getEan, Clientdata::setEan);
        clientDataBinder.forField(getTxtName()).bind(Clientdata::getClientname, Clientdata::setClientname);
        clientDataBinder.forField(getTxtOther()).bind(Clientdata::getOtheraddressinfo, Clientdata::setOtheraddressinfo);
        clientDataBinder.forField(getTxtPostalCode())
                .withConverter(
                new StringToLongConverter("Must enter a number"))
                .bind(Clientdata::getPostalcode, Clientdata::setPostalcode);
        clientDataBinder.forField(getTxtStreetname()).bind(Clientdata::getStreetnamenumber, Clientdata::setStreetnamenumber);
        clientDataBinder.readBean(clientdata);

        map = createMap();

        final LocationTextField<GeocodedLocation> ltf = LocationTextField.newBuilder()
                .withCaption("Map:")
                .withDelayMillis(1200)
                .withLocationProvider(OpenStreetMapGeocoder.getInstance())
                .withMinimumQueryCharacters(5)
                .withWidth("100%")
                .withHeight("40px")
                .build();

        ltf.addLocationValueChangeListener(event -> {
            GeocodedLocation location = event.getSource().getValue();
            map.setView(createView(location.getLon(), location.getLat()));
            //getTxtCity().setValue(location.getAdministrativeAreaLevel2());
            //getTxtPostalCode().setValue(location.getPostalCode());
            //getTxtStreetname().setValue(location.getRoute()+" "+location.getStreetNumber());
            try {
                clientDataBinder.writeBean(clientdata);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        getMapHolder().addComponent(map);

        getFormLayout().addComponents(ltf);

        getBtnEdit().addClickListener(event -> {
            getCssHider().setVisible(true);
            getBtnEdit().setVisible(false);
        });

        getBtnSave().addClickListener(event -> {
            clientdataClient.save(clientdata.getUuid(), clientdata);
            getCssHider().setVisible(false);
            getBtnEdit().setVisible(true);
        });

        getBtnDelete().addClickListener(event -> {
            getCssHider().setVisible(false);
            getBtnEdit().setVisible(true);
        });
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
