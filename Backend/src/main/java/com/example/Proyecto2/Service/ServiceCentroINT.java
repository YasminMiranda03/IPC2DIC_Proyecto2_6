package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Mensajeros;

import com.example.Proyecto2.Models.Centro;
import java.util.List;

public interface ServiceCentroINT {
    List<Centro> obtenerCentros();
    List<Mensajeros> obtenerMensajerosDelCentro(String centroId);

    Centro obtenerCentroPorId(String id);

    Centro crearCentro(Centro centro);
    Centro actualizarCentro(String id, Centro centroActualizado);
    boolean eliminarCentro(String id);

    boolean existeCentro(String id);

    boolean agregarMensajeroACentro(String centroId, String mensajeroId);
    boolean quitarMensajeroDeCentro(String centroId, String mensajeroId);

    void limpiar();
}
