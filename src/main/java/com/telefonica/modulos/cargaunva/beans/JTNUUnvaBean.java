package com.telefonica.modulos.cargaunva.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

import java.util.Iterator;

public class JTNUUnvaBean extends ServicioUnvaBean {

	public JTNUUnvaBean(JsonNode node, String aplicacion, String pesp, String ruta2DF) {
		Iterator<JsonNode> iterator = node.get("jt-nuc").elements();
		JsonNode nodeInfoSRV = iterator.next();
		String implementacion = nodeInfoSRV.get("implementationTechnology").asText();
		String tipoActivo = null;
		String tipoActivo1CO = null;
		if(implementacion.equals("JEE")) {
			tipoActivo = ServicioEnum.JTNU.getTipoActivoExcel();
			tipoActivo1CO = ServicioEnum.JTNU.getTipoActivoCodi();
		}
		setearCampos(nodeInfoSRV, tipoActivo, tipoActivo1CO,  aplicacion, pesp, ruta2DF);
		setNodeInfoSRV(nodeInfoSRV);
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.JTNU.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.JTNU.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.JTNU.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.JTNU.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.JTNU.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.JTNU.getTipoActivoUnva();
	}
}