package cool.cfapps.mds.views.welcome;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cool.cfapps.mds.demo.DemoData;
import cool.cfapps.mds.demo.DemoDataService;
import cool.cfapps.mds.infrastructure.SecurityService;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;


@Route("welcome")
@PageTitle("Welcome")
@PermitAll
@Slf4j
public class WelcomeView extends VerticalLayout {

    public WelcomeView(SecurityService securityService, AuthenticationContext authenticationContext, DemoDataService dataService) {
        log.info("Welcome view loaded for user: {}", authenticationContext.getPrincipalName());
        add(new Div("Welcome to the logged in System " + authenticationContext.getPrincipalName().get() + " type:" + dataService.type() + "!"));

        Grid<DemoData> grid = new Grid<>(DemoData.class, false);

        grid.addColumn(DemoData::getId).setHeader("ID");
        grid.addColumn(DemoData::getField1).setHeader("Field 1");
        grid.addColumn(DemoData::getField2).setHeader("Field 2");
        grid.setHeight("200px");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setItems(dataService.fetchDemoData());

        log.info("Welcome view initialized roles: {}", authenticationContext.getGrantedRoles());
        if(authenticationContext.hasAllRoles("ADMIN")) {
            Button adminButton = new Button("Admin Only Button");
            add(adminButton);
            adminButton.addClickListener(event -> {
                Notification.show("Admin Only Button Clicked!",3000, Notification.Position.MIDDLE);
            });
        }

        if(authenticationContext.hasAllRoles("USER")) {
            Button userButton = new Button("User Button");
            add(userButton);
            userButton.addClickListener(event -> {
                Notification.show("User Button Clicked!",3000, Notification.Position.MIDDLE);
            });
        }

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(event -> {
            if (authenticationContext.getPrincipalName() != null && authenticationContext.getPrincipalName().isPresent()) {
                securityService.logout(authenticationContext.getPrincipalName().get());
            } else {
                log.info("User is not logged in");
            }
        });
        add(grid, logoutButton);
    }
}
