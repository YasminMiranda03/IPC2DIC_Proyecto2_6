package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Paquetes;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;



@Service
public class ServicePaqueteImpl implements ServicePaqueteINT {
	
	private final List<Paquetes> paquetes = new ArrayList<>();
    private final ServiceCentroINT serviceCentro;

    public ServicePaqueteImpl(ServiceCentroINT serviceCentro) {
        this.serviceCentro = serviceCentro;
    }

    @Override
    public List<Paquetes> obtenerPaquetes() {
        return paquetes;
    }

    @Override
    public Paquetes obtenerPaquetePorId(String id) {
        if (id == null) return null;

        for (Paquetes p : paquetes) {
            if (p != null && p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Paquetes crearPaquete(Paquetes paquete) {
        if (paquete == null) return null;
        if (paquete.getId() == null || paquete.getId().isBlank()) return null;
        if (paquete.getCliente() == null || paquete.getCliente().isBlank()) return null;
        if (paquete.getDestino() == null || paquete.getDestino().isBlank()) return null;
        if (paquete.getCentroActual() == null || paquete.getCentroActual().isBlank()) return null;
        if (paquete.getPeso() == null || paquete.getPeso() <= 0) return null;

        if (obtenerPaquetePorId(paquete.getId()) != null) return null;

        if (serviceCentro != null && !serviceCentro.existeCentro(paquete.getDestino())) return null;
        if (serviceCentro != null && !serviceCentro.existeCentro(paquete.getCentroActual())) return null;

        if (paquete.getEstado() == null || paquete.getEstado().isBlank()) {
            paquete.setEstado("PENDIENTE");
        } else {
            String est = normalizarEstado(paquete.getEstado());
            if (est == null) return null;
            paquete.setEstado(est);
        }

        paquetes.add(paquete);
        return paquete;
    }

    @Override
    public Paquetes actualizarPaquete(String id, Paquetes paqueteActualizado) {
        if (id == null || id.isBlank() || paqueteActualizado == null) return null;

        Paquetes existente = obtenerPaquetePorId(id);
        if (existente == null) return null;

        if (paqueteActualizado.getPeso() != null) {
            if (paqueteActualizado.getPeso() <= 0) return null;
            existente.setPeso(paqueteActualizado.getPeso());
        }

        if (paqueteActualizado.getCliente() != null && !paqueteActualizado.getCliente().isBlank()) {
            existente.setCliente(paqueteActualizado.getCliente());
        }

        if (paqueteActualizado.getDestino() != null && !paqueteActualizado.getDestino().isBlank()) {
            if (serviceCentro != null && !serviceCentro.existeCentro(paqueteActualizado.getDestino())) {
                return null;
            }
            existente.setDestino(paqueteActualizado.getDestino());
        }

        if (paqueteActualizado.getCentroActual() != null && !paqueteActualizado.getCentroActual().isBlank()) {
            if (serviceCentro != null && !serviceCentro.existeCentro(paqueteActualizado.getCentroActual())) {
                return null;
            }
            existente.setCentroActual(paqueteActualizado.getCentroActual());
        }

        if (paqueteActualizado.getEstado() != null && !paqueteActualizado.getEstado().isBlank()) {
            String est = normalizarEstado(paqueteActualizado.getEstado());
            if (est == null) return null;
            existente.setEstado(est);
        }

        return existente;
    }

    @Override
    public boolean eliminarPaquete(String id) {
        Paquetes p = obtenerPaquetePorId(id);
        if (p == null) return false;

        String est = (p.getEstado() == null) ? "" : p.getEstado().trim().toUpperCase();
        if (est.equals("EN_TRANSITO") || est.equals("ENTREGADO")) {
            return false;
        }

        return paquetes.removeIf(x -> x != null && id.equals(x.getId()));
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) return null;

        String e = estado.trim().toUpperCase();
        if (!e.equals("PENDIENTE") && !e.equals("EN_TRANSITO") && !e.equals("ENTREGADO")) {
            return null;
        }
        return e;
    }

    @Override
    public void limpiar() {
        paquetes.clear();
    }

    @Override
    public List<Paquetes> obtenerPaquetesPorCentro(String centroId) {
        List<Paquetes> res = new ArrayList<>();
        if (centroId == null) return res;

        for (Paquetes p : paquetes) {
            if (p != null && centroId.equals(p.getCentroActual())) {
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public int contarPaquetesEnCentro(String centroId) {
        int count = 0;
        if (centroId == null) return 0;

        for (Paquetes p : paquetes) {
            if (p != null && centroId.equals(p.getCentroActual())) {
                count++;
            }
        }
        return count;
    }
}
