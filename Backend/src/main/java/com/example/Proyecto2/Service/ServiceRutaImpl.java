package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Rutas;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceRutaImpl implements ServiceRutaINT {
	
	private final List<Rutas> rutas = new ArrayList<>();
    private final ServiceCentroINT serviceCentro;
    public ServiceRutaImpl(ServiceCentroINT serviceCentro) {
        this.serviceCentro = serviceCentro;
    }
    @Override
    public List<Rutas> obtenerRutas() {
        return rutas;
    }
    @Override
    public Rutas obtenerRutaPorId(String id) {
        if (id == null) return null;
        for (Rutas r : rutas) {
            if (r != null && r.getId() != null && r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }
    @Override
    public Rutas crearRuta(Rutas ruta) {
        if (ruta == null) return null;

        if (ruta.getId() == null || ruta.getId().isBlank()) return null;
        if (ruta.getOrigen() == null || ruta.getOrigen().isBlank()) return null;
        if (ruta.getDestino() == null || ruta.getDestino().isBlank()) return null;
        if (ruta.getDistancia() == null || ruta.getDistancia() <= 0) return null;
        if (obtenerRutaPorId(ruta.getId()) != null) return null;
        if (serviceCentro != null) {
            if (!serviceCentro.existeCentro(ruta.getOrigen())) return null;
            if (!serviceCentro.existeCentro(ruta.getDestino())) return null;
        }
        if (existeRutaOrigenDestino(ruta.getOrigen(), ruta.getDestino())) return null;
        rutas.add(ruta);
        return ruta;
    }
    @Override
    public Rutas actualizarRuta(String id, Rutas rutaActualizada) {
        if (id == null || rutaActualizada == null) return null;
        Rutas existente = obtenerRutaPorId(id);
        if (existente == null) return null;
        if (rutaActualizada.getOrigen() == null || rutaActualizada.getOrigen().isBlank()) return null;
        if (rutaActualizada.getDestino() == null || rutaActualizada.getDestino().isBlank()) return null;
        if (rutaActualizada.getDistancia() == null || rutaActualizada.getDistancia() <= 0) return null;
        if (serviceCentro != null) {
            if (!serviceCentro.existeCentro(rutaActualizada.getOrigen())) return null;
            if (!serviceCentro.existeCentro(rutaActualizada.getDestino())) return null;
        }
        boolean cambiaPar = !existente.getOrigen().equals(rutaActualizada.getOrigen())
                || !existente.getDestino().equals(rutaActualizada.getDestino());

        if (cambiaPar && existeRutaOrigenDestino(rutaActualizada.getOrigen(), rutaActualizada.getDestino())) {
            return null;
        }
        existente.setOrigen(rutaActualizada.getOrigen());
        existente.setDestino(rutaActualizada.getDestino());
        existente.setDistancia(rutaActualizada.getDistancia());
        return existente;
    }
    @Override
    public boolean eliminarRuta(String id) {
        if (id == null) return false;
        return rutas.removeIf(r -> r != null && id.equals(r.getId()));
    }
    @Override
    public boolean existeRuta(String id) {
        return obtenerRutaPorId(id) != null;
    }
    private boolean existeRutaOrigenDestino(String origen, String destino) {
        for (Rutas r : rutas) {
            if (r == null) continue;
            if (r.getOrigen() == null || r.getDestino() == null) continue;

            if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void limpiar() {
        rutas.clear();
    }

}
