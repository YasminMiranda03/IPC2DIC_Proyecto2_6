package com.example.Proyecto2.Controllers;

import com.example.Proyecto2.Models.Rutas;
import com.example.Proyecto2.Service.ServiceRutaINT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {
	private final ServiceRutaINT serviceRuta;

    public RutaController(ServiceRutaINT serviceRuta) {
        this.serviceRuta = serviceRuta;
    }
    @GetMapping
    public List<Rutas> listarRutas() {
        return serviceRuta.obtenerRutas();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Rutas> obtenerRuta(@PathVariable String id) {
        Rutas r = serviceRuta.obtenerRutaPorId(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }
    @PostMapping
    public ResponseEntity<Rutas> crearRuta(@RequestBody Rutas ruta) {
        Rutas creada = serviceRuta.crearRuta(ruta);
        if (creada == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(creada);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Rutas> actualizarRuta(@PathVariable String id, @RequestBody Rutas ruta) {
        Rutas actualizada = serviceRuta.actualizarRuta(id, ruta);
        if (actualizada == null) {         
            if (!serviceRuta.existeRuta(id)) return ResponseEntity.notFound().build();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(actualizada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarRuta(@PathVariable String id) {
        if (!serviceRuta.existeRuta(id)) return ResponseEntity.notFound().build();
        boolean eliminado = serviceRuta.eliminarRuta(id);
        return ResponseEntity.ok(eliminado);
    }
}
