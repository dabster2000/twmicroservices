package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.services.ProjectService;

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
                    //clientManager.createClientDetailsView(clientdata.getClient());
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
}
