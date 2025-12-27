package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Mensajeros;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceMensajerosImpl implements ServiceMensajerosINT {


	private final List<Mensajeros> mensajeros = new ArrayList<>();
    private final ServiceCentroINT serviceCentro;

    public ServiceMensajerosImpl(ServiceCentroINT serviceCentro) {
        this.serviceCentro = serviceCentro;
    }
    @Override
    public List<Mensajeros> obtenerMensajeros() {
        return mensajeros;
    }
    @Override
    public Mensajeros obtenerMensajeroPorId(String id) {
        if (id == null) return null;

        for (Mensajeros m : mensajeros) {
            if (m != null && m.getId() != null && m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }
    @Override
    public Mensajeros crearMensajero(Mensajeros mensajero) {
        if (mensajero == null) return null;
        if (mensajero.getId() == null || mensajero.getId().isBlank()) return null;
        if (mensajero.getNombre() == null || mensajero.getNombre().isBlank()) return null;
        if (mensajero.getCentroId() == null || mensajero.getCentroId().isBlank()) return null;
        if (mensajero.getCapacidad() == null || mensajero.getCapacidad() <= 0) return null;
        if (obtenerMensajeroPorId(mensajero.getId()) != null) return null;
        if (mensajero.getEstado() == null || mensajero.getEstado().isBlank()) {
            mensajero.setEstado("DISPONIBLE");
        } else {
            String estado = normalizarEstado(mensajero.getEstado());
            if (estado == null) return null;
            mensajero.setEstado(estado);
        }
        if (serviceCentro != null && !serviceCentro.existeCentro(mensajero.getCentroId())) {
            return null;
        }

        mensajeros.add(mensajero);
        if (serviceCentro != null) {
            serviceCentro.agregarMensajeroACentro(mensajero.getCentroId(), mensajero.getId());
        }
        return mensajero;
    }
    @Override
    public Mensajeros cambiarEstado(String id, String nuevoEstado) {
        Mensajeros m = obtenerMensajeroPorId(id);
        if (m == null) return null;

        String estado = normalizarEstado(nuevoEstado);
        if (estado == null) return null; 

        m.setEstado(estado);
        return m;
    }
    @Override
    public Mensajeros reasignarCentro(String id, String nuevoCentroId) {
        Mensajeros m = obtenerMensajeroPorId(id);
        if (m == null) return null;

        if (nuevoCentroId == null || nuevoCentroId.isBlank()) return null;
        if ("EN_TRANSITO".equalsIgnoreCase(m.getEstado())) {
            return null;
        }
        if (serviceCentro != null && !serviceCentro.existeCentro(nuevoCentroId)) {
            return null;
        }

        String centroAnterior = m.getCentroId();
        if (serviceCentro != null) {
            if (centroAnterior != null && !centroAnterior.isBlank()) {
                serviceCentro.quitarMensajeroDeCentro(centroAnterior, m.getId());
            }
            serviceCentro.agregarMensajeroACentro(nuevoCentroId, m.getId());
        }
        m.setCentroId(nuevoCentroId);
        return m;
    }
    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) return null;

        String e = estado.trim().toUpperCase();
        if (!e.equals("DISPONIBLE") && !e.equals("EN_TRANSITO")) {
            return null;
        }
        return e;
    }
    @Override
    public void limpiar() {
        mensajeros.clear();
    }
}
