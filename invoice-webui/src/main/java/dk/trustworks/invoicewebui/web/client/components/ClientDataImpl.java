package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.services.ProjectService;
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

import java.util.List;

/**
 * Created by hans on 13/08/2017.
 */

public class ClientDataImpl extends ClientDataDesign {

    private Binder<Clientdata> clientDataBinder;

    public ClientDataImpl(ClientdataRepository clientdataRepository, Clientdata clientdata, ProjectService projectService) {
        clientDataBinder = new Binder<>();
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

        getBtnEdit().addClickListener(event -> {
            getCssHider().setVisible(true);
            getBtnEdit().setVisible(false);
        });

        getBtnSave().addClickListener(event -> {
            try {
                clientDataBinder.writeBean(clientdata);
                if(clientdata.getUuid()!=null && !clientdata.getUuid().trim().equals("")) {
                    System.out.println("Update data");
                    clientdataRepository.save(clientdata);

                    getCssHider().setVisible(false);
                    getBtnEdit().setVisible(true);
                } else {
                    System.out.println("Create data");
                    clientdataRepository.save(clientdata);
                    ((Window)this.getParent()).close();
                }
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        getBtnDelete().addClickListener(event -> {
            List<Project> projects = projectService.findByClientdata(clientdata);
            //Resources<Resource<Project>> projects = projectClient.findByClientdatauuid(clientdata.getUuid());
            if(projects.size() > 0) {
                String description = "The contact information is in use by the following projects: \n\n";
                for (Project projectResource : projects) {
                    description += projectResource.getName()+", \n";
                }
                Notification sample = new Notification("Can't delete contact information", description);
                sample.show(Page.getCurrent());
            } else {

            clientdataRepository.delete(clientdata.getUuid());
            clientdata.setClientname("DELETED");
            clientdata.setContactperson("");
            clientdata.setCity("");
            clientdata.setCvr("");
            clientdata.setEan("");
            clientdata.setOtheraddressinfo("");
            clientdata.setPostalcode(0L);
            clientdata.setStreetnamenumber("");
            clientDataBinder.readBean(clientdata);
            getCssHider().setVisible(false);
            getBtnEdit().setVisible(false);
           }
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
