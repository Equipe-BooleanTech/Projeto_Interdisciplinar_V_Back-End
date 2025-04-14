package com.fatec.backend.model.User;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
public class UserDetailsImpl implements UserDetails {

    private User modelUser;

    public UserDetailsImpl(User modelUser) {
        this.modelUser = modelUser;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna uma única autoridade baseada no role do usuário
        return List.of(new SimpleGrantedAuthority(modelUser.getName()));
    }


    @Override
    public String getPassword() {
        return modelUser.getPassword();
    }

    @Override
    public String getUsername() {
        return modelUser.getUsername();
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
}