package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.Usuario;
import br.com.pousda.pousada.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        boolean usernameVazio = usuario.getUsername() == null || usuario.getUsername().trim().isEmpty();
        boolean emailVazio = usuario.getEmail() == null || usuario.getEmail().trim().isEmpty();

        if (usernameVazio && emailVazio) {
            return ResponseEntity.badRequest().body("Informe o username OU e-mail!");
        }

        if (!usernameVazio && usuarioRepository.existsByUsername(usuario.getUsername())) {
            return ResponseEntity.badRequest().body("Username já cadastrado!");
        }

        if (!emailVazio && usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        if (usuario.getPassword() == null || usuario.getPassword().length() < 3) {
            return ResponseEntity.badRequest().body("A senha deve ter pelo menos 3 caracteres!");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }
}