package com.springecommerce.ecommerce.controller;

import com.springecommerce.ecommerce.model.Producto;
import com.springecommerce.ecommerce.model.Usuario;
import com.springecommerce.ecommerce.service.IProductoService;
import com.springecommerce.ecommerce.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller @RequestMapping("/administrador")
public class AministradorController {
    @Autowired
    private IProductoService iProductoService;

    @Autowired
    private IUsuarioService iUsuarioService;

    //Vista de los productos
    @GetMapping("")
    public String productos(Model model){
        List<Producto> productos = iProductoService.finAll();

        model.addAttribute("productos", productos);
        return "administrador/home";
    }

    @GetMapping("/users")
    public String users(Model model){
        List<Usuario> usuarios = iUsuarioService.findAll();

        model.addAttribute("usuarios", usuarios);
        return "administrador/usuarios";
    }


}
