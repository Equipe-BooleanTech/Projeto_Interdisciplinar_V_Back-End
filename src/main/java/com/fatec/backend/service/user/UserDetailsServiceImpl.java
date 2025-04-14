package com.fatec.backend.service.user;

import com.fatec.backend.model.User.User;
import com.fatec.backend.model.User.UserDetailsImpl;
import com.fatec.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User modelUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserDetailsImpl(modelUser);
    }
}