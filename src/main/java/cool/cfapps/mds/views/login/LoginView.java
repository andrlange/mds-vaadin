package cool.cfapps.mds.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Route("login")
@PageTitle("Demo Login")
@AnonymousAllowed
@Slf4j
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();


    public LoginView(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder,
                     HttpServletRequest request, HttpServletResponse response) {

        log.info("Locale from VaadinService: {}", VaadinService.getCurrentRequest().getLocale());
        loginForm = new LoginForm();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        //loginForm.setAction("login");

        loginForm.addLoginListener(event -> {
            log.info("Login {} : {}", event.getUsername(), event.getPassword());
            String decoded = event.getUsername() + ":" + event.getPassword();
            String base64user =
                    "$base64$Basic " + Base64.getEncoder().encodeToString(decoded.getBytes());

            try {
                // Fetch user details from the database
                UserDetails user = userDetailsManager.loadUserByUsername(base64user);

                // Compare the raw password with the hashed password from the database
                if (passwordEncoder.matches(event.getPassword(), user.getPassword())) {
                    // Authentication successful

                    List<GrantedAuthority> authorities = new ArrayList<>();
                   user.getAuthorities().forEach(authority -> {
                       log.info("User has role: {}", authority.getAuthority());
                       authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
                   });


                    Authentication authentication
                            = new UsernamePasswordAuthenticationToken(event.getUsername(), event.getPassword(),
                            authorities);
                    VaadinAwareSecurityContextHolderStrategy strategy = new VaadinAwareSecurityContextHolderStrategy();

                    SecurityContext context = strategy.createEmptyContext();
                    context.setAuthentication(authentication);
                    strategy.setContext(context);
                    securityContextRepository.saveContext(context, request, response);


                    getUI().ifPresent(ui -> ui.navigate("welcome"));
                } else {
                    // Password mismatch
                    getUI().ifPresent(ui -> ui.navigate("login?error"));
                }
            } catch (Exception e) {
                // Handle exceptions (e.g., user not found)
                getUI().ifPresent(ui -> ui.navigate("login?error"));
            }


        });

        add(new H1("Demo"), new Div("Anmeldung"), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
