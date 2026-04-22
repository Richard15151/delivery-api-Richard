package com.deliverytech.delivery_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.LoginRequestDTO;
import com.deliverytech.delivery_api.dto.responses.LoginResponseDTO;
import com.deliverytech.delivery_api.enums.Role;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.deliverytech.delivery_api.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {
    
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.repository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
        summary = "Registrar um novo usuário", 
        description = "Cria um novo acesso ao sistema. Por padrão, se não for informada uma função, o usuário será criado como CLIENTE."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso e logado", 
                     content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "E-mail já cadastrado no sistema"),
        @ApiResponse(responseCode = "403", description = "Tentativa de criar um usuário com função ADMIN não permitida")
    })
    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@RequestBody LoginRequestDTO request){
        if(repository.existsByEmail(request.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail já cadastrado.");
        }

        if(request.getRole() == Role.ADMIN){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não é permitido criar usuários com função ADMIN.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole() != null ? request.getRole() : Role.CLIENTE);
        usuario.setAtivo(true);

        repository.save(usuario);
        String token = jwtUtil.generateToken(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(token));
    }

    @Operation(
        summary = "Realizar login", 
        description = "Autentica o usuário e retorna um token JWT para acesso aos endpoints protegidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", 
                     content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(request.getEmail());

        if (usuarioOpt.isPresent() && passwordEncoder.matches(request.getSenha(), usuarioOpt.get().getSenha())) {
            String token = jwtUtil.generateToken(usuarioOpt.get());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
    }
}