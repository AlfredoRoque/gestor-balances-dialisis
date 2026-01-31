package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.JwtResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.LoginRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Usuario;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtUtil;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        // validar usuario/contraseña
        Usuario user = usuarioService.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        String token = jwtUtil.generarToken(request.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
