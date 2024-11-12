package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

import java.util.Iterator;

public class SRNSUnvaBean extends ServicioUnvaBean {

	public SRNSUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF, boolean esV4) {
		Iterator<JsonNode> iterator = node.get(esV4?"srv-nuc-v4":"srv-nuc").elements();
		JsonNode nodeInfoSRV = iterator.next();
		String implementacion = nodeInfoSRV.get("implementationTechnology").asText();
		String tipoActivo = null;
		String tipoActivo1CO = null;
		if(!implementacion.equals("JEE")) {
			tipoActivo = ServicioEnum.SRNS.getTipoActivoExcel();
			tipoActivo1CO = ServicioEnum.SRNS.getTipoActivoCodi();
		}
		setearCampos(nodeInfoSRV, tipoActivo, tipoActivo1CO,  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.SRNS.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.SRNS.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.SRNS.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.SRNS.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.SRNS.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.SRNS.getTipoActivoUnva();
	}
}