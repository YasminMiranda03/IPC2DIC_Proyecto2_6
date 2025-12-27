package com.example.Proyecto2.Controllers;


import com.example.Proyecto2.Models.Paquetes;
import com.example.Proyecto2.Service.ServicePaqueteINT;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
public class PaqueteController {
	
	 private final ServicePaqueteINT servicePaquete;

	    public PaqueteController(ServicePaqueteINT servicePaquete) {
	        this.servicePaquete = servicePaquete;
	    }

	    @GetMapping
	    public List<Paquetes> listarPaquetes() {
	        return servicePaquete.obtenerPaquetes();
	    }

	    @GetMapping("/{id}")
	    public Paquetes obtenerPaquete(@PathVariable String id) {
	        return servicePaquete.obtenerPaquetePorId(id);
	    }

	    @PostMapping
	    public Paquetes crearPaquete(@RequestBody Paquetes paquete) {
	        return servicePaquete.crearPaquete(paquete);
	    }

	    @PutMapping("/{id}")
	    public Paquetes actualizarPaquete(@PathVariable String id, @RequestBody Paquetes paquete) {
	        return servicePaquete.actualizarPaquete(id, paquete);
	    }

	    @DeleteMapping("/{id}")
	    public boolean eliminarPaquete(@PathVariable String id) {
	        return servicePaquete.eliminarPaquete(id);
	    }

}
