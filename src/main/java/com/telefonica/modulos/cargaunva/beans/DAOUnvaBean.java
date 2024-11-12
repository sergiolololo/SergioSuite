package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class DAOUnvaBean extends ServicioUnvaBean {

	public DAOUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
		JsonNode nodeInfoSRV = node.get(ServicioEnum.DAO.getTipoActivoCodi()).elements().next();
		setearCampos(nodeInfoSRV, ServicioEnum.DAO.getTipoActivoExcel(), ServicioEnum.DAO.getTipoActivoCodi(),  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.DAO.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.DAO.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.DAO.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.DAO.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.DAO.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.DAO.getTipoActivoUnva();
	}
}