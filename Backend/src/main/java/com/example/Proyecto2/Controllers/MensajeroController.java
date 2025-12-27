package com.example.Proyecto2.Controllers;
import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Service.ServiceMensajerosINT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajeros")
public class MensajeroController {

	private final ServiceMensajerosINT serviceMensajeros;

    public MensajeroController(ServiceMensajerosINT serviceMensajeros) {
        this.serviceMensajeros = serviceMensajeros;
    }

    @GetMapping
    public ResponseEntity<List<Mensajeros>> listarMensajeros() {
        return ResponseEntity.ok(serviceMensajeros.obtenerMensajeros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMensajero(@PathVariable String id) {
        Mensajeros m = serviceMensajeros.obtenerMensajeroPorId(id);
        if (m == null) {
            return ResponseEntity.status(404).body("ERROR: Mensajero no encontrado: " + id);
        }
        return ResponseEntity.ok(m);
    }

    @PostMapping
    public ResponseEntity<?> crearMensajero(@RequestBody Mensajeros mensajero) {
        Mensajeros creado = serviceMensajeros.crearMensajero(mensajero);
        if (creado == null) {
            return ResponseEntity.badRequest().body("ERROR: Datos inválidos o centro no existe.");
        }
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String id,
            @RequestParam String nuevoEstado
    ) {
        Mensajeros existente = serviceMensajeros.obtenerMensajeroPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("ERROR: Mensajero no encontrado: " + id);
        }

        Mensajeros actualizado = serviceMensajeros.cambiarEstado(id, nuevoEstado);
        if (actualizado == null) {
            return ResponseEntity.badRequest().body("ERROR: Estado inválido. Use DISPONIBLE o EN_TRANSITO.");
        }

        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/centro")
    public ResponseEntity<?> reasignarCentro(
            @PathVariable String id,
            @RequestParam String nuevoCentroId
    ) {
        Mensajeros existente = serviceMensajeros.obtenerMensajeroPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("ERROR: Mensajero no encontrado: " + id);
        }

        Mensajeros actualizado = serviceMensajeros.reasignarCentro(id, nuevoCentroId);
        if (actualizado == null) {
            return ResponseEntity.badRequest().body("ERROR: No se puede reasignar (en tránsito o centro inválido).");
        }

        return ResponseEntity.ok(actualizado);
    }

}
