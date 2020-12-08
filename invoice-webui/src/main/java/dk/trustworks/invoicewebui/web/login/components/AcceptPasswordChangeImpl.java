package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.network.rest.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 12/08/2017.
 */
public class AcceptPasswordChangeImpl extends AcceptPasswordChangeDesign {

    protected static Logger logger = LoggerFactory.getLogger(AcceptPasswordChangeImpl.class.getName());

    public AcceptPasswordChangeImpl() {
        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
    }

}
