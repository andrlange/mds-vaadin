package cool.cfapps.mds.infrastructure;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import cool.cfapps.mds.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig extends VaadinWebSecurity {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/login").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/logout").permitAll())
        //.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        .httpBasic(Customizer.withDefaults());  // Enables HTTP Basic Authentication

        super.configure(http);
        setLoginView(http, LoginView.class);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }
}
