package study.example.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import study.example.AppController;
import study.example.service.CustomUserDetails;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessHandler.class);

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl( Authentication authentication) throws IOException {
        String url ="/login.html?error=true";
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Fetch the roles from Authentication object
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        GrantedAuthority b = (GrantedAuthority) authentication.getAuthorities();
//        b.getAuthority();

//        String role = (String) authentication.getAuthorities();
        List<String> roles = new ArrayList<>();
        for(GrantedAuthority a : authorities){
            roles.add(a.getAuthority());
        }


//        String redirectURL = request.getContextPath();

        if (userDetails.getUserLabel().equals("Employee")) {
            url = "/expense_list";

        } else if (userDetails.getUserLabel().equals("Manager")) {
            url = "/users";
        }
        LOGGER.info("User's email is: "+ userDetails.getUserEmail()+ "and " + "User's role is: " + userDetails.getUserRole());
        LOGGER.info("user's roleName: " + userDetails.getUserRole());
        LOGGER.info("URL is: " + url);
        LOGGER.info("authentication " + authentication);
        return url;

    }

}