package com.example.Proyecto2.Service;
import java.util.List;
import com.example.Proyecto2.Models.Solicitudes;
import com.example.Proyecto2.Models.RespuestasProcesamiento;

public interface ServiceSolicitudINT {
	List<Solicitudes> obtenerSolicitudes();
	
	Solicitudes crearSolicitud(Solicitudes solicitudes);
	
	boolean eliminarSolicitud(String id);
	
	RespuestasProcesamiento procesarMayorPrioridad();
	RespuestasProcesamiento procesarN(int n);
	void limpiar();
}
