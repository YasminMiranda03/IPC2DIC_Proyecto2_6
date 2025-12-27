package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Centro;
import com.example.Proyecto2.Models.Mensajeros;
import com.example.Proyecto2.Models.Paquetes;
import com.example.Proyecto2.Models.Rutas;
import com.example.Proyecto2.Models.Solicitudes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
public class ServiceImportarImpl implements ServiceImportarINT {

	private final ServiceCentroINT serviceCentro;
    private final ServiceRutaINT serviceRuta;
    private final ServiceMensajerosINT serviceMensajeros;
    private final ServicePaqueteINT servicePaquete;
    private final ServiceSolicitudINT serviceSolicitud;

    public ServiceImportarImpl(
            ServiceCentroINT serviceCentro,
            ServiceRutaINT serviceRuta,
            ServiceMensajerosINT serviceMensajeros,
            ServicePaqueteINT servicePaquete,
            ServiceSolicitudINT serviceSolicitud
    ) {
        this.serviceCentro = serviceCentro;
        this.serviceRuta = serviceRuta;
        this.serviceMensajeros = serviceMensajeros;
        this.servicePaquete = servicePaquete;
        this.serviceSolicitud = serviceSolicitud;
    }

    @Override
    public String importarXml(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "ERROR: Archivo vacio";
        }
        serviceCentro.limpiar();
        serviceRuta.limpiar();
        serviceMensajeros.limpiar();
        servicePaquete.limpiar();
        serviceSolicitud.limpiar();

        int creadosCentros = 0;
        int creadosRutas = 0;
        int creadosMensajeros = 0;
        int creadosPaquetes = 0;
        int creadasSolicitudes = 0;

        List<String> errores = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            if (root == null) return "ERROR: en el XML ";

            Element config = firstDirectChild(root, "configuracion");
            if (config == null) {
                return "ERROR: No se encontró el nodo <configuracion>.";
            }

            Element centrosEl = firstDirectChild(config, "centros");
            if (centrosEl != null) {
                NodeList centros = centrosEl.getElementsByTagName("centro");
                for (int i = 0; i < centros.getLength(); i++) {
                    Node n = centros.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                    try {
                        Element cEl = (Element) n;
                        String id = attr(cEl, "id");
                        String nombre = text(cEl, "nombre");
                        String ciudad = text(cEl, "ciudad");
                        Integer capacidad = intText(cEl, "capacidad");
                        logLinea("[CENTRO] id=" + id + ", nombre=" + nombre + ", ciudad=" + ciudad + ", capacidad=" + capacidad);//Ht3

                        if (isBlank(id) || isBlank(nombre) || isBlank(ciudad) || capacidad == null || capacidad <= 0) {
                            errores.add("CENTRO inválido en index " + i);
                            continue;
                        }

                        Centro creado = serviceCentro.crearCentro(new Centro(id, nombre, ciudad, capacidad));
                        if (creado == null) {
                            errores.add("Centro duplicado : " + id);
                            continue;
                        }
                        creadosCentros++;

                    } catch (Exception e) {
                        errores.add("CENTRO error en index " + i + ": " + e.getMessage());
                    }
                }
            }
            Element rutasEl = firstDirectChild(config, "rutas");
            if (rutasEl != null) {
                NodeList rutas = rutasEl.getElementsByTagName("ruta");
                for (int i = 0; i < rutas.getLength(); i++) {
                    Node n = rutas.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                    try {
                        Element rEl = (Element) n;
                        String id = attr(rEl, "id");
                        String origen = attr(rEl, "origen");
                        String destino = attr(rEl, "destino");
                        Double distancia = doubleAttr(rEl, "distancia");
                        logLinea("[RUTA] id=" + id + ", origen=" + origen + ", destino=" + destino + ", distancia=" + distancia);//Ht3

                        if (isBlank(id) || isBlank(origen) || isBlank(destino) || distancia == null || distancia <= 0) {
                            errores.add("RUTA inválida en index " + i);
                            continue;
                        }

                        Rutas creado = serviceRuta.crearRuta(new Rutas(id, origen, destino, distancia));
                        if (creado == null) {
                            errores.add("RUTA omitida (duplicada, inválida o centros no existen): " + id);
                            continue;
                        }
                        creadosRutas++;

                    } catch (Exception e) {
                        errores.add("RUTA error en index " + i + ": " + e.getMessage());
                    }
                }
            }

            Element mensajerosEl = firstDirectChild(config, "mensajeros");
            if (mensajerosEl != null) {
                NodeList mens = mensajerosEl.getElementsByTagName("mensajero");
                for (int i = 0; i < mens.getLength(); i++) {
                    Node n = mens.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                    try {
                        Element mEl = (Element) n;
                        String id = attr(mEl, "id");
                        String nombre = attr(mEl, "nombre");
                        Double capacidad = doubleAttr(mEl, "capacidad");
                        String centroId = attr(mEl, "centro");
                        String estado = "DISPONIBLE";
                        logLinea("[MENSAJERO] id=" + id + ", nombre=" + nombre + ", capacidad=" + capacidad +
                                ", centro=" + centroId + ", estado=" + estado);//Ht3 norrar

                        if (isBlank(id) || isBlank(nombre) || isBlank(centroId) || capacidad == null || capacidad <= 0) {
                            errores.add("MENSAJERO inválido (campos faltantes o capacidad <= 0) en index " + i);
                            continue;
                        }

                        Mensajeros creado = serviceMensajeros.crearMensajero(
                                new Mensajeros(id, nombre, estado, centroId, capacidad)
                        );
                        if (creado == null) {
                            errores.add("MENSAJERO omitido (duplicado, centro no existe o datos inválidos): " + id);
                            continue;
                        }
                        creadosMensajeros++;

                    } catch (Exception e) {
                        errores.add("MENSAJERO error en index " + i + ": " + e.getMessage());
                    }
                }
            }

            Element paquetesEl = firstDirectChild(config, "paquetes");
            if (paquetesEl != null) {
                NodeList packs = paquetesEl.getElementsByTagName("paquete");
                for (int i = 0; i < packs.getLength(); i++) {
                    Node n = packs.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                    try {
                        Element pEl = (Element) n;
                        String id = attr(pEl, "id");
                        String cliente = attr(pEl, "cliente");
                        Double peso = doubleAttr(pEl, "peso");
                        String destino = attr(pEl, "destino");
                        String estado = attr(pEl, "estado");
                        String centroActual = attr(pEl, "centroActual");
                        logLinea("[PAQUETE] id=" + id + ", cliente=" + cliente + ", peso=" + peso +
                                ", destino=" + destino + ", estado=" + estado + ", centroActual=" + centroActual);//borrarHt3

                        if (isBlank(id) || isBlank(cliente) || isBlank(destino) || isBlank(centroActual) || peso == null || peso <= 0) {
                            errores.add("PAQUETE inválido en index " + i);
                            continue;
                        }

                        if (isBlank(estado)) estado = "PENDIENTE";

                        Paquetes creado = servicePaquete.crearPaquete(
                                new Paquetes(id, cliente, peso, destino, estado, centroActual)
                        );
                        if (creado == null) {
                            errores.add("PAQUETE duplicado o no existe: " + id);
                            continue;
                        }
                        creadosPaquetes++;

                    } catch (Exception e) {
                        errores.add("PAQUETE error en index " + i + ": " + e.getMessage());
                    }
                }
            }

            Element solicitudesEl = firstDirectChild(config, "solicitudes");
            if (solicitudesEl != null) {
                NodeList sols = solicitudesEl.getElementsByTagName("solicitud");
                for (int i = 0; i < sols.getLength(); i++) {
                    Node n = sols.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE) continue;

                    try {
                        Element sEl = (Element) n;
                        String id = attr(sEl, "id");
                        String tipo = attr(sEl, "tipo");
                        String paqueteId = attr(sEl, "paquete");
                        Integer prioridad = intAttr(sEl, "prioridad");
                        logLinea("[SOLICITUD] id=" + id + ", tipo=" + tipo + ", paquete=" + paqueteId + ", prioridad=" + prioridad);//Ht3 borrar


                        if (isBlank(id) || isBlank(tipo) || isBlank(paqueteId) || prioridad == null ) {
                            errores.add("SOLICITUD inválida en index " + i);
                            continue;
                        }
                        if (servicePaquete.obtenerPaquetePorId(paqueteId) == null) {
                            errores.add("SOLICITUD omitida: paquete no existe (" + paqueteId + ") en solicitud " + id);
                            continue;
                        }

                        Solicitudes creada = serviceSolicitud.crearSolicitud(
                                new Solicitudes(id, tipo, paqueteId, prioridad)
                        );
                        if (creada == null) {
                            errores.add("SOLICITUD duplicada : " + id);
                            continue;
                        }
                        creadasSolicitudes++;

                    } catch (Exception e) {
                        errores.add("SOLICITUD error en index " + i + ": " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            return "ERROR: XML inválido o no se pudo procesar. Detalle: " + e.getMessage();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("IMPORTACIÓN COMPLETA\n");
        sb.append("Centros creados: ").append(creadosCentros).append("\n");
        sb.append("Rutas creadas: ").append(creadosRutas).append("\n");
        sb.append("Mensajeros creados: ").append(creadosMensajeros).append("\n");
        sb.append("Paquetes creados: ").append(creadosPaquetes).append("\n");
        sb.append("Solicitudes creadas: ").append(creadasSolicitudes).append("\n");
        sb.append("Errores: ").append(errores.size()).append("\n");

        if (!errores.isEmpty()) {
            sb.append("----- LISTA DE ERRORES -----\n");
            for (String err : errores) {
                sb.append("- ").append(err).append("\n");
            }
        }

        return sb.toString();
    }
    private static Element firstDirectChild(Element parent, String tag) {
        if (parent == null) return null;
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && tag.equals(n.getNodeName())) {
                return (Element) n;
            }
        }
        return null;
    }

    private static String text(Element parent, String tag) {
        Element el = firstDirectChild(parent, tag);
        if (el == null) return null;
        String t = el.getTextContent();
        return (t == null) ? null : t.trim();
    }

    private static String attr(Element el, String name) {
        if (el == null) return null;
        String v = el.getAttribute(name);
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    private static Integer intText(Element parent, String tag) {
        try {
            String v = text(parent, tag);
            if (v == null) return null;
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer intAttr(Element el, String name) {
        try {
            String v = attr(el, name);
            if (v == null) return null;
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Double doubleAttr(Element el, String name) {
        try {
            String v = attr(el, name);
            if (v == null) return null;
            return Double.parseDouble(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    //borrar esto es para la ht3
    private static void logLinea(String msg) {
        System.out.println(msg);
    }
}
