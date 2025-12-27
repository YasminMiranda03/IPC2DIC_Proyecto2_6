package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Centro;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class ServiceCentroImpl implements ServiceCentroINT {
	
	private final List<Centro> centros = new ArrayList<>();
    @Override
    public List<Centro> obtenerCentros() {
        return centros;
    }
    @Override
    public Centro obtenerCentroPorId(String id) {
        if (id == null) return null;

        for (Centro c : centros) {
            if (c == null || c.getId() == null) continue;
            if (c.getId().equals(id)) return c;
        }
        return null;
    }
    @Override
    public Centro crearCentro(Centro centro) {
        if (centro == null) return null;
        if (centro.getId() == null || centro.getId().isBlank()) return null;
        if (existeCentro(centro.getId())) return null;
        if (centro.getMensajerosIds() == null) {
            centro.setMensajerosIds(new ArrayList<>());
        }

        centros.add(centro);
        return centro;
    }
    @Override
    public Centro actualizarCentro(String id, Centro centroActualizado) {
        if (id == null || centroActualizado == null) return null;

        Centro existente = obtenerCentroPorId(id);
        if (existente == null) return null;

        if (centroActualizado.getNombre() != null && !centroActualizado.getNombre().isBlank()) {
            existente.setNombre(centroActualizado.getNombre());
        }
        if (centroActualizado.getCiudad() != null && !centroActualizado.getCiudad().isBlank()) {
            existente.setCiudad(centroActualizado.getCiudad());
        }
        if (centroActualizado.getCapacidad() != null && centroActualizado.getCapacidad() > 0) {
            existente.setCapacidad(centroActualizado.getCapacidad());
        }

        return existente;
    }
    @Override
    public boolean eliminarCentro(String id) {
        if (id == null) return false;
        return centros.removeIf(c -> c != null && id.equals(c.getId()));
    }

    @Override
    public boolean existeCentro(String id) {
        return obtenerCentroPorId(id) != null;
    }
    @Override
    public boolean agregarMensajeroACentro(String centroId, String mensajeroId) {
        Centro c = obtenerCentroPorId(centroId);
        if (c == null) return false;
        if (mensajeroId == null || mensajeroId.isBlank()) return false;

        if (c.getMensajerosIds() == null) {
            c.setMensajerosIds(new ArrayList<>());
        }

        if (!c.getMensajerosIds().contains(mensajeroId)) {
            c.getMensajerosIds().add(mensajeroId);
        }
        return true;
    }

    @Override
    public boolean quitarMensajeroDeCentro(String centroId, String mensajeroId) {
        Centro c = obtenerCentroPorId(centroId);
        if (c == null) return false;
        if (mensajeroId == null || mensajeroId.isBlank()) return false;
        if (c.getMensajerosIds() == null) return false;

        return c.getMensajerosIds().remove(mensajeroId);
    }

    @Override
    public void limpiar() {
        centros.clear();
    }
}