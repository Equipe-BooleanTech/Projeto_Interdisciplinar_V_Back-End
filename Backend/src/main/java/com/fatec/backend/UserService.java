package com.fatec.backend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;

import static jdk.internal.classfile.impl.DirectCodeBuilder.build;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, SecurityConfig securityConfig, AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
    }


    public UUID buscarIdPorEmail(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(UserNotFoundException::new);
        return user.getId();
    }

    public String buscarNomePorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return user.getUsername();
    }



        public UUID salvarUsuario(CreateUserDTO createUserDTO) {
            if (userRepository.findByEmail(createUserDTO.email()).isPresent()) {
                throw new RuntimeException("E-mail já cadastrado!");
            }
            User user = new User();
            user.setUsername(createUserDTO.username());
            user.setEmail(createUserDTO.email());
            user.setPassword(passwordEncoder.encode(createUserDTO.password()));
            userRepository.save(user);
            return user.getId();
        }

        public void atualizarUsuario(UUID id, UpdateUserDTO updateUserDTO) {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
            user.setEmail(updateUserDTO.email);
            user.setUsername(updateUserDTO.username);
            userRepository.save(user);
        }

        public void deletarUsuario(UUID id) {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("Usuário não encontrado!");
            }
            userRepository.deleteById(id);
        }

    public JwtTokenDTO authenticarUsuario(LoginUserDTO loginUserDTO){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginUserDTO.getEmail(),
                loginUserDTO.getPassword());}
    }
