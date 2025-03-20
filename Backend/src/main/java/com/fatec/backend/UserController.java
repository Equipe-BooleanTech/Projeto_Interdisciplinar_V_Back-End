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
            String fullName = userService.buscarNomePorEmail(loginUserDTO.getUsername());
            LoginResponseDTO response = new LoginResponseDTO(userId, tokenDTO.token());
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
        } catch (UserCreationFailedException e) {
            throw e;
        }

    }



    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateResponse> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        try {
            userService.atualizarUsuario(id, updateUserDTO);
            UpdateResponse response = new UpdateResponse("USUÁRIO ATUALIZADO COM SUCESSO!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (UserUpdateFailedException e){
            throw  e;
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse> removerUsuario(@PathVariable UUID id) {
        try {
            userService.deletarUsuario(id);
            SuccessResponse response = new SuccessResponse("USUÁRIO DELETADO COM SUCESSO!", id);
            return ResponseEntity.noContent().build();
        }catch (UserDeletionFailedException e){
            throw e;
        }
    }


    @PutMapping("/update-password")
    public ResponseEntity<String> mudarSenha(@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
        userService.mudarSenha(updatePasswordDTO);
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }


    @GetMapping("/get-users")
    public ResponseEntity<Page<UserDTO>> listarTodosUsuarios(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> users = (Page<UserDTO>) userService.listarTodosUsuarios(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }
    @GetMapping("/get-users-by-id/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try{
            Optional<User> user = userService.listarPeloId(id);

            if (user.isPresent()) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("USUÁRIO NÃO ENCONTRADO");
            }}catch (UserNotFoundException e){
            throw e;
        }
    }




    @PostMapping("/list-users-by-period")
    public ResponseEntity<?> listarUsuariosPorPeriodo(
            @RequestBody DateRangeDTO dateRangeDTO,
            @RequestParam(defaultValue = "monthly") String groupingType) {
        TimeSummaryDTO usuariosPorPeriodo = userService.listarUsuariosPorPeriodo(dateRangeDTO);
        if (usuariosPorPeriodo.data().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NENHUM USUARIO ENCONTRADO NO PERÍODO ESPECIFICADO");
        } else {
            return ResponseEntity.ok(usuariosPorPeriodo);
        }
    }

}
