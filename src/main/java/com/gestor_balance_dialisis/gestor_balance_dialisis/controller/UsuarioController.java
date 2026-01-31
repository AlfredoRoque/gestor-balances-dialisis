package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Usuario;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/save")
    public Usuario saveUser(@RequestBody Usuario user) {
        return usuarioService.save(user);
    }
}
