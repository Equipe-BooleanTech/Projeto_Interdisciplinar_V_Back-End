package com.fatec.backend.service.user;


import com.fatec.backend.DTO.user.*;
import com.fatec.backend.DTO.auth.JwtTokenDTO;
import com.fatec.backend.DTO.auth.LoginUserDTO;
import com.fatec.backend.exception.InvalidCredentialsException;
import com.fatec.backend.exception.UserNotFoundException;
import com.fatec.backend.mapper.user.UserMapper;
import com.fatec.backend.model.UserDetailsImpl;
import com.fatec.backend.repository.UserRepository;
import com.fatec.backend.configuration.SecurityConfig;
import com.fatec.backend.model.User;
import com.fatec.backend.service.auth.JwtTokenService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public UUID buscarIdPorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return user.getId();
    }

    public String buscarNomePorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return user.getName() + " " + user.getLastname();
    }



        public UUID salvarUsuario(CreateUserDTO createUserDTO) {
            if (userRepository.findByEmail(createUserDTO.email()).isPresent()) {
                throw new RuntimeException("E-mail já cadastrado!");
            }
            User user = User.builder()
                    .email(createUserDTO.email())
                    .username(createUserDTO.username())
                    .password(passwordEncoder.encode(createUserDTO.password()))
                    .name(createUserDTO.name())
                    .lastname(createUserDTO.lastname())
                    .Phone(createUserDTO.phone())
                    .birthdate(createUserDTO.birthdate())
                    .createdAt(LocalDateTime.now())
                    .image(null)
                    .build();

            return userRepository.save(user).getId();
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
                loginUserDTO.email(),
                loginUserDTO.password());
        try {
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            UserDetailsImpl modelUserDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtTokenService.generateToken(modelUserDetails);
            return new JwtTokenDTO(token);

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("FALHA NA AUTENTICAÇÃO: CREDENCIAIS INVÁLIDAS");
        } catch (Exception e) {
            throw new RuntimeException("ERRO INESPERADO NA AUTENTICAÇÃO: " + e.getMessage());
        }
    }

    public Page<UserDTO> listUsers(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
                .map(UserMapper.INSTANCE::toUserDTO);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateUser(UUID userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (userUpdateDTO.username() != null && !userUpdateDTO.username().isBlank()) {
            // Consider adding validation for username uniqueness if it's a requirement
            user.setUsername(userUpdateDTO.username());
        }
        if (userUpdateDTO.name() != null && !userUpdateDTO.name().isBlank()) {
            user.setName(userUpdateDTO.name());
        }
        if (userUpdateDTO.lastname() != null && !userUpdateDTO.lastname().isBlank()) {
            user.setLastname(userUpdateDTO.lastname());
        }
        if (userUpdateDTO.phone() != null && !userUpdateDTO.phone().isBlank()) {
            user.setPhone(userUpdateDTO.phone());
        }
        return userRepository.save(user);
    }

    public User uploadImagem(UUID id, MultipartFile file) throws IOException {
        User user =userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user == null) {
            return null;
        }
        user.setImage(file.getBytes());
        return userRepository.save(user);
    }


    public byte[] getImagem(UUID id) {
        User user =userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user == null || user.getImage() == null) {
            return null;
        }
        return user.getImage();
    }

    public void solicitarRecuperacaoSenha(String email) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new IllegalArgumentException("Usuário não encontrado com este email.");

        String token = jwtTokenService.generatePasswordResetToken(email);
        String link = "http://localhost:8080/api/users/resetar-senha?token=" + token;

        Context context = new Context();
        context.setVariable("nome", user.get().getName());
        context.setVariable("link", link);

        String htmlBody = templateEngine.process("password-recovery", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(user.get().getEmail());
        helper.setSubject("Recuperação de Senha");
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    public void redefinirSenhaEsquecida(String token, String password) {
        String email = jwtTokenService.verifyPasswordResetToken(token);
        Optional<User> usuario = userRepository.findByEmail(email);
        if (usuario.isEmpty()) throw new IllegalArgumentException("Usuário não encontrado com este token.");
        usuario.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(usuario.get());
    }

    public void redefinirSenha(UUID id, PasswordDTO passwordDTO) {
        Optional<User> usuario = userRepository.findById(id);
        if (usuario.isEmpty()) throw new IllegalArgumentException("User not found");
        if(usuario.get().getPassword().equals(passwordEncoder.encode(passwordDTO.oldPassword()))){
            usuario.get().setPassword(passwordEncoder.encode(passwordDTO.newPassword()));
        }else {
            throw new RuntimeException("Senha incorreta");
        }
    }
    }
