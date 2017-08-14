package dk.trustworks.model.projections;

import dk.trustworks.model.Client;
import dk.trustworks.model.Clientdata;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * Created by hans on 25/06/2017.
 */
@Projection(name = "no_logo", types = { Client.class })
public interface ClientNoLogoProjection {

    String getUuid();
    String getName();
    String getActive();
    String getContactname();
    java.sql.Timestamp getCreated();
    Double getLatitude();
    Double getLongitude();
    List<Clientdata> getClientdata();

}
