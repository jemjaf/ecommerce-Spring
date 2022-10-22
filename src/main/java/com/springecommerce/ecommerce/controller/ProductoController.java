package com.springecommerce.ecommerce.controller;

import com.springecommerce.ecommerce.model.Producto;
import com.springecommerce.ecommerce.model.Usuario;
import com.springecommerce.ecommerce.service.ProductoService;
import com.springecommerce.ecommerce.service.UploadFileService;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    public ProductoService productoService;

    @Autowired
    private UploadFileService upload;

    @GetMapping("")
    public String show(Model model){
        model.addAttribute("Productos",productoService.finAll());
        return "productos/show";
    }

    @GetMapping ("/create")
    public String create(){
        return  "productos/create";
    }

    @PostMapping("/save")
    public String save(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
        Usuario u = new Usuario(1,"","","","","","","");
        producto.setUsuario(u);

        //Guardar imagen
        if (producto.getId()==null){
            String nombreImagen = upload.saveImage(file);
            producto.setImagen(nombreImagen);
        }else{
            if (file.isEmpty()){ //Edición de datos pero no cambia la imagen
                Producto p = new Producto();
                p = productoService.get(producto.getId()).get();
                producto.setImagen(p.getImagen());
            }else{
                String nombreImagen = upload.saveImage(file);
                producto.setImagen(nombreImagen);
            }
        }

        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model){
        Producto producto = new Producto();
        Optional<Producto> optionalProducto = productoService.get(id);
        producto = optionalProducto.get();

        model.addAttribute("producto", producto);
        return  "productos/edit";
    }

    @PostMapping("/update")
    public String update(Producto producto) {
        productoService.update(producto);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        productoService.delete(id);
        return "redirect:/productos";
    }
}
