package com.example.Proyecto2.Service;

import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Models.Paquetes;
import com.example.Proyecto2.Models.RespuestasProcesamiento;
import com.example.Proyecto2.Models.Rutas;
import com.example.Proyecto2.Models.Solicitudes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceSolicitudImpl implements ServiceSolicitudINT {
	
	
    private final List<Solicitudes> cola = new ArrayList<>();
    private final List<Solicitudes> historialAtendidas = new ArrayList<>();
    private final ServicePaqueteINT servicePaquete;
    private final ServiceMensajerosINT serviceMensajeros;
    private final ServiceRutaINT serviceRuta;

    public ServiceSolicitudImpl(ServicePaqueteINT servicePaquete,
                                ServiceMensajerosINT serviceMensajeros,
                                ServiceRutaINT serviceRuta) {
        this.servicePaquete = servicePaquete;
        this.serviceMensajeros = serviceMensajeros;
        this.serviceRuta = serviceRuta;
    }
    @Override
    public List<Solicitudes> obtenerSolicitudes() {
        ordenarCola();
        return cola;
    }
    @Override
    public Solicitudes crearSolicitud(Solicitudes s) {
        if (s == null) return null;
        if (s.getId() == null || s.getId().isBlank()) return null;
        if (s.getTipo() == null || s.getTipo().isBlank()) return null;
        if (s.getPaqueteId() == null || s.getPaqueteId().isBlank()) return null;
        if (s.getPrioridad() == null) return null;
        if (obtenerSolicitudPorId(s.getId()) != null) return null;
        if (s.getEstado() == null || s.getEstado().isBlank()) {
            s.setEstado("PENDIENTE");
        }

        cola.add(s);
        ordenarCola();
        return s;
    }

    @Override
    public boolean eliminarSolicitud(String id) {
        if (id == null) return false;
        return cola.removeIf(x -> x != null && id.equals(x.getId()));
    }

    @Override
    public RespuestasProcesamiento procesarMayorPrioridad() {
        ordenarCola();
        if (cola.isEmpty()) {
            return new RespuestasProcesamiento(false, "No hay solicitudes en cola.", new ArrayList<>());
        }
        return procesarInterno(1);
    }

    @Override
    public RespuestasProcesamiento procesarN(int n) {
        ordenarCola();
        if (n <= 0) {
            return new RespuestasProcesamiento(false, "N debe ser mayor que 0.", new ArrayList<>());
        }
        if (cola.isEmpty()) {
            return new RespuestasProcesamiento(false, "No hay solicitudes en cola.", new ArrayList<>());
        }
        return procesarInterno(n);
    }

    private RespuestasProcesamiento procesarInterno(int n) {
        ordenarCola();
        int limite = Math.min(n, cola.size());
        List<Solicitudes> top = new ArrayList<>();
        for (int i = 0; i < limite; i++) top.add(cola.get(i));
        List<String> atendidas = new ArrayList<>();
        String motivoFallo = null;

        for (Solicitudes sol : top) {
            if (!cola.contains(sol)) continue;

            ResultadoValidacion val = validarSolicitudEnvio(sol);
            if (!val.exito) {
                if (motivoFallo == null) motivoFallo = val.motivo;
                continue;
            }

            Paquetes paqueteBase = val.paquete;
            String origen = paqueteBase.getCentroActual();
            String destino = paqueteBase.getDestino();

            Mensajeros mensajero = buscarMensajeroDisponibleEnCentro(origen);
            if (mensajero == null) {
                if (motivoFallo == null) motivoFallo = "No hay mensajeros disponibles";
                continue;
            }

            int capacidad = (int) Math.floor(mensajero.getCapacidad() == null ? 0 : mensajero.getCapacidad());
            if (capacidad <= 0) {
                if (motivoFallo == null) motivoFallo = "El mensajero no puede llevar esa cantidad de paquetes.";
                continue;
            }
            List<Solicitudes> mismoTrayecto = new ArrayList<>();
            for (Solicitudes s2 : top) {
                if (!cola.contains(s2)) continue;

                ResultadoValidacion v2 = validarSolicitudEnvio(s2);
                if (!v2.exito) continue;

                Paquetes p2 = v2.paquete;
                if (origen.equals(p2.getCentroActual()) && destino.equals(p2.getDestino())) {
                    mismoTrayecto.add(s2);
                }
            }

            int asignadas = 0;
            for (Solicitudes sRuta : mismoTrayecto) {
                if (asignadas >= capacidad) break;
                if (!cola.contains(sRuta)) continue;
                ResultadoValidacion vRuta = validarSolicitudEnvio(sRuta);
                if (!vRuta.exito) continue;
                marcarEnTransito(mensajero, vRuta.paquete);
                atendidas.add(sRuta.getId());
                sRuta.setEstado("ATENDIDA");
                historialAtendidas.add(sRuta);
                cola.remove(sRuta);
                asignadas++;
            }
        }

        ordenarCola();

        if (atendidas.isEmpty()) {
            if (motivoFallo == null) motivoFallo = "No se pudo procesar ninguna solicitud";
            return new RespuestasProcesamiento(false, motivoFallo, atendidas);
        }

        return new RespuestasProcesamiento(true, null, atendidas);
    }

    private void marcarEnTransito(Mensajeros mensajero, Paquetes paquete) {
        serviceMensajeros.cambiarEstado(mensajero.getId(), "EN_TRANSITO");
        paquete.setEstado("EN_TRANSITO");
        servicePaquete.actualizarPaquete(paquete.getId(), paquete);
    }

    private ResultadoValidacion validarSolicitudEnvio(Solicitudes sol) {
        if (sol == null) return new ResultadoValidacion(false, "Solicitud nula.", null);

        Paquetes p = servicePaquete.obtenerPaquetePorId(sol.getPaqueteId());
        if (p == null) return new ResultadoValidacion(false, "El paquete no existe.", null);

        String est = (p.getEstado() == null) ? "" : p.getEstado().trim().toUpperCase();
        if (!est.equals("PENDIENTE")) {
            return new ResultadoValidacion(false, "El paquete no está en estado PENDIENTE.", null);
        }

        if (!existeRutaDirecta(p.getCentroActual(), p.getDestino())) {
            return new ResultadoValidacion(false, "No existe ruta directa entre el centro de origen y destino.", null);
        }

        return new ResultadoValidacion(true, null, p);
    }

    private boolean existeRutaDirecta(String origen, String destino) {
        List<Rutas> rutas = serviceRuta.obtenerRutas();
        if (rutas == null) return false;

        for (Rutas r : rutas) {
            if (r == null) continue;
            if (r.getOrigen() == null || r.getDestino() == null) continue;
            if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                return true;
            }
        }
        return false;
    }

    private Mensajeros buscarMensajeroDisponibleEnCentro(String centroId) {
        List<Mensajeros> lista = serviceMensajeros.obtenerMensajeros();
        if (lista == null) return null;

        for (Mensajeros m : lista) {
            if (m == null) continue;
            if (m.getCentroId() == null) continue;

            if (m.getCentroId().equals(centroId) && "DISPONIBLE".equalsIgnoreCase(m.getEstado())) {
                return m;
            }
        }
        return null;
    }

    private Solicitudes obtenerSolicitudPorId(String id) {
        for (Solicitudes s : cola) {
            if (s != null && s.getId() != null && s.getId().equals(id)) return s;
        }
        return null;
    }

    private void ordenarCola() {
        cola.sort((a, b) -> {
            Integer pa = (a == null) ? null : a.getPrioridad();
            Integer pb = (b == null) ? null : b.getPrioridad();

            if (pa == null && pb == null) return 0;
            if (pa == null) return 1;
            if (pb == null) return -1;

            return Integer.compare(pb, pa);
        });
    }

    private static class ResultadoValidacion {
        boolean exito;
        String motivo;
        Paquetes paquete;

        ResultadoValidacion(boolean exito, String motivo, Paquetes paquete) {
            this.exito = exito;
            this.motivo = motivo;
            this.paquete = paquete;
        }
    }
    @Override
    public void limpiar() {
        cola.clear();
        historialAtendidas.clear();
    }
    public List<Solicitudes> getHistorialAtendidas() {
        return historialAtendidas;
    }
    public int getHistorialAtendidasCount() {
        return historialAtendidas.size();
    }

}
