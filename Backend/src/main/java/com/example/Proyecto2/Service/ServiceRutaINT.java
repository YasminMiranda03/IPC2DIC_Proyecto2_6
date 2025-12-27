package com.example.Proyecto2.Service;
import java.util.List;
import com.example.Proyecto2.Models.Rutas;

public interface ServiceRutaINT {
	
	List<Rutas> obtenerRutas();
	
	Rutas obtenerRutaPorId(String id);
	
	Rutas crearRuta(Rutas ruta);
	
	Rutas actualizarRuta(String id, Rutas rutaActualizada);
	
	boolean eliminarRuta(String id);
	
	//valide si existe  origen y el destino
	boolean existeRuta(String id);
	void limpiar();

}
