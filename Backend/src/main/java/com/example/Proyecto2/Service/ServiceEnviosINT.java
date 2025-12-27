package com.example.Proyecto2.Service;
import com.example.Proyecto2.Models.Paquetes;


public interface ServiceEnviosINT {
	boolean asignarMensajero(String paqueteId, String mensajeroId); 
	Paquetes cambiarEstadoEnvio(String paqueteId, String nuevoEstado);
	

}
