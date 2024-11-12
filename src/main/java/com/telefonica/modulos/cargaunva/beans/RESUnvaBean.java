package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

import java.util.Iterator;

public class RESUnvaBean extends ServicioUnvaBean {

	public RESUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
        Iterator<JsonNode> iterator = node.get("res-nuc").elements();
        JsonNode nodeInfoSRV = iterator.next();
        String implementacion = nodeInfoSRV.get("implementationTechnology").asText();
        String tipoActivo = null;
        String tipoActivo1CO = null;
        if(implementacion.equals("JEE")) {
            tipoActivo = ServicioEnum.RES.getTipoActivoExcel();
            tipoActivo1CO = ServicioEnum.RES.getTipoActivoCodi();
        }
        setearCampos(nodeInfoSRV, tipoActivo, tipoActivo1CO, aplicacion, pesp, ruta2DF);
        setNodeInfoSRV(nodeInfoSRV);
    }

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.RES.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.RES.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.RES.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.RES.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.RES.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.RES.getTipoActivoUnva();
	}
}