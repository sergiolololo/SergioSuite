package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;

public class ServicioUnvaBean extends ComunActivoBean {

	private String pesp;
	private String nombreDTD;
	private String guidDTD;
	private String inicioRuta2DF;
	private String ruta0AT;
	private String ruta1CO;
	private boolean generarNode;
	private JsonNode nodeInfoSRV;
	
	public ServicioUnvaBean() {
	}
	
	public ServicioUnvaBean(JsonNode nodeInfoSRV, String tipoActivo, String tipoActivoCodi, String aplicacion, String pesp, String inicioRuta2DF) {
		this.setearCampos(nodeInfoSRV, tipoActivo, tipoActivoCodi, aplicacion, pesp, inicioRuta2DF);
	}

	public void setearCampos(JsonNode nodeInfoSRV, String tipoActivo, String tipoActivoCodi, String aplicacion, String pesp, String inicioRuta2DF){
		super.setearCampos(nodeInfoSRV, tipoActivo, tipoActivoCodi, aplicacion);

		setPesp(pesp);
		setRuta0AT();
		setRuta1CO();
		generarNombreDTD();
		generarGuidDTD();
		setInicioRuta2DF(inicioRuta2DF);
		setGenerarNode();
	}

	private void generarNombreDTD() {
		String carcasaNombreDTD = getCarcasaNombreDTD();
		carcasaNombreDTD = StringUtils.replaceOnce(carcasaNombreDTD, "?", getAplicacion());
		carcasaNombreDTD = StringUtils.replaceOnce(carcasaNombreDTD, "?", getNombreActivo());
		carcasaNombreDTD = StringUtils.replaceOnce(carcasaNombreDTD, "?", getVersion());
		carcasaNombreDTD = StringUtils.replaceOnce(carcasaNombreDTD, "?", getRevision());
		setNombreDTD(carcasaNombreDTD);
	}
	
	private void generarGuidDTD() {
		String carcasaGuidDTD = getCarcasaGuidDTD();
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getNombreActivo());
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getVersion());
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getRevision());
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getPesp());
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getArquitectura());
		carcasaGuidDTD = StringUtils.replaceOnce(carcasaGuidDTD, "?", getNombreDTD());
		setGuidDTD(carcasaGuidDTD);
	}
	
	private String generarRuta0AT(String ruta0AT, String tipoActivoCodi) {
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", getAplicacion());
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", tipoActivoCodi);
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", tipoActivoCodi);
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", getNombreActivo());
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", getVersion());
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", getRevision());
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", tipoActivoCodi);
		ruta0AT = StringUtils.replaceOnce(ruta0AT, "?", getNombreActivo());
		return ruta0AT;
	}

	private String generarRuta1CO(String ruta1CO, String tipoActivoCodi) {
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", getAplicacion());
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", tipoActivoCodi);
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", tipoActivoCodi);
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", getNombreActivo());
		
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", getVersion());
		String pespReducido = getPesp().substring(0, getPesp().indexOf("_", getPesp().indexOf("_") + 1));
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", pespReducido);
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", tipoActivoCodi);
		ruta1CO = StringUtils.replaceOnce(ruta1CO, "?", getNombreActivo());
		return ruta1CO;
	}

	public String getNombreDTD() {
		return nombreDTD;
	}
	public void setNombreDTD(String nombreDTD) {
		this.nombreDTD = nombreDTD;
	}
	public String getGuidDTD() {
		return guidDTD;
	}
	public void setGuidDTD(String guidDTD) {
		this.guidDTD = guidDTD;
	}
	public String getPesp() {
		return pesp;
	}
	public void setPesp(String pesp) {
		this.pesp = pesp;
	}
	public String getInicioRuta2DF() {
		return inicioRuta2DF;
	}
	public void setInicioRuta2DF(String inicioRuta2DF) {
		this.inicioRuta2DF = calcularInicioRuta2DF(inicioRuta2DF);
	}
	public String getRuta0AT(String tipoActivoCodi) {
		return generarRuta0AT(ruta0AT, tipoActivoCodi);
	}
	public void setRuta0AT(String ruta0AT) {
		this.ruta0AT = ruta0AT;
	}
	public String getRuta1CO(String tipoActivoCodi) {
		return generarRuta1CO(ruta1CO, tipoActivoCodi);
	}
	public void setRuta1CO(String ruta1CO) {
		this.ruta1CO = ruta1CO;
	}
	protected String getInicioNombreCompletoServicio() {
		return null;
	}
	public String getFqn() {
		return getInicioNombreCompletoServicio() + getNombreActivo();
	}
	public boolean isGenerarNode() {
		return generarNode;
	}
	public void setGenerarNode(boolean generarNode) {
		this.generarNode = generarNode;
	}
	public JsonNode getNodeInfoSRV() {
		return nodeInfoSRV;
	}
	public void setNodeInfoSRV(JsonNode nodeInfoSRV) {
		this.nodeInfoSRV = nodeInfoSRV;
	}

	public void writeFichero0AT(BufferedWriter writer, String tipoActivoCodi) {
		try {
			writer.write("\n" + "svn co --depth immediates " + getRuta0AT(tipoActivoCodi));
			writer.write("\n" + "for directorio in ./" + tipoActivoCodi + "-" + getNombreActivo() + "/* ; do");
			writer.write("\n" + "	echo $directorio");
			writer.write("\n" + "	svn update --set-depth infinity $directorio;");
			writer.write("\n" + "done");
		} catch (IOException e) {
			System.out.println("Error al escribir en el fichero de 0-AT");
		}
	}
	
	public void writeFichero1CO(BufferedWriter writer, String tipoActivoCodi) {
		try {
			writer.write("\n" + "svn co --depth immediates " + getRuta1CO(tipoActivoCodi));
			writer.write("\n" + "for directorio in ./" + tipoActivoCodi + "-" + getNombreActivo() + "/* ; do");
			writer.write("\n" + "	echo $directorio");
			writer.write("\n" + "	svn update --set-depth infinity $directorio;");
			writer.write("\n" + "done");
		} catch (IOException e) {
			System.out.println("Error al escribir en el fichero de 1-CO");
		}
	}

	protected String getCarcasaNombreDTD(){
		return "";
	}
	protected String getCarcasaGuidDTD(){
		return "";
	}
	protected String calcularInicioRuta2DF(String ruta){
		return "";
	}
	protected void setRuta0AT(){
	}
	protected void setRuta1CO(){
	}
	protected void setGenerarNode() {
		setGenerarNode(false);
	}
}