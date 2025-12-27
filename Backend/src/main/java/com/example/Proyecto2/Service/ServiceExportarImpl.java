package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Centro;
import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Models.Paquetes;
import com.example.Proyecto2.Models.Solicitudes;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceExportarImpl implements ServiceExportarINT{
	
	 private final ServiceCentroINT serviceCentro;
	    private final ServiceMensajerosINT serviceMensajeros;
	    private final ServicePaqueteINT servicePaquete;
	    private final ServiceSolicitudINT serviceSolicitud;

	    public ServiceExportarImpl(
	            ServiceCentroINT serviceCentro,
	            ServiceMensajerosINT serviceMensajeros,
	            ServicePaqueteINT servicePaquete,
	            ServiceSolicitudINT serviceSolicitud
	    ) {
	        this.serviceCentro = serviceCentro;
	        this.serviceMensajeros = serviceMensajeros;
	        this.servicePaquete = servicePaquete;
	        this.serviceSolicitud = serviceSolicitud;
	    }

	    @Override
	    public String exportarXml() {
	        List<Centro> centros = serviceCentro.obtenerCentros();
	        List<Mensajeros> mensajeros = serviceMensajeros.obtenerMensajeros();
	        List<Paquetes> paquetes = servicePaquete.obtenerPaquetes();
	        List<Solicitudes> solicitudesPendientes = serviceSolicitud.obtenerSolicitudes(); 
	        int paquetesProcesados = 0;
	        if (paquetes != null) {
	            for (Paquetes p : paquetes) {
	                if (p == null) continue;
	                String est = safeUpper(p.getEstado());
	                if ("EN_TRANSITO".equals(est) || "ENTREGADO".equals(est)) {
	                    paquetesProcesados++;
	                }
	            }
	        }
	        int solicitudesAtendidas = 0;
	        List<Solicitudes> solicitudesAtendidasList = null;
	        if (serviceSolicitud instanceof ServiceSolicitudImpl) {
	            ServiceSolicitudImpl impl = (ServiceSolicitudImpl) serviceSolicitud;
	            solicitudesAtendidas = impl.getHistorialAtendidasCount();
	            solicitudesAtendidasList = impl.getHistorialAtendidas();
	        }
	        int mensajerosActivos = (mensajeros == null) ? 0 : mensajeros.size();
	        StringBuilder xml = new StringBuilder();
	        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	        xml.append("<resultadoLogitrack>\n");
	        xml.append("  <estadisticas>\n");
	        xml.append("    <paquetesProcesados>").append(paquetesProcesados).append("</paquetesProcesados>\n");
	        xml.append("    <solicitudesAtendidas>").append(solicitudesAtendidas).append("</solicitudesAtendidas>\n");
	        xml.append("    <mensajerosActivos>").append(mensajerosActivos).append("</mensajerosActivos>\n");
	        xml.append("  </estadisticas>\n");       
	        xml.append("  <centros>\n");
	        if (centros != null) {
	            for (Centro c : centros) {
	                if (c == null) continue;
	                String idCentro = c.getId();

	                int paquetesActuales = servicePaquete.contarPaquetesEnCentro(idCentro);
	                int mensajerosDisponibles = 0;

	                List<String> idsMens = c.getMensajerosIds();
	                if (idsMens != null) {
	                    for (String idM : idsMens) {
	                        Mensajeros m = serviceMensajeros.obtenerMensajeroPorId(idM);
	                        if (m != null && "DISPONIBLE".equals(safeUpper(m.getEstado()))) {
	                            mensajerosDisponibles++;
	                        }
	                    }
	                }
	                xml.append("    <centro id=\"").append(escapeAttr(idCentro)).append("\">\n");
	                xml.append("      <paquetesActuales>").append(paquetesActuales).append("</paquetesActuales>\n");
	                xml.append("      <mensajerosDisponibles>").append(mensajerosDisponibles).append("</mensajerosDisponibles>\n");
	                xml.append("    </centro>\n");
	            }
	        }
	        xml.append("  </centros>\n");
	        xml.append("  <mensajeros>\n");
	        if (mensajeros != null) {
	            for (Mensajeros m : mensajeros) {
	                if (m == null) continue;
	                xml.append("    <mensajero id=\"")
	                        .append(escapeAttr(m.getId()))
	                        .append("\" estado=\"")
	                        .append(escapeAttr(safeUpper(m.getEstado())))
	                        .append("\"/>\n");
	            }
	        }
	        xml.append("  </mensajeros>\n");
	        xml.append("  <paquetes>\n");
	        if (paquetes != null) {
	            for (Paquetes p : paquetes) {
	                if (p == null) continue;
	                xml.append("    <paquete id=\"")
	                        .append(escapeAttr(p.getId()))
	                        .append("\" estado=\"")
	                        .append(escapeAttr(safeUpper(p.getEstado())))
	                        .append("\" centroActual=\"")
	                        .append(escapeAttr(p.getCentroActual()))
	                        .append("\"/>\n");
	            }
	        }
	        xml.append("  </paquetes>\n");
	        xml.append("  <solicitudes>\n");
	        if (solicitudesPendientes != null) {
	            for (Solicitudes s : solicitudesPendientes) {
	                if (s == null) continue;
	                xml.append("    <solicitud id=\"")
	                        .append(escapeAttr(s.getId()))
	                        .append("\" estado=\"")
	                        .append(escapeAttr(safeUpper(s.getEstado()))) 
	                        .append("\" paquete=\"")
	                        .append(escapeAttr(s.getPaqueteId()))
	                        .append("\"/>\n");
	            }
	        }
	        if (solicitudesAtendidasList != null) {
	            for (Solicitudes s : solicitudesAtendidasList) {
	                if (s == null) continue;
	                xml.append("    <solicitud id=\"")
	                        .append(escapeAttr(s.getId()))
	                        .append("\" estado=\"")
	                        .append(escapeAttr(safeUpper(s.getEstado())))
	                        .append("\" paquete=\"")
	                        .append(escapeAttr(s.getPaqueteId()))
	                        .append("\"/>\n");
	            }
	        }

	        xml.append("  </solicitudes>\n");

	        xml.append("</resultadoLogitrack>\n");
	        return xml.toString();
	    }

	    private static String safeUpper(String s) {
	        if (s == null) return "";
	        return s.trim().toUpperCase();
	    }

	    private static String escapeAttr(String s) {
	        if (s == null) return "";
	        return s.replace("&", "&amp;")
	                .replace("\"", "&quot;")
	                .replace("<", "&lt;")
	                .replace(">", "&gt;");
	    }

}
