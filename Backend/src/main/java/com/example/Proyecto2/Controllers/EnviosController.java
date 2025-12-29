package com.example.Proyecto2.Controllers;

import com.example.Proyecto2.Models.AsignarEnvio;
import com.example.Proyecto2.Models.Paquetes;
import com.example.Proyecto2.Service.ServiceEnviosINT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/envios")
public class EnviosController {

    private final ServiceEnviosINT serviceEnvios;

    public EnviosController(ServiceEnviosINT serviceEnvios) {
        this.serviceEnvios = serviceEnvios;
    }

    @PutMapping("/asignar")
    public ResponseEntity<?> asignar(@RequestBody AsignarEnvio req) {
        if (req == null) return ResponseEntity.badRequest().body("ERROR: Body vacío.");

        boolean ok = serviceEnvios.asignarMensajero(req.getPaqueteId(), req.getMensajeroId());
        if (!ok) return ResponseEntity.badRequest().body("ERROR: No se pudo asignar (reglas no cumplen).");

        return ResponseEntity.ok(true);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable("id") String id,
            @RequestParam("nuevoEstado") String nuevoEstado
    ) {
        Paquetes p = serviceEnvios.cambiarEstadoEnvio(id, nuevoEstado);
        if (p == null) return ResponseEntity.badRequest().body("ERROR: Transición inválida o paquete no existe.");
        return ResponseEntity.ok(p);
    }
}