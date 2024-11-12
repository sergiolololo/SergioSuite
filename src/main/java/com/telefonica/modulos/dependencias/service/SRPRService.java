package com.telefonica.modulos.dependencias.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class SRPRService extends ActivoComunService {
	private String rutaINFA;

	public void buscar(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		String nombreActivo = getNombreActivo(dir);
		if(!FileUtil.activosBuscados.contains(nombreActivo + "(" + getTipoActivo3() + ")")) {
			buscarOcurrenia(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
		}
	}
	
	@Override
	protected String getTipoActivo1() {
		return TipoActivoEnum.SRV_PRES.getTipoActivo1();
	}

	@Override
	protected String getTipoActivo2() {
		return TipoActivoEnum.SRV_PRES.getTipoActivo2();
	}

	@Override
	protected String getTipoActivo3() {
		return TipoActivoEnum.SRV_PRES.getTipoActivo3();
	}

	@Override
	protected void buscarSiguiente(String nombreActivo, String arbolDependencias, List<String> ocurrenciasCausal, File dir) throws Exception {
		// buscamos el PRES en los CNT java
		File rutaCntJava = new File(rutaINFA + "\\cnt\\");
		String presBuscar = ">SRV-PRES-" + nombreActivo.substring(4).toUpperCase() + "<";
		for(File ruta: rutaCntJava.listFiles()) {
			super.fileUtil.fetchFiles(ruta, presBuscar, arbolDependencias, ocurrenciasCausal);
		}
	}

	@Override
	protected String getNombreActivo(File dir) {
		String aux = dir.getAbsolutePath().substring(dir.getAbsolutePath().indexOf(getTipoActivo1()));
        return aux.substring(0, aux.indexOf("\\")).replace(getTipoActivo1(), getTipoActivo2());
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

	public void setRutaINFA(String rutaINFA) {
		this.rutaINFA = rutaINFA;
	}
}