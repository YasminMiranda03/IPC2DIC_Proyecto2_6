	package com.example.Proyecto2.Models;
	
	public class Solicitudes {
	    private String id;
	    private String tipo;
	    private String paqueteId;
	    private Integer prioridad;

	    private String estado = "PENDIENTE";

	    public Solicitudes() { }

	    public Solicitudes(String id, String tipo, String paqueteId, Integer prioridad) {
	        this.id = id;
	        this.tipo = tipo;
	        this.paqueteId = paqueteId;
	        this.prioridad = prioridad;
	        this.estado = "PENDIENTE";
	    }

	    public String getId() {
	        return id;
	    }
	    public void setId(String id) {
	        this.id = id;
	    }

	    public String getTipo() {
	        return tipo;
	    }
	    public void setTipo(String tipo) {
	        this.tipo = tipo;
	    }

	    public String getPaqueteId() {
	        return paqueteId;
	    }
	    public void setPaqueteId(String paqueteId) {
	        this.paqueteId = paqueteId;
	    }

	    public Integer getPrioridad() {
	        return prioridad;
	    }
	    public void setPrioridad(Integer prioridad) {
	        this.prioridad = prioridad;
	    }

	    public String getEstado() {
	        return estado;
	    }
	    public void setEstado(String estado) {
	        this.estado = estado;
	    }
	}
