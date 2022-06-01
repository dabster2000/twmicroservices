package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.File;

import java.io.ByteArrayInputStream;


/**
 * Created by hans on 12/08/2017.
 */

public class ClientCardImpl extends ClientCardDesign {

    public ClientCardImpl(Client client, File photo) {
        if(photo!=null && photo.getFile() != null && photo.getFile().length > 0) {
            getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getFile()),
                            "logo.jpg"));
        } else {
            getImgTop().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        getImgBlackStripe().setSource(new ThemeResource("images/black-stripe.png"));

        getImgTop().setSizeFull();

        if(client.isActive()) {
            getBtnDelete().setCaption("DEACTIVATE");
            getBtnDelete().removeStyleName("friendly");
            getBtnDelete().addStyleName("danger");
        }
        if(!client.isActive()) {
            getBtnDelete().setCaption("ACTIVATE");
            getBtnDelete().removeStyleName("danger");
            getBtnDelete().addStyleName("friendly");
        }
        getLblHeading().setValue(client.getName());
    }
}
