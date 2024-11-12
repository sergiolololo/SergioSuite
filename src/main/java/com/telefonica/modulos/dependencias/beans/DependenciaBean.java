package com.telefonica.modulos.dependencias.beans;

public class DependenciaBean {

	private String nombre;
	private String tipo;
	private String aplicacion;
	private String causal;
	private String proceso;
	private String version;
	private boolean cambioMajor;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public String getCausal() {
		return causal;
	}
	public void setCausal(String causal) {
		this.causal = causal;
	}
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isCambioMajor() {
		return cambioMajor;
	}
	public void setCambioMajor(boolean cambioMajor) {
		this.cambioMajor = cambioMajor;
	}
}