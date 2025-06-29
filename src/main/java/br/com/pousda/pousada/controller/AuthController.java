package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.dto.AuthRequest;
import br.com.pousda.pousada.model.Usuario;
import br.com.pousda.pousada.repository.UsuarioRepository;
import br.com.pousda.pousada.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Map<String, String> response = new HashMap<>();

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            response.put("error", "Username ou e-mail obrigatório.");
            return ResponseEntity.badRequest().body(response);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            response.put("error", "Senha obrigatória.");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getUsername().length() > 50) {
            response.put("error", "Username ou email muito grande.");
            return ResponseEntity.badRequest().body(response);
        }
        if (request.getPassword().length() > 50) {
            response.put("error", "Senha muito grande.");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseGet(() -> usuarioRepository.findByEmail(request.getUsername()).orElse(null));
        if (usuario == null) {
            response.put("error", "Usuário ou senha inválidos.");
            return ResponseEntity.status(401).body(response);
        }
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            response.put("error", "Usuário ou senha inválidos.");
            return ResponseEntity.status(401).body(response);
        }

        String token = JwtUtil.generateToken(usuario.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
