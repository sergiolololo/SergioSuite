package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class CGTUnvaBean extends ServicioUnvaBean {

	public CGTUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
		JsonNode nodeInfoSRV = node.get(ServicioEnum.CGT.getTipoActivoCodi()).elements().next();
		setearCampos(nodeInfoSRV, ServicioEnum.CGT.getTipoActivoExcel(), ServicioEnum.CGT.getTipoActivoCodi(),  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.CGT.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.CGT.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.CGT.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.CGT.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.CGT.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.CGT.getTipoActivoUnva();
	}
	@Override
	protected void setGenerarNode() {
		setGenerarNode(true);
	}
}