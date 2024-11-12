package com.telefonica.modulos.cargaunva.beans;

import com.telefonica.modulos.cargaunva.enumm.ServicioEnum;

public class RTUnvaBean extends ServicioUnvaBean {

	public RTUnvaBean() {
	}

	@Override
	protected String getCarcasaNombreDTD(){
		return ServicioEnum.RT.getNombreDTD();
	}
	@Override
	protected String getCarcasaGuidDTD(){
		return ServicioEnum.RT.getGuidDTD();
	}
	@Override
	protected String calcularInicioRuta2DF(String ruta){
		return ruta + ServicioEnum.RT.getInicioRuta2DF();
	}
	@Override
	protected void setRuta0AT(){
		setRuta0AT(ServicioEnum.RT.getRuta0AT());
	}
	@Override
	protected void setRuta1CO(){
		setRuta1CO(ServicioEnum.RT.getRuta1CO());
	}
	@Override
	protected String getInicioNombreCompletoServicio() {
		return ServicioEnum.RT.getTipoActivoUnva();
	}
}