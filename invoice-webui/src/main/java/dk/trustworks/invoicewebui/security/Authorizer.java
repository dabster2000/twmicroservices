package dk.trustworks.invoicewebui.security;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import java.util.Arrays;

/**
 * Created by hans on 18/08/2017.
 */

@Aspect
@org.springframework.stereotype.Component
public class Authorizer {

    private static final Logger log = LoggerFactory.getLogger(Authorizer.class);

    private final UserRepository userRepository;

    @Autowired
    public Authorizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void authorize(Component component, RoleType roleType) {
        log.info("Authorizer.authorize");
        log.info("component = [" + component + "], roleType = [" + roleType + "]");
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession == null) return;
        if(!userSession.hasRole(roleType)) component.getUI().getNavigator().navigateTo("login");
    }

    public boolean hasAccess(RoleType... roleTypes) {
        log.info("Authorizer.hasAccess");
        log.info("roleTypes = [" + Arrays.toString(roleTypes) + "]");
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(roleTypes == null) return true;
        if(roleTypes.length == 0) return true;
        if(userSession == null) return false;
        for (RoleType roleType : roleTypes) {
            log.debug("roleType = " + roleType);
            log.debug("userSession.hasRole(roleType) = " + userSession.hasRole(roleType));
            if (userSession.hasRole(roleType)) return true;
        }
        return false;
    }

    public boolean hasAccess(Object view) {
        log.info("Authorizer.hasAccess");
        log.info("view = [" + view + "]");
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        AccessRules accessRules = view.getClass().getAnnotation(AccessRules.class);
        if(accessRules == null) return true;
        RoleType[] roleTypes = accessRules.roleTypes();
        if(roleTypes == null) return true;
        if(roleTypes.length == 0) return true;
        if(userSession == null) return false;
        for (RoleType roleType : roleTypes) {
            if (userSession.hasRole(roleType)) return true;
        }
        return false;
    }

    @Pointcut(" execution(* dk.trustworks.invoicewebui.web.dashboard.DashboardView.init(..))")
    public void publicMethod() {
        System.out.println("Authorizer.publicMethod");
    }

    //@Around("execution(@dk.trustworks.invoicewebui.security.AccessRules * *(..)) && @annotation(accessRules)")
    //@Around("execution(public * *(..)) && @annotation(accessRules)")
    //@Around("@annotation(accessRules) && execution(* *(..))")
    @Around("publicMethod() && @annotation(accessRules) ")
    public Object hasAccess(ProceedingJoinPoint joinPoint, AccessRules accessRules) throws Throwable {
        log.info("Authorizer.hasAccess");
        log.info("joinPoint = [" + joinPoint + "], accessRules = [" + accessRules + "]");
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        RoleType[] roleTypes = accessRules.roleTypes();
        if(roleTypes == null) return joinPoint.proceed();
        if(roleTypes.length == 0) return joinPoint.proceed();
        if(userSession == null) return false;
        for (RoleType roleType : roleTypes) {
            if (userSession.hasRole(roleType)) return joinPoint.proceed();
        }
        return null;
    }

    private boolean isCookieAuthorized() {
        String NAME_COOKIE = "trustworks_login";
        Cookie cookie = getCookieByName(NAME_COOKIE);
        if (cookie != null) {
            User user = userRepository.findByUuid(cookie.getValue());
            UserSession userSession = new UserSession(user, user.getRoleList());
            VaadinSession.getCurrent().setAttribute(UserSession.class, userSession);
            return true;
        }
        return false;
    }

    private Cookie getCookieByName(String name) {
        // Fetch all cookies from the request
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();

        // Iterate to find cookie by its name
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }
}
