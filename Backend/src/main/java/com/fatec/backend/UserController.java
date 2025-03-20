package com.fatec.backend;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody @Valid LoginUserDTO loginUserDTO) {
        try {
            JwtTokenDTO tokenDTO = userService.authenticarUsuario(loginUserDTO);
            UUID userId = userService.buscarIdPorEmail(loginUserDTO.getEmail());
            String username = userService.buscarNomePorEmail(loginUserDTO.getUsername());
            LoginResponseDTO response = new LoginResponseDTO(userId,username, tokenDTO.token());
            return ResponseEntity.ok().body(response);

        } catch (UserNotFoundException e) {
            throw e;
        }
    }


    @PostMapping("/create-complete")
    public ResponseEntity<SuccessResponse> salvarUsuario(@RequestBody @Valid CreateUserDTO createUserDTO) {
        try {
            UUID id = userService.salvarUsuario(createUserDTO);
            SuccessResponse response = new SuccessResponse("USUÁRIO CRIADO COM SUCESSO!", id);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            throw e;
        }

    }



    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateResponse> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        try {
            userService.atualizarUsuario(id, updateUserDTO);
            UpdateResponse response = new UpdateResponse("USUÁRIO ATUALIZADO COM SUCESSO!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw e;
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse> removerUsuario(@PathVariable UUID id) {
        try {
            userService.deletarUsuario(id);
            SuccessResponse response = new SuccessResponse("USUÁRIO DELETADO COM SUCESSO!", id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            throw e;
        }
    }




}
