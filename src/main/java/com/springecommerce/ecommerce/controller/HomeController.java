package com.springecommerce.ecommerce.controller;

import com.springecommerce.ecommerce.model.DetalleOrden;
import com.springecommerce.ecommerce.model.Orden;
import com.springecommerce.ecommerce.model.Producto;
import com.springecommerce.ecommerce.model.Usuario;
import com.springecommerce.ecommerce.service.IDetalleOrdenService;
import com.springecommerce.ecommerce.service.IOrdenService;
import com.springecommerce.ecommerce.service.IProductoService;
import com.springecommerce.ecommerce.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private IProductoService iProductoService;
    @Autowired
    private IUsuarioService iUsuarioService;
    @Autowired
    private IOrdenService iOrdenService;
    @Autowired
    private IDetalleOrdenService iDetalleOrdenService;

    //Para el carrito
    List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

    Orden orden = new Orden();

    //Vista home
    @GetMapping("/")
    public String Home(Model model, HttpSession httpSession){

        model.addAttribute("productos", iProductoService.findAll());
        model.addAttribute("idUsuario", httpSession.getAttribute("idUsuario"));
        return "usuario/home";
    }

    //Vista de un producto en particular
    @GetMapping("/productohome/{id}")
    public String productoHome(Model model ,@PathVariable Integer id){
        Producto producto = new Producto();
        producto = iProductoService.get(id).get();
        model.addAttribute("producto", producto);
        return  "usuario/productohome";
    }

    //Añadir producto al carrito y mostrar vista del carrito
    @PostMapping("/carrito")
    public String addCart(@RequestParam("id") Integer id, @RequestParam("cantidad") Integer cantidad, Model model){

        DetalleOrden detalleOrden = new DetalleOrden();
        Producto producto = new Producto();
        double total = 0;

        producto = iProductoService.get(id).get();
        detalleOrden.setNombre(producto.getNombre());
        detalleOrden.setCantidad(cantidad);
        detalleOrden.setPrecio(producto.getPrecio());
        detalleOrden.setTotal(producto.getPrecio()*cantidad);
        detalleOrden.setProducto(producto);

        //No repetir productos
        Integer idProducto=producto.getId();
        boolean existeProducto = detalles.stream().anyMatch(p -> p.getProducto().getId()==idProducto);

        if (!existeProducto) {
            detalles.add(detalleOrden);
        }


        total = detalles.stream().mapToDouble(dt-> dt.getTotal()).sum();
        orden.setTotal(total);

        model.addAttribute("carrito", detalles);
        model.addAttribute("orden", orden);

        return "usuario/carrito";
    }

    //Quitar producto del carrito
    @GetMapping("/putcart/{id}")
    public String putCart(@PathVariable Integer id, Model model){

        List<DetalleOrden> ordenesNuevas = new ArrayList<DetalleOrden>();

        for (DetalleOrden detalleOrden : detalles){
            if (detalleOrden.getProducto().getId()!=id){
                ordenesNuevas.add(detalleOrden);
            }
        }
        detalles = ordenesNuevas;

        double total =0;
        total = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
        orden.setTotal(total);

        model.addAttribute("carrito", detalles);
        model.addAttribute("orden", orden);

        return "usuario/carrito";
    }

    //Mostrar el carrito
    @GetMapping("/getCarrito")
    public String getCarrito(Model model, HttpSession httpSession) {

        model.addAttribute("carrito", detalles);
        model.addAttribute("orden", orden);
        model.addAttribute("idUsuario", httpSession.getAttribute("idUsuario"));
        return "usuario/carrito";
    }

    //Mostrar el resumen de la orden
    @GetMapping("/orden")
    public String orden(Model model, HttpSession httpSession) {

        Usuario usuario = iUsuarioService.findById(Integer.parseInt(httpSession.getAttribute("idUsuario").toString())).get();

        model.addAttribute("carrito", detalles);
        model.addAttribute("orden", orden);
        model.addAttribute("usuario", usuario);
        return "usuario/resumenorden";
    }

    //Guardar la orden y sus detalles en la bd
    @GetMapping("/saveorden")
    public  String saveOrden(HttpSession httpSession){

        Date fechaCreacion = new Date();
        orden.setFechaCreacion(fechaCreacion);
        orden.setNumero(iOrdenService.generarNumeroOrden());
        //Usuario que hace la orden
        orden.setUsuario(iUsuarioService.findById(Integer.parseInt(httpSession.getAttribute("idUsuario").toString())).get());
        //Guardar Orden
        iOrdenService.save(orden);
        //Guardar Detalles
        for (DetalleOrden dt:detalles){
            dt.setOrden(orden);
            iDetalleOrdenService.save(dt);
        }
        //Limpiar lo ya guardado
        orden = new Orden();
        detalles.clear();
        return "redirect:/";
    }

    //Caja de texto para buscar productos
    @PostMapping("/search")
    public String search(@RequestParam String search, Model model){
        model.addAttribute("productos", iProductoService.findAll().stream().filter(p -> p.getNombre().contains(search)).collect(Collectors.toList()));
        return "usuario/home";
    }


}
