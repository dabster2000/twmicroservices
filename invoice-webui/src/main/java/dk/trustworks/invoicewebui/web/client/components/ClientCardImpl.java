package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Client;

import java.io.ByteArrayInputStream;


/**
 * Created by hans on 12/08/2017.
 */

@SpringComponent
@SpringUI
public class ClientCardImpl extends ClientCardDesign {

    public ClientCardImpl(Client client) {

        if(client.getLogo()!=null && client.getLogo().getLogo().length > 0) {
            getImgTop().setSource(new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(client.getLogo().getLogo()),
                            "logo.jpg"));
        } else {
            getImgTop().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        getImgBlackStripe().setSource(new ThemeResource("images/black-stripe.png"));

        getImgTop().setSizeFull();
        getLblHeading().setValue(client.getName());
    }

}
