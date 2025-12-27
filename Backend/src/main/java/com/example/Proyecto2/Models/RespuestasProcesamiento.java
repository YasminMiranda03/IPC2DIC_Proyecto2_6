package com.example.Proyecto2.Models;
import java.util.List;



public class RespuestasProcesamiento {
	
	private boolean exito;
	private String motivo;
	private List<String> solicitudesAtendidas;
	public RespuestasProcesamiento() {
		
	}
	
	public RespuestasProcesamiento(boolean exito, String motivo, List<String> solicitudesAtendidas ) {
		this.exito = exito;
		this.motivo = motivo;
		this.solicitudesAtendidas = solicitudesAtendidas;
}

	 public boolean isExito() {
	        return exito;
	    }

	    public void setExito(boolean exito) {
	        this.exito = exito;
	    }

	    public String getMotivo() {
	        return motivo;
	    }

	    public void setMotivo(String motivo) {
	        this.motivo = motivo;
	    }

	    public List<String> getSolicitudesAtendidas() {
	        return solicitudesAtendidas;
	    }

	    public void setSolicitudesAtendidas(List<String> solicitudesAtendidas) {
	        this.solicitudesAtendidas = solicitudesAtendidas;

}
}