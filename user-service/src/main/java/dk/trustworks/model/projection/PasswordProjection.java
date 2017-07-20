package dk.trustworks.model.projection;

import dk.trustworks.model.User;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by hans on 23/06/2017.
 */
@Projection(name = "passwords", types = { User.class })
public interface PasswordProjection {

    String getPassword();

}
