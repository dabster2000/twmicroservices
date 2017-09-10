package dk.trustworks.invoicewebui.security;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

/**
 * Created by hans on 18/08/2017.
 */

@Aspect
@Service
public class Authorizer {
    public void authorize(Component component, RoleType roleType) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession == null) component.getUI().getNavigator().navigateTo("login");
        if(userSession != null && !userSession.hasRole(roleType)) component.getUI().getNavigator().navigateTo("login");
    }

    public boolean hasAccess(RoleType... roleTypes) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(roleTypes == null) return true;
        if(roleTypes.length == 0) return true;
        if(userSession != null) {
            for (RoleType roleType : roleTypes) {
                if (userSession.hasRole(roleType)) return true;
            }
        }
        return false;
    }

    public boolean hasAccess(Object view) {
        //System.out.println("view.getClass() = " + view.getClass());
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        AccessRules accessRules = view.getClass().getAnnotation(AccessRules.class);
        //System.out.println("accessRules = " + accessRules);
        if(accessRules == null) return true;
        RoleType[] roleTypes = accessRules.roleTypes();
        //System.out.println("roleTypes = " + roleTypes);
        if(roleTypes == null) return true;
        //System.out.println("roleTypes.length = " + roleTypes.length);
        if(roleTypes.length == 0) return true;
        if(userSession != null) {
            //System.out.println("userSession = " + userSession);
            for (RoleType roleType : roleTypes) {
                //System.out.println("roleType = " + roleType);
                if (userSession.hasRole(roleType)) return true;
            }
        }
        return false;
    }

    @Around("execution(@dk.trustworks.invoicewebui.security.AccessRules * *(..)) && @annotation(accessRules)")
    public Object hasAccess(ProceedingJoinPoint joinPoint, AccessRules accessRules) throws Throwable {
        System.out.println("Authorizer.hasAccess");
        System.out.println("joinPoint = [" + joinPoint + "], accessRules = [" + accessRules + "]");
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        RoleType[] roleTypes = accessRules.roleTypes();
        System.out.println("roleTypes = " + roleTypes);
        if(roleTypes == null) return joinPoint.proceed();
        System.out.println("roleTypes.length = " + roleTypes.length);
        if(roleTypes.length == 0) return joinPoint.proceed();
        if(userSession != null) {
            System.out.println("userSession = " + userSession);
            for (RoleType roleType : roleTypes) {
                System.out.println("roleType = " + roleType);
                if (userSession.hasRole(roleType)) return joinPoint.proceed();
            }
        }
        return null;
    }
}
