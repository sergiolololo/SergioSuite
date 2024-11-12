package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class CNTUnvaBean extends ServicioUnvaBean {

	public CNTUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
		JsonNode nodeInfoSRV = node.get(ServicioEnum.CNT.getTipoActivoCodi()).elements().next();
		setearCampos(nodeInfoSRV, ServicioEnum.CNT.getTipoActivoExcel(), ServicioEnum.CNT.getTipoActivoCodi(),  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.CNT.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.CNT.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.CNT.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.CNT.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.CNT.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.CNT.getTipoActivoUnva();
	}
	@Override
	protected void setGenerarNode() {
		setGenerarNode(true);
	}
}