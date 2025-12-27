package com.example.Proyecto2.Service;
import java.util.List;
import com.example.Proyecto2.Models.Centro;


public interface ServiceCentroINT {
	List<Centro> obtenerCentros();
	
	Centro obtenerCentroPorId(String id);
	
	Centro crearCentro(Centro centro);
	
	Centro actualizarCentro(String id, Centro centroActualizado);
	
	boolean eliminarCentro(String id);
	
	//Sirve para verificar si el centro existe 
	boolean existeCentro(String id);
	boolean agregarMensajeroACentro(String centroId, String mensajeroId);
	boolean quitarMensajeroDeCentro(String centroId, String mensajeroId);
	void limpiar();

}
