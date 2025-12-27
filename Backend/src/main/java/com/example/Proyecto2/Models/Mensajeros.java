package com.example.Proyecto2.Models;


public class Mensajeros {
	
	private String id;
	private String nombre;
	private String estado;
	private String centroId;
	private Double capacidad;
	
	public Mensajeros() {
		
	}
	

	public Mensajeros(String id, String nombre, String estado, String centroId, Double capacidad) {
		this.id = id;
		this.nombre = nombre;
		this.estado = estado;
		this.centroId = centroId;
		this.capacidad = capacidad;
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCentroId() {
		return centroId;
	}
	public void setCentroId(String centroId) {
		this.centroId = centroId;
	}
	public Double getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(Double capacidad) {
		this.capacidad = capacidad;
	}

}
