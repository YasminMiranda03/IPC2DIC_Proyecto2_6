package com.example.Proyecto2.Controllers;

import com.example.Proyecto2.Models.Centro;
import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Service.ServiceCentroINT;
import com.example.Proyecto2.Service.ServiceMensajerosINT;
import com.example.Proyecto2.Service.ServicePaqueteINT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/centros")
public class CentroController {

    private final ServiceCentroINT serviceCentro;
    private final ServicePaqueteINT servicePaquete;
    private final ServiceMensajerosINT serviceMensajeros;

    public CentroController(ServiceCentroINT serviceCentro,
                            ServicePaqueteINT servicePaquete,
                            ServiceMensajerosINT serviceMensajeros) {
        this.serviceCentro = serviceCentro;
        this.servicePaquete = servicePaquete;
        this.serviceMensajeros = serviceMensajeros;
    }

    @GetMapping
    public ResponseEntity<List<Centro>> listarCentros() {
        return ResponseEntity.ok(serviceCentro.obtenerCentros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCentroPorId(@PathVariable("id") String id) {
        Centro c = serviceCentro.obtenerCentroPorId(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: Centro no encontrado: " + id);
        }

        int capacidadMaxima = (c.getCapacidad() == null) ? 0 : c.getCapacidad();
        int cargaActual = servicePaquete.contarPaquetesEnCentro(id);
        double porcentajeUso = (capacidadMaxima <= 0) ? 0.0 : (cargaActual * 100.0 / capacidadMaxima);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", c.getId());
        resp.put("nombre", c.getNombre());
        resp.put("ciudad", c.getCiudad());
        resp.put("capacidadMaxima", capacidadMaxima);
        resp.put("cargaActual", cargaActual);
        resp.put("porcentajeUso", porcentajeUso);
        resp.put("mensajerosIds", c.getMensajerosIds());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/paquetes")
    public ResponseEntity<?> paquetesDelCentro(@PathVariable("id") String id) {
        Centro c = serviceCentro.obtenerCentroPorId(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: Centro no encontrado: " + id);
        }
        return ResponseEntity.ok(servicePaquete.obtenerPaquetesPorCentro(id));
    }

    @GetMapping("/{id}/mensajeros")
    public ResponseEntity<?> mensajerosDelCentro(@PathVariable("id") String id) {
        Centro c = serviceCentro.obtenerCentroPorId(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: Centro no encontrado: " + id);
        }

        List<Mensajeros> lista = new ArrayList<>();
        List<Mensajeros> todos = serviceMensajeros.obtenerMensajeros();

        if (todos != null) {
            for (Mensajeros m : todos) {
                if (m == null) continue;

                String centroId = m.getCentroId();
                if (centroId != null && centroId.equalsIgnoreCase(id)) {
                    lista.add(m);
                }
            }
        }

        return ResponseEntity.ok(lista);
    }
}

