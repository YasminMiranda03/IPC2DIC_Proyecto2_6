package com.example.Proyecto2.Models;

public class Paquetes {
	private String id;
	private String cliente;
	private Double peso;
	private String destino;
	private String estado;
	private String centroActual;
	
	public Paquetes() {
		
	}
	
	 public Paquetes(String id, String cliente, Double peso, String destino, String estado, String centroActual) {
	        this.id = id;
	        this.cliente = cliente;
	        this.peso = peso;
	        this.destino = destino;
	        this.estado = estado;
	        this.centroActual = centroActual;
	 }

	 public String getId() { 
		 return id; 
	}
	 public void setId(String id) {
		 this.id = id; 
	}
	 public String getCliente() { 
		 return cliente; 
	}
	 public void setCliente(String cliente) {
		 this.cliente = cliente; 
	}

	 public Double getPeso() { 
		 return peso; 
	}
	 public void setPeso(Double peso) {
		 this.peso = peso; 
	}

	 public String getDestino() { 
		 return destino; 
	}
	 public void setDestino(String destino) {
		 this.destino = destino; 
	}

	 public String getEstado() {
		 return estado; 
	}
	 public void setEstado(String estado) {
		 this.estado = estado;
	}

	 public String getCentroActual() {
		 return centroActual; 
	}
	 public void setCentroActual(String centroActual) { 
		 this.centroActual = centroActual;
	}
	 private String mensajeroId;
	 public String getMensajeroId() { return mensajeroId; }
	 public void setMensajeroId(String mensajeroId) { this.mensajeroId = mensajeroId; }
	 
}
