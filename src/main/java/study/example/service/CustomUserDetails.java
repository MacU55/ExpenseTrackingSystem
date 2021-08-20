package study.example.service;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import study.example.model.Role;
import study.example.model.User;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }
// original
    /*
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

     */

    //
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
        return this.user.hasRole(roleName);
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

}

