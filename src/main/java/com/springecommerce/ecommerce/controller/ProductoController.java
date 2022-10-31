package com.springecommerce.ecommerce.controller;

import com.springecommerce.ecommerce.model.Producto;
import com.springecommerce.ecommerce.model.Usuario;
import com.springecommerce.ecommerce.service.IProductoService;
import com.springecommerce.ecommerce.service.UploadFileService;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    public IProductoService IProductoService;

    @Autowired
    private UploadFileService upload;

    //Mantenedor productos
    @GetMapping("")
    public String show(Model model){
        model.addAttribute("productos", IProductoService.finAll());
        return "productos/show";
    }

    //Vista para crear productos
    @GetMapping ("/create")
    public String create(){
        return  "productos/create";
    }

    //Guardar un producto en la bd
    @PostMapping("/save")
    public String save(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
        Usuario u = new Usuario(1,"","","","","","","");
        producto.setUsuario(u);

        //Guardar imagen

        String nombreImagen = upload.saveImage(file);
        producto.setImagen(nombreImagen);


        IProductoService.save(producto);
        return "redirect:/productos";
    }

    //Vista para editar un producto
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model){
        Producto producto = new Producto();
        producto = IProductoService.get(id).get();

        model.addAttribute("producto", producto);
        return  "productos/edit";
    }

    //Atualizar producto editado
    @PostMapping("/update")
    public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {

        Producto p = new Producto();
        p = IProductoService.get(producto.getId()).get();

        if (file.isEmpty()){ //Edición de datos pero no cambia la imagen
            producto.setImagen(p.getImagen());
        }else{//Editar imagen

            //Eliminar la imagen del servidor
            if (!p.getImagen().equals("default.jpg")){
                upload.deleteImage(p.getImagen());
            }
            String nombreImagen = upload.saveImage(file);
            producto.setImagen(nombreImagen);
        }

        producto.setUsuario(p.getUsuario());
        IProductoService.update(producto);
        return "redirect:/productos";
    }

    //Eliminar producto y recargar la vista del mantenedor actualizada
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        Producto p = new Producto();
        p = IProductoService.get(id).get();

        //Eliminar la imagen del servidor
        if (!p.getImagen().equals("default.jpg")){
            upload.deleteImage(p.getImagen());
        }

        IProductoService.delete(id);
        return "redirect:/productos";
    }
}
