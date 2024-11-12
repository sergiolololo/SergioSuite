package com.telefonica.modulos.dependencias.service;

import java.io.File;
import java.util.List;

import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class CNTNodeService extends ActivoComunService {

	public void buscar(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		String nombreActivo = getNombreActivo(dir);
		if(!FileUtil.activosBuscados.contains(nombreActivo + "(" + getTipoActivo3() + ")")) {
			buscarOcurrenia(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
		}
	}
	
	protected String getTipoActivo1() {
		return TipoActivoEnum.CNT_NODE.getTipoActivo1();
	}

	@Override
	protected String getTipoActivo2() {
		return TipoActivoEnum.CNT_NODE.getTipoActivo2();
	}

	@Override
	protected String getTipoActivo3() {
		return TipoActivoEnum.CNT_NODE.getTipoActivo3();
	}

	@Override
	protected String getNombreActivo(File dir) {
		String aux = dir.getAbsolutePath().substring(dir.getAbsolutePath().indexOf(getTipoActivo1()));
		aux = aux.substring(aux.indexOf("\\")+1);
		String nombreActivo = aux.substring(0, aux.indexOf("\\"));
		nombreActivo = nombreActivo.replace("cnt-", getTipoActivo2());
		return nombreActivo;
	}
	
	@Override
	protected String buscarLinea(String line, String activoBuscar) {
		String ocurrencia = null;
		if(activoBuscar != null) {
			if(line.trim().toUpperCase().contains(activoBuscar.toUpperCase())) {
				ocurrencia = line.trim();
			}
		}else {
			if(getListaBuscar1().stream().anyMatch(line.trim().toUpperCase()::contains)) {
				ocurrencia = line.trim();
			}
		}
		return ocurrencia;
	}

	@Override
	protected String getOcurrencia(String line) {
		return line.trim();
	}
	
	@Override
	protected void aniadirActivoEncontrado(String nombreActivo) {
		FileUtil.activosBuscados.add(nombreActivo + "(" + getTipoActivo3() + ")");
	}
}