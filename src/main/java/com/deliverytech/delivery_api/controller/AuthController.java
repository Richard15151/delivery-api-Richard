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

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@org.springframework.web.bind.annotation.RequestBody LoginRequestDTO request){
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@org.springframework.web.bind.annotation.RequestBody LoginRequestDTO request) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(request.getEmail());

        if (usuarioOpt.isPresent() && passwordEncoder.matches(request.getSenha(), usuarioOpt.get().getSenha())) {
            String token = jwtUtil.generateToken(usuarioOpt.get());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
    }
}