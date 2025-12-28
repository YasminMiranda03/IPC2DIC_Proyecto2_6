package com.example.Proyecto2.Controllers;
import com.example.Proyecto2.Models.Rutas;
import com.example.Proyecto2.Service.ServiceRutaINT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<?> obtenerRuta(@PathVariable("id") String id) {
        Rutas r = serviceRuta.obtenerRutaPorId(id);
        if (r == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no existe.");
        }
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<?> crearRuta(@RequestBody Rutas ruta) {
        Rutas creada = serviceRuta.crearRuta(ruta);
        if (creada == null) {
            return ResponseEntity.badRequest().body(
                "No se pudo crear: datos inválidos / centros inexistentes / ruta duplicada (origen-destino)."
            );
        }
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRuta(@PathVariable("id") String id, @RequestBody Rutas ruta) {
        Rutas actualizada = serviceRuta.actualizarRuta(id, ruta);

        if (actualizada == null) {
            if (!serviceRuta.existeRuta(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no existe.");
            }
            return ResponseEntity.badRequest().body(
                "No se pudo actualizar: datos inválidos / centros inexistentes / ruta duplicada (origen-destino)."
            );
        }

        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable("id") String id) {
        if (!serviceRuta.existeRuta(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no existe.");
        }
        boolean eliminado = serviceRuta.eliminarRuta(id);
        return ResponseEntity.ok(eliminado);
    }
}
