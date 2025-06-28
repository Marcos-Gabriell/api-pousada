package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.Usuario;
import br.com.pousda.pousada.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        public String cadastrar(@RequestBody Usuario usuario) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
            return "Usuário cadastrado com sucesso!";
        }

}