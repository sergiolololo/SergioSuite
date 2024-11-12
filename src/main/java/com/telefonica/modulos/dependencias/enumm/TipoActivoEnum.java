package com.telefonica.modulos.dependencias.enumm;

import java.util.List;

public enum TipoActivoEnum{

	CGT_NODE(
			"\\1_CO\\cgt-node\\",
            List.of("_model.js"),
			"cgt-node",
			"CGT_",
			"CGT",
			"CGT_?"
			),
	CNT(
			"\\1_CO\\cnt\\",
            List.of("pom.xml"),
			"cnt-",
			"CNT_",
			"CNT",
			""
			),
	CNT_NODE(
			"\\1_CO\\cnt-node\\",
            List.of("package.json"),
			"cnt-node",
			"CNT_",
			"CNT",
			""
			),
	DAO(
			"\\1_CO\\dao\\",
			List.of(".java"),
			"dao-",
			"DAO_",
			"DAO",
			"DAO_?"
			),
	JT(
			"\\1_CO\\jt-nuc-jee\\",
            List.of(".java"),
			"jt-nuc-jee-",
			"JT_",
			"JTNU",
			""
			),
	RES(
			"\\1_CO\\res-nuc-jee\\",
			List.of("Mapper.java", "Command.java"),
			"res-nuc-jee-",
			"RES_",
			"RES",
			"RES_?"
			),
	SRV_NUC(
			"\\1_CO\\srv-nuc-jee\\",
			List.of("Mapper.java", "Command.java", "Impl.java", "Operation.java"),
			"srv-nuc-jee-",
			"SRV_",
			"SRNU",
			"SRV_?"
			),
	SRV_SOA(
			"\\1_CO\\srv-nuc-soa\\",
			List.of("composite.xml"),
			"srv-nuc-soa-",
			"SRV_",
			"SRNS",
			"SRV_?"
			),
	SRV_PRES(
			"\\1_CO\\srv-pres\\",
			List.of("pom.xml"),
			"srv-pres-",
			"SRV_",
			"SRPR",
			"SRV_?"
			),
	SRV_EXP(
			"\\1_CO\\srv-exp-osb\\",
			null,
			"srv-exp-osb-",
			"SRV_",
			"SREX",
			"SRV_?"
			),
	OPEX(
			"",
			null,
			"",
			"",
			"",
			"SRV_?.SREX.OSB.OP_?"
			),
	OPNJ(
			"",
			null,
			"",
			"",
			"",
			"SRV_?.SRNU.JEE.OP_?"
			),
	OPNS(
			"",
			null,
			"",
			"",
			"",
			"SRV_?.SRNS.BPEL.OP_?"
			),
	OPPR(
			"",
			null,
			"",
			"",
			"",
			"SRV_?.SRPR.JEE.OP_?"
			);
	
	private final String ruta1CO;
	private final List<String> archivoDondeBuscar;
	private final String tipoActivo1;
	private final String tipoActivo2;
	private final String tipoActivo3;
	private final String plantillaNombre;
	
	TipoActivoEnum(String ruta1CO, List<String> archivoDondeBuscar, String tipoActivo1, String tipoActivo2, String tipoActivo3, String plantillaNombre){
		this.ruta1CO = ruta1CO;
		this.archivoDondeBuscar = archivoDondeBuscar;
		this.tipoActivo1 = tipoActivo1;
		this.tipoActivo2 = tipoActivo2;
		this.tipoActivo3 = tipoActivo3;
		this.plantillaNombre = plantillaNombre;
	}

	
	public String getRuta1CO() {
		return ruta1CO;
	}
	public List<String> getArchivoDondeBuscar() {
		return archivoDondeBuscar;
	}
	public String getTipoActivo1() {
		return tipoActivo1;
	}
	public String getTipoActivo2() {
		return tipoActivo2;
	}
	public String getTipoActivo3() {
		return tipoActivo3;
	}
	public String getPlantillaNombre() {
		return plantillaNombre;
	}
}