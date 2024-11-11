package cool.cfapps.mds.infrastructure;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/login";
    private final DbUserDetailsService dbUserDetailsService;

    public SecurityService(DbUserDetailsService dbUserDetailsService) {
        this.dbUserDetailsService = dbUserDetailsService;
    }

    public void logout(String username) {

        log.info("Logout user: {}",username);
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        dbUserDetailsService.logOut(username);
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }
}
