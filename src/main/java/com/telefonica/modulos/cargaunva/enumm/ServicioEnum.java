package com.telefonica.modulos.cargaunva.enumm;

public enum ServicioEnum {

	SRNU(
			"DTD_?_SRV-NUC-JEE_?_v?-?-1",
			"srv-nuc/srv-nuc-?/branches/v?.?_?_v?/doc/?",
			"\\srv-nuc\\srv-nuc-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"SRNU",
			"srv-nuc-jee",
			"SRV_"
			),
	SRNS(
			"DTD_?_SRV-NUC-SOA_?_v?-?-1",
			"srv-nuc/srv-nuc-?/branches/v?.?_?_v?/doc/?",
			"\\srv-nuc\\srv-nuc-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"SRNS",
			"srv-nuc-soa",
			"SRV_"
			),
	SRPR(
			"DTD_?_SRV-PRES-JEE_?_v?-?-1",
			"srv-pres/srv-pres-?/branches/v?.?_?_v?/doc/?",
			"\\srv-pres\\srv-pres-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"SRPR",
			"srv-pres",
			"SRV_"
			),
	SRUT(
			"DTD_?_SRV-UTIL-JEE_?_v?-?-1",
			"srv-util/srv-util-?/branches/v?.?_?_v?/doc/?",
			"\\srv-util\\srv-util-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"",
			"",
			"SRV_"
			),
	CNT(
			"DTD_?_CNT_?_v?-?-1",
			"cnt/cnt-?/branches/v?.?_?_v?/doc/?",
			"\\cnt\\cnt-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"CNT",
			"cnt",
			"CNT_"
			),
	CGT(
			"DTD_?_CGT_?_v?-?-1",
			"cgt/cgt-?/branches/v?.?_?_v?/doc/?",
			"\\cgt\\cgt-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"CGT",
			"cgt",
			"CGT_"
			),
	DAO(
			"DTD_?_DAO_?_v?-?-1",
			"dao/dao-?/branches/v?.?_?_v?/doc/?",
			"\\dao\\dao-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"",
			"",
			"DAO_"
			),
	RES(
			"DTD_?_RES_?_v?-?-1",
			"res-nuc/res-nuc-?/branches/v?.?_?_v?/doc/?",
			"\\res-nuc\\res-nuc-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"RES",
			"res-nuc-jee",
			"RES_"
			),
	JTNU(
			"DTD_?_JOB-JEE_?_v?-?-1",
			"jt-nuc/jt-nuc-?/branches/v?.?_?_v?/doc/?",
			"\\jt-nuc\\jt-nuc-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"JTNU",
			"jt-nuc-jee",
			"JT_"
			),
	RT(
			"DTD_?_RT_?_v?-?-1",
			"rt/rt-?/branches/v?.?_?_v?/doc/?",
			"\\rt\\rt-?",
			"https://svn30.es.telefonica:18080/svn/?/0_AT/?/?-?/tags/?.0-?-1 ./?-?",
			"https://svn30.es.telefonica:18080/svn/?/1_CO/?/?-?/branches/v?.0_?_v1 ./?-?",
			"",
			"",
			"RT_"
			);
	
	private final String nombreDTD;
	private final String guidDTD;
	private final String inicioRuta2DF;
	private final String ruta0AT;
	private final String ruta1CO;
	private final String tipoActivoExcel;
	private final String tipoActivoCodi;
	private final String tipoActivoUnva;
	
	ServicioEnum(String nombreDTD, String guidDTD, String inicioRuta2DF, String ruta0AT, String ruta1CO, String tipoActivoExcel, String tipoActivoCodi, String  tipoActivoUnva){
		this.nombreDTD = nombreDTD;
		this.guidDTD = guidDTD;
		this.inicioRuta2DF = inicioRuta2DF;
		this.ruta0AT = ruta0AT;
		this.ruta1CO = ruta1CO;
		this.tipoActivoExcel = tipoActivoExcel;
		this.tipoActivoCodi = tipoActivoCodi;
		this.tipoActivoUnva = tipoActivoUnva;
	}

	public String getNombreDTD() {
		return nombreDTD;
	}
	public String getGuidDTD() {
		return guidDTD;
	}
	public String getInicioRuta2DF() {
		return inicioRuta2DF;
	}
	public String getRuta0AT() {
		return ruta0AT;
	}

	public String getRuta1CO() {
		return ruta1CO;
	}
	public String getTipoActivoExcel() {
		return tipoActivoExcel;
	}
	public String getTipoActivoCodi() {
		return tipoActivoCodi;
	}
	public String getTipoActivoUnva() {
		return tipoActivoUnva;
	}
}