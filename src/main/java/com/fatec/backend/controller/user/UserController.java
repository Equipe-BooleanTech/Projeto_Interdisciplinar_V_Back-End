package com.fatec.backend.controller.user;

import com.fatec.backend.DTO.auth.LoginResponseDTO;
import com.fatec.backend.DTO.auth.LoginUserDTO;
import com.fatec.backend.DTO.user.CreateUserDTO;
import com.fatec.backend.DTO.auth.JwtTokenDTO;
import com.fatec.backend.DTO.user.PasswordDTO;
import com.fatec.backend.DTO.user.UpdateUserDTO;
import com.fatec.backend.DTO.user.UserDTO;
import com.fatec.backend.exception.UserNotFoundException;
import com.fatec.backend.model.User;
import com.fatec.backend.response.SuccessResponse;
import com.fatec.backend.response.UpdateResponse;
import com.fatec.backend.service.auth.JwtTokenService;
import com.fatec.backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody @Valid LoginUserDTO loginUserDTO) {
        try {
            JwtTokenDTO tokenDTO = userService.authenticarUsuario(loginUserDTO);
            UUID userId = userService.buscarIdPorEmail(loginUserDTO.email());
            String fullName = userService.buscarNomePorEmail(loginUserDTO.email());
            LoginResponseDTO response = new LoginResponseDTO(userId, fullName, tokenDTO.token());
            return ResponseEntity.ok().body(response);

        } catch (UserNotFoundException e) {
            throw e;
        }
    }


    @PostMapping("/create-user")
    public ResponseEntity<SuccessResponse> salvarUsuario(@RequestBody @Valid CreateUserDTO createUserDTO) {
        UUID id = userService.salvarUsuario(createUserDTO);
        SuccessResponse response = new SuccessResponse("USUÁRIO CRIADO COM SUCESSO!", id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }



    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateResponse> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        userService.atualizarUsuario(id, updateUserDTO);
        UpdateResponse response = new UpdateResponse("USUÁRIO ATUALIZADO COM SUCESSO!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse> removerUsuario(@PathVariable UUID id) {
        userService.deletarUsuario(id);
        SuccessResponse response = new SuccessResponse("USUÁRIO DELETADO COM SUCESSO!", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listall-users")
    public ResponseEntity<Page<UserDTO>> listAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> vehicles = userService.listUsers(PageRequest.of(page, size));
        return new ResponseEntity<>(vehicles,HttpStatus.FOUND);
    }

    @GetMapping("/list-by-id/{id}")
    public ResponseEntity<?> listUserById(@PathVariable UUID id) {
        Optional<User> user = Optional.ofNullable(userService.findById(id));
        if(user.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(user.get());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
        }
    }

    @PutMapping("/upload-image/{id}")
    public ResponseEntity<?> uploadImagem(@PathVariable UUID id, @RequestParam("imagem") MultipartFile file) throws IOException {
        User usuario = userService.uploadImagem(id, file);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Imagem atualizada com sucesso!");
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> solicitarRecuperacaoSenha(@RequestBody String email) {
        try {
            userService.solicitarRecuperacaoSenha(email);
            return ResponseEntity.ok("Email de recuperação enviado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody String token, @RequestBody String novaSenha) {
        try {
            userService.redefinirSenhaEsquecida(token, novaSenha);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/get-token/{id}")
    public ResponseEntity<?> getToken(@PathVariable UUID id, @RequestBody LoginUserDTO loginUserDTO) {
        User user = userService.findById(id);
        JwtTokenDTO tokenDTO = userService.authenticarUsuario(loginUserDTO);
        String token = tokenDTO.token();
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        boolean isValid = jwtTokenService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token válido.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }
    }


    @PutMapping("/redefinir-senha/{id}")
    public ResponseEntity<?> setPassword(@PathVariable UUID id, @RequestBody PasswordDTO dto) {
        User user = userService.findById(id);
        userService.redefinirSenha(id,dto);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}
