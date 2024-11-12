package com.telefonica.modulos.dependencias.service;

import com.telefonica.modulos.dependencias.pantalla.PanelConsola;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SRNUService extends ActivoComunService {

	public void buscar(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		File rutaAnterior = dir.getParentFile();
		boolean encuentraCommand = Arrays.stream(rutaAnterior.list()).anyMatch(p -> p.endsWith("Command.java"));
		if(encuentraCommand) {
			buscarOcurrenia(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
		}
	}

	protected void buscarSiguiente(String nombreActivo, String arbolDependencias, List<String> ocurrenciasCausal, File dir) throws Exception {
		String rutaNuc = dir.getAbsolutePath().substring(0, dir.getAbsolutePath().indexOf("\\srv-nuc-jee-"));
		File dirNuc = new File(rutaNuc);
		for (File file : dirNuc.listFiles()) {
			fileUtil.fetchFiles(file, nombreActivo, arbolDependencias, ocurrenciasCausal);
		}
	}

	private String buscarLinea(File dir, String line, String activoBuscar, BufferedReader br, AtomicBoolean terminarBucle) throws IOException {
		String ocurrencia = buscarLinea(dir.getAbsolutePath(), line, activoBuscar, br);
		if (ocurrencia != null) {
			if (!ocurrencia.contains(".repository.")) {
				ocurrencia = getOcurrencia(ocurrencia);
			}
			String servicioOperacion = getNombreActivo(dir);
			if (!FileUtil.activosBuscados.contains(servicioOperacion)) {
				if (!ocurrencia.equals(servicioOperacion)) {
					terminarBucle.set(true);
				}else{
					ocurrencia = null;
				}
			} else {
				ocurrencia = null;
				terminarBucle.set(true);
			}
		}
		return ocurrencia;
	}
	
	private Set<String> getListaBuscar2(String aplicacion) {
		return getListaDaosApp().get(aplicacion);
	}

	@Override
	protected String getTipoActivo1() {
		return null;
	}

	@Override
	protected String getTipoActivo2() {
		return null;
	}

	@Override
	protected String getTipoActivo3() {
		return null;
	}

	@Override
	protected String getNombreActivo(File dir) {
		String servicio1 = dir.getParentFile().getParentFile().getParentFile().getName();
		String operacionDondeLoEncuentra = dir.getParentFile().getName();
        return servicio1 + "." + operacionDondeLoEncuentra;
	}
	
	@Override
	protected String getNombreActivo2(String ruta) {
		String servicio = ruta.substring(ruta.indexOf("srv-nuc-jee-"));
		servicio = servicio.substring(0, servicio.indexOf("\\")).replace("srv-nuc-jee-", "SRV_");
		return servicio;
	}

	protected String buscarLinea(String dir, String line, String activoBuscar, BufferedReader br) throws IOException {
		String ocurrencia = null;
		if(activoBuscar != null) {
			if(line.trim().toUpperCase().contains(activoBuscar.toUpperCase())) {
				ocurrencia = activoBuscar;
			}
		}else {
			if (getListaBuscar1().stream().anyMatch(line.trim().toUpperCase()::contains)){
				ocurrencia = line.trim().replace("import ", "");
				ocurrencia = ocurrencia.substring(0, ocurrencia.lastIndexOf(".")+1);
			}else {
				// buscamos en DAOs
				String aplicacion = fileUtil.obtenerAplicacion(dir);
				Set<String> daos = getListaBuscar2(aplicacion);
				if(daos != null && !daos.isEmpty()) {
					for(String dao: daos) {
						String[] split = dao.split("\\.");
						String nombreDao = split[0];
						String repository = null;
						String query = null;
						if(split.length > 1) {
							repository = split[1];
						}
						if(split.length > 2) {
							query = split[2];
						}
						String lineaDao = "." + nombreDao + ".repository." + (repository!=null?repository:"");
						if(line.trim().toUpperCase().contains(lineaDao.toUpperCase())) {
							if(query != null) {
								// seguimos buscando en la clase, esta vez buscamos la query
								line = br.readLine();
								while(line != null) {
									if(line.contains("." + query + "(")) {
										ocurrencia = lineaDao + "." +query;
										break;
									}
									line = br.readLine();
								}
							}else {
								ocurrencia = lineaDao;
							}
							if(ocurrencia != null) {
								break;
							}
						}
					}
				}else{
					if(getListaBuscar3().stream().anyMatch(line.trim().toUpperCase()::contains)){
						ocurrencia = line.trim().replace("import ", "");
						ocurrencia = ocurrencia.substring(0, ocurrencia.lastIndexOf(".")+1);
					}
				}
			}
		}
		return ocurrencia;
	}
	
	@Override
	protected String getOcurrencia(String ocurrencia) {
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

	@Override
	protected void procesar(String line, String activoBuscar, File dir, List<String> ocurrenciasCausal, String arbolDependencias, AtomicBoolean terminarBucle, BufferedReader br) throws Exception {
		String ocurrencia = buscarLinea(dir, line, activoBuscar, br, terminarBucle);
		if(ocurrencia != null){
			String nombreActivo = getNombreActivo(dir);
			aniadirActivoEncontrado(nombreActivo);

			String pintar = arbolDependencias + nombreActivo + " (OPNJ)";
			ocurrenciasCausal.add(pintar);
			PanelConsola.addText(pintar);
			System.out.println(pintar);
			arbolDependencias += "      ";

			nombreActivo = getNombreActivo2(dir.getAbsolutePath());
			String aplicacion = fileUtil.obtenerAplicacion(dir.getAbsolutePath());
			FileUtil.listaActivos.add(aplicacion + " -> " + "SRNU" + " -> " + nombreActivo);

			String operacionDondeLoEncuentra = dir.getParentFile().getName();
			operacionDondeLoEncuentra = "com.telefonica." + aplicacion.toLowerCase() + ".srv.nuc." + dir.getParentFile().getParentFile().getParentFile().getName() + ".msg." + operacionDondeLoEncuentra + ".";

			buscarSiguiente(operacionDondeLoEncuentra, arbolDependencias, ocurrenciasCausal, dir);
			super.incluirArbolDependencias(activoBuscar, ocurrencia, ocurrenciasCausal);
			terminarBucle.set(true);
		}
	}
}