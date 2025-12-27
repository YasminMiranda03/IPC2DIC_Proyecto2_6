package com.example.Proyecto2.Models;

public class Rutas {
	private String id;
	private String origen;
	private String destino;
	private Double distancia;
	
	public Rutas() {
	}
	
	public Rutas(String id, String origen, String destino, Double distancia) {
		this.id = id;
		this.origen = origen;
		this.destino = destino;
		this.distancia = distancia;
	}
	
	public String getId() {
		return id; 
	}
    public void setId(String id) {
    	this.id = id;
    }
    public String getOrigen() {
    	return origen;
    }
    public void setOrigen(String origen) {
    	this.origen = origen;
    }
    public String getDestino() {
    	return destino;
    }
    public void setDestino(String destino) {
    	this.destino = destino; 
    }

    public Double getDistancia() {
    	return distancia;
    }
    public void setDistancia(Double distancia) {
    	this.distancia = distancia;
    }
	
}
