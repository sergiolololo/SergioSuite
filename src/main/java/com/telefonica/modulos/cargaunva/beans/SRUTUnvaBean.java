package com.telefonica.modulos.cargaunva.beans;

import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class SRUTUnvaBean extends ServicioUnvaBean {

	public SRUTUnvaBean() {
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.SRUT.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.SRUT.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.SRUT.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.SRUT.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.SRUT.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.SRUT.getTipoActivoUnva();
	}
}