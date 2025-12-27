package com.example.Proyecto2.Controllers;

import com.example.Proyecto2.Models.RespuestasProcesamiento;
import com.example.Proyecto2.Models.Solicitudes;
import com.example.Proyecto2.Service.ServiceSolicitudINT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
	private final ServiceSolicitudINT serviceSolicitud;

    public SolicitudController(ServiceSolicitudINT serviceSolicitud) {
        this.serviceSolicitud = serviceSolicitud;
    }
    @GetMapping
    public List<Solicitudes> listarSolicitudes() {
        return serviceSolicitud.obtenerSolicitudes();
    }
    @PostMapping
    public ResponseEntity<Solicitudes> crearSolicitud(@RequestBody Solicitudes solicitud) {
        Solicitudes creada = serviceSolicitud.crearSolicitud(solicitud);
        if (creada == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(creada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable String id) {
        boolean eliminado = serviceSolicitud.eliminarSolicitud(id);
        if (!eliminado) {
            return ResponseEntity.status(404).body("ERROR: Solicitud no encontrada: " + id);
        }
        return ResponseEntity.ok(true);
    }
    @PostMapping("/procesar")
    public ResponseEntity<RespuestasProcesamiento> procesarMayorPrioridad() {
        RespuestasProcesamiento resp = serviceSolicitud.procesarMayorPrioridad();
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/procesar/{n}")
    public ResponseEntity<RespuestasProcesamiento> procesarN(@PathVariable int n) {
        RespuestasProcesamiento resp = serviceSolicitud.procesarN(n);
        return ResponseEntity.ok(resp);
    }

}
