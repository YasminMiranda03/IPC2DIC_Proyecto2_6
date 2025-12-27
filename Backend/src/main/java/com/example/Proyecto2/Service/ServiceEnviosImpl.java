package com.example.Proyecto2.Service;

import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Models.Paquetes;
import org.springframework.stereotype.Service;


@Service
public class ServiceEnviosImpl implements ServiceEnviosINT{
	private final ServicePaqueteINT servicePaquete;
    private final ServiceMensajerosINT serviceMensajeros;

    public ServiceEnviosImpl(ServicePaqueteINT servicePaquete, ServiceMensajerosINT serviceMensajeros) {
        this.servicePaquete = servicePaquete;
        this.serviceMensajeros = serviceMensajeros;
    }

    @Override
    public boolean asignarMensajero(String paqueteId, String mensajeroId) {
        if (paqueteId == null || paqueteId.isBlank()) return false;
        if (mensajeroId == null || mensajeroId.isBlank()) return false;

        Paquetes p = servicePaquete.obtenerPaquetePorId(paqueteId);
        if (p == null) return false;

        Mensajeros m = serviceMensajeros.obtenerMensajeroPorId(mensajeroId);
        if (m == null) return false;

        if (p.getCentroActual() == null || m.getCentroId() == null) return false;
        if (!p.getCentroActual().equals(m.getCentroId())) return false;

        String estP = (p.getEstado() == null) ? "" : p.getEstado().trim().toUpperCase();
        if (!"PENDIENTE".equals(estP)) return false;

        String estM = (m.getEstado() == null) ? "" : m.getEstado().trim().toUpperCase();
        if (!"DISPONIBLE".equals(estM)) return false;

        serviceMensajeros.cambiarEstado(m.getId(), "EN_TRANSITO");
        p.setEstado("EN_TRANSITO");
        servicePaquete.actualizarPaquete(p.getId(), p);

        return true;
    }
    @Override
    public Paquetes cambiarEstadoEnvio(String paqueteId, String nuevoEstado) {
        if (paqueteId == null || paqueteId.isBlank()) return null;
        if (nuevoEstado == null || nuevoEstado.isBlank()) return null;
        Paquetes p = servicePaquete.obtenerPaquetePorId(paqueteId);
        if (p == null) return null;
        String actual = (p.getEstado() == null) ? "" : p.getEstado().trim().toUpperCase();
        String target = nuevoEstado.trim().toUpperCase();
        boolean ok = ("PENDIENTE".equals(actual) && "EN_TRANSITO".equals(target))
                  || ("EN_TRANSITO".equals(actual) && "ENTREGADO".equals(target));

        if (!ok) return null;

        p.setEstado(target);

        if ("ENTREGADO".equals(target)) {
          
            for (Mensajeros m : serviceMensajeros.obtenerMensajeros()) {
                if (m == null) continue;
                if (m.getCentroId() == null) continue;

                if (m.getCentroId().equals(p.getCentroActual())
                        && "EN_TRANSITO".equalsIgnoreCase(m.getEstado())) {
                    serviceMensajeros.cambiarEstado(m.getId(), "DISPONIBLE");
                    break;
                }
            }
        }

        servicePaquete.actualizarPaquete(p.getId(), p);
        return p;
    }

}
