package com.example.Proyecto2.Service;
import java.util.List;
import com.example.Proyecto2.Models.Mensajeros;

public interface ServiceMensajerosINT {
	List<Mensajeros> obtenerMensajeros();
	List<Mensajeros> obtenerMensajerosPorCentro(String centroId);

	
	Mensajeros obtenerMensajeroPorId(String id);
	
	Mensajeros crearMensajero(Mensajeros mensajeros);
	
	Mensajeros cambiarEstado(String id, String nuevoEstado);
	
	
	Mensajeros reasignarCentro(String id, String nuevoCentroId);
	void limpiar();
}