package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;

public class ComunActivoBean {

	private String nombreActivo;
	private String guid;
	private String version;
	private String revision;
	private String tipoActivo;
	private String tipoActivoCodi;
	private String aplicacion;
	private String arquitectura;
	
	public ComunActivoBean() {
	}
	
	public ComunActivoBean(JsonNode nodeInfoSRV, String tipoActivo, String tipoActivoCodi, String aplicacion) {
		setearCampos(nodeInfoSRV, tipoActivo, tipoActivoCodi, aplicacion);
	}

	public void setearCampos(JsonNode nodeInfoSRV, String tipoActivo, String tipoActivoCodi, String aplicacion){
		String nombreActivo = nodeInfoSRV.get("name").asText();
		String version = nodeInfoSRV.get("version").asText();
		String revision = nodeInfoSRV.get("revision").asText();
		String arquitectura = nodeInfoSRV.get("versionAT") != null?nodeInfoSRV.get("versionAT").asText():"3";
		String guid = nodeInfoSRV.get("guid").asText();

		setArquitectura(arquitectura);
		setNombreActivo(nombreActivo);
		setGuid(guid);
		setVersion(version);
		setRevision(revision);
		setTipoActivo(tipoActivo);
		setTipoActivoCodi(tipoActivoCodi);
		setAplicacion(aplicacion);
	}
	
	// getters and setters
	public String getNombreActivo() {
		return nombreActivo;
	}
	public void setNombreActivo(String nombreActivo) {
		this.nombreActivo = nombreActivo;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getTipoActivo() {
		return tipoActivo;
	}
	public void setTipoActivo(String tipoActivo) {
		this.tipoActivo = tipoActivo;
	}
	public String getTipoActivoCodi() {
		return tipoActivoCodi;
	}
	public void setTipoActivoCodi(String tipoActivoCodi) {
		this.tipoActivoCodi = tipoActivoCodi;
	}
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public String getArquitectura() {
		return arquitectura;
	}
	public void setArquitectura(String arquitectura) {
		this.arquitectura = arquitectura;
	}
}
