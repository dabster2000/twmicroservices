package dk.trustworks.model.projection;

import dk.trustworks.model.Salary;
import dk.trustworks.model.User;
import dk.trustworks.model.UserStatus;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;

/**
 * Created by hans on 25/06/2017.
 */
@Projection(name = "details", types = { User.class })
public interface DetailsProjection {

    Date getCreated();

    String getEmail();

    String getFirstname();

    String getLastname();

    String getPassword();

    String getUsername();

    String getSlackusername();

    List<UserStatus> getStatuses();

}
