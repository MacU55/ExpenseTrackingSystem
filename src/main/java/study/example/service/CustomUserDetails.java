package study.example.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.example.AppController;
import study.example.model.Role;
import study.example.model.User;

import javax.xml.transform.Result;
@Component
public class CustomUserDetails implements UserDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetails.class);


    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        EnumSet<Role> roles = user.getRoleSet();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getLabel()));
        }
        return authorities;
    }

    public boolean hasRole(String roleName) {
        return this.user.hasRoleFromUserEntity(roleName);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Role getUserRole(){ return user.getRole(); }

    public String getUserEmail(){ return user.getEmail(); }


    public String getUserLabel(){return user.getLabel();}



}

