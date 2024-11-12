package com.telefonica.modulos.dependencias.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class JTService extends ActivoComunService {

	public void buscar(File dir, List<String> ocurrenciasCausal) throws Exception {
		String servicio = getNombreActivo(dir);
		if(!FileUtil.activosBuscados.contains(servicio)) {
			buscarOcurrenia(dir, null, "", ocurrenciasCausal);
		}
	}
	
	@Override
	protected String getTipoActivo1() {
		return TipoActivoEnum.JT.getTipoActivo1();
	}

	@Override
	protected String getTipoActivo2() {
		return TipoActivoEnum.JT.getTipoActivo2();
	}

	@Override
	protected String getTipoActivo3() {
		return TipoActivoEnum.JT.getTipoActivo3();
	}

	@Override
	protected String getNombreActivo(File dir) {
		String servicio = dir.getAbsolutePath().substring(dir.getAbsolutePath().indexOf(getTipoActivo1()));
		servicio = servicio.substring(0, servicio.indexOf("\\"));
		return servicio;
	}
	
	@Override
	protected String getNombreActivo2(String nombreActivo) {
		return nombreActivo.replace(getTipoActivo1(), getTipoActivo2());
	}
	
	@Override
	protected String buscarLinea(String line, String activoBuscar) {
		String ocurrencia = null;
		if (getListaBuscar1().stream().anyMatch(line.trim().toUpperCase()::contains)){
			ocurrencia = getOcurrencia(line);
    	}
		return ocurrencia;
	}
	
	@Override
	protected String getOcurrencia(String line) {
		String ocurrencia = line.trim().replace("import ", "");
		ocurrencia = ocurrencia.substring(0, ocurrencia.lastIndexOf(".")+1);
		String primeraParte = ocurrencia.substring(0, ocurrencia.indexOf(".msg."));
		primeraParte = primeraParte.substring(primeraParte.lastIndexOf(".")+1);
		String segundaParte = ocurrencia.substring(ocurrencia.indexOf(".msg.")+5);
		segundaParte = segundaParte.substring(0, segundaParte.indexOf("."));
		
		ocurrencia = primeraParte+"."+segundaParte;
		return ocurrencia;
	}

	@Override
	protected void aniadirActivoEncontrado(String nombreActivo) {
		FileUtil.activosBuscados.add(nombreActivo);
	}
}