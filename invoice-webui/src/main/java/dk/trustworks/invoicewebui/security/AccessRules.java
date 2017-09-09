package dk.trustworks.invoicewebui.security;

import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.RoleType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by hans on 09/09/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface AccessRules {
    public RoleType[] roleTypes();
}
