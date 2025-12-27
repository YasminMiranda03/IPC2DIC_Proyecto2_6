package com.example.Proyecto2.Models;
import java.util.List;
import java.util.ArrayList;



public class Centro {
	private String id;
	private String nombre;
	private String ciudad;
	private Integer capacidad;
	
	 private List<String> mensajerosIds = new ArrayList<>();
	
	public Centro() {
	}
	
	public Centro(String id, String nombre, String ciudad, Integer capacidad) {
		this.id = id;
		this.nombre = nombre;
		this.ciudad = ciudad;
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
	public String getCiudad() {
		return ciudad; 
	}
	public void setCiudad(String ciudad) { 
		this.ciudad = ciudad; 
	}
	public Integer getCapacidad() {
		return capacidad; 
	}
	public void setCapacidad(Integer capacidad) { 
		this.capacidad = capacidad; 
	}
	public List<String> getMensajerosIds() { 
		return mensajerosIds; 
	}
    public void setMensajerosIds(List<String> mensajerosIds) { 
    	this.mensajerosIds = mensajerosIds; 
    }
}
