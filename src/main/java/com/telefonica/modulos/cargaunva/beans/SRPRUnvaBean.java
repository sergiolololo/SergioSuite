package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class SRPRUnvaBean extends ServicioUnvaBean {

	public SRPRUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
		JsonNode nodeInfoSRV = node.get(ServicioEnum.SRPR.getTipoActivoCodi()).elements().next();
		setearCampos(nodeInfoSRV, ServicioEnum.SRPR.getTipoActivoExcel(), ServicioEnum.SRPR.getTipoActivoCodi(),  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.SRPR.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.SRPR.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.SRPR.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.SRPR.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.SRPR.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.SRPR.getTipoActivoUnva();
	}
}