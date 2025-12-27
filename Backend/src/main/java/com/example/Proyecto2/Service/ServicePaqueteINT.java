package com.example.Proyecto2.Service;
import java.util.List;
import com.example.Proyecto2.Models.Paquetes;


public interface ServicePaqueteINT {
	List<Paquetes> obtenerPaquetes();
	
	Paquetes obtenerPaquetePorId(String id);
	
	Paquetes crearPaquete(Paquetes paquete);
	
	Paquetes actualizarPaquete(String id, Paquetes paqueteActualizado);
	
	List<Paquetes> obtenerPaquetesPorCentro(String centroId);
	int contarPaquetesEnCentro(String centroId);
	
	
	boolean eliminarPaquete(String id);
	void limpiar();
}
