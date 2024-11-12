package com.telefonica.modulos.dependencias.service;

import com.telefonica.modulos.dependencias.beans.DependenciaBean;
import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnalisisDependenciasCodiService extends JPanel {
	
	@Autowired
	private FileUtil fileUtil;

	private static List<File> listaRutasbuscar = new ArrayList<>();
	private static Set<String> serviciosOperacionesBuscadas = new HashSet<>();

	
	public void ejecutarAnalisisDependencias(String rutaFicheroDependencias, List<String> listaActivos2,
											 List<DependenciaBean> impactosManuales, String vcAnalisis,
											 String rutaINFA, String rutaPRTE, String rutaTERC) throws Exception {
		
		limpiarListas();
		listaRutasbuscar.add(new File(rutaINFA));
		listaRutasbuscar.add(new File(rutaPRTE));
		listaRutasbuscar.add(new File(rutaTERC));
		fileUtil.inicializarListaRutas(rutaINFA);


		Map<String, Set<String>>  listaDaosApp = new HashMap<>();
		Set<String> listaOperaciones = new HashSet<>(), listaSRPR = new HashSet<>(), listaCGT = new HashSet<>(), listaRES = new HashSet<>(),
		listaSRNU_NS_EX = new HashSet<>(), listaParaComposites = new HashSet<>(), listaServiciosMajor = new HashSet<>();

		rellenarListasConImpactos(rutaFicheroDependencias, impactosManuales, listaDaosApp, listaOperaciones,
				listaSRPR, listaCGT, listaRES, listaSRNU_NS_EX, listaParaComposites, listaServiciosMajor);

		commitListsToFileUtil(vcAnalisis, rutaINFA, listaDaosApp, listaOperaciones,
				listaSRPR, listaCGT, listaRES, listaSRNU_NS_EX, listaParaComposites, listaServiciosMajor);
		
		listaRutasbuscar.forEach(ruta -> Arrays.stream(ruta.listFiles()).forEach(ruta2 -> {
            if(fileUtil.comprobarRutaBuscar(ruta2.getAbsolutePath()+"\\")) {
                Arrays.stream(ruta2.listFiles()).forEach(ruta3 -> {
                    try {
                        fileUtil.fetchFiles(ruta3, null, "", new ArrayList<>());
                    } catch (Exception e) {
						System.out.println(e.getMessage());
                    }
                });
            }
        }));
		for(Map.Entry<String, List<String>> out: FileUtil.mapaCausalArbol.entrySet()){
			for(String out2: out.getValue()){
				System.out.println(out2);
			}
		}

		listaActivos2.addAll(FileUtil.listaActivos);
		Collections.sort(listaActivos2);
	}

	private void commitListsToFileUtil(String vcAnalisis, String rutaINFA,
									   Map<String, Set<String>>  listaDaosApp,
									   Set<String> listaOperaciones, Set<String> listaSRPR, Set<String> listaCGT, Set<String> listaRES,
									   Set<String> listaSRNU_NS_EX, Set<String> listaParaComposites, Set<String> listaServiciosMajor) {
		fileUtil.setListaDaosApp(listaDaosApp);
		fileUtil.setListaOperaciones(listaOperaciones);
		fileUtil.setListaSRPR(listaSRPR);
		fileUtil.setListaCGT(listaCGT);
		fileUtil.setListaRES(listaRES);
		fileUtil.setListaSRNU_NS_EX(listaSRNU_NS_EX);
		fileUtil.setListaParaComposites(listaParaComposites);
		fileUtil.setListaServiciosMajor(listaServiciosMajor);
		fileUtil.setVcAnalisis(vcAnalisis);
		fileUtil.setRutaINFA(rutaINFA);
	}

	private void limpiarListas() {
		FileUtil.mapaCausalArbol = new LinkedHashMap<>();
		serviciosOperacionesBuscadas = new HashSet<>();
		FileUtil.listaActivos = new HashSet<>();
		listaRutasbuscar = new ArrayList<>();
		FileUtil.activosBuscados = new LinkedHashSet<>();
	}
	
	private void rellenarListasConImpactos(String rutaFicheroDependencias, List<DependenciaBean> impactosManuales,
										   Map<String, Set<String>>  listaDaosApp,
										   Set<String> listaOperaciones, Set<String> listaSRPR, Set<String> listaCGT, Set<String> listaRES,
										   Set<String> listaSRNU_NS_EX, Set<String> listaParaComposites, Set<String> listaServiciosMajor) throws IOException {
		for(DependenciaBean bean: impactosManuales) {
			rellenarListas(bean.getTipo(), bean.getProceso(), bean.getAplicacion(), bean.getNombre(), bean.isCambioMajor(),
					listaDaosApp, listaOperaciones, listaSRPR, listaCGT,
					listaRES, listaSRNU_NS_EX, listaParaComposites, listaServiciosMajor);
		}
		if(!rutaFicheroDependencias.isEmpty()) {
			FileInputStream fis = new FileInputStream(rutaFicheroDependencias);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			for(int i=0; i<wb.getNumberOfSheets(); i++) {
				XSSFSheet sheet = wb.getSheetAt(i);
				if(sheet.getSheetName().equals("Análisis dependencias completo")) {
					for(Row row: sheet){
						if(row.getRowNum() >= 2){
							rellenarListas(row,
									listaDaosApp, listaOperaciones, listaSRPR, listaCGT,
									listaRES, listaSRNU_NS_EX, listaParaComposites, listaServiciosMajor);
						}
					}
				}
			}
			wb.close();
		}
	}

	private void rellenarListas(Row row,
								Map<String, Set<String>>  listaDaosApp,
								Set<String> listaOperaciones, Set<String> listaSRPR, Set<String> listaCGT, Set<String> listaRES,
								Set<String> listaSRNU_NS_EX, Set<String> listaParaComposites, Set<String> listaServiciosMajor) {
		String tipoActivoCausal = row.getCell(4).getStringCellValue();
		String proceso = row.getCell(1).getStringCellValue();
		String aplicacion = row.getCell(2).getStringCellValue();
		aplicacion = aplicacion.contains("-")?aplicacion.substring(2):aplicacion;
		String activo = row.getCell(5).getStringCellValue();
		boolean cambioMajor = row.getCell(6).getStringCellValue().contains(".0.");
		
		rellenarListas(tipoActivoCausal, proceso, aplicacion, activo, cambioMajor,
				listaDaosApp, listaOperaciones, listaSRPR, listaCGT,
				listaRES, listaSRNU_NS_EX, listaParaComposites, listaServiciosMajor);
	}
	
	private void rellenarListas(String tipoActivoCausal, String proceso, String aplicacion, String activo, boolean cambioMajor,
								Map<String, Set<String>>  listaDaosApp,
								Set<String> listaOperaciones, Set<String> listaSRPR, Set<String> listaCGT, Set<String> listaRES,
								Set<String> listaSRNU_NS_EX, Set<String> listaParaComposites, Set<String> listaServiciosMajor) {
		if(tipoActivoCausal.equals("OPNJ") || tipoActivoCausal.equals("OPNS") || tipoActivoCausal.equals("OPEX") || tipoActivoCausal.equals("OPPR")) {
			String servicio = activo.substring(4, activo.indexOf("."));
			String operacion = activo.substring(activo.indexOf("OP_")+3);
			
			if(tipoActivoCausal.equals("OPPR")) {
				listaSRPR.add(">SRV-PRES-" + servicio.toUpperCase() + "<");
			}else {
				listaOperaciones.add(("com.telefonica." + proceso + ".srv.exp." + servicio + ".msg." + operacion + ".").toUpperCase());
				listaOperaciones.add(("com.telefonica." + aplicacion + ".srv.exp." + servicio + ".msg." + operacion + ".").toUpperCase());
				
				if(tipoActivoCausal.equals("OPNJ") || tipoActivoCausal.equals("OPNS")) {
					listaOperaciones.add(("com.telefonica." + proceso + ".srv.nuc." + servicio + ".msg." + operacion + ".").toUpperCase());
					listaOperaciones.add(("com.telefonica." + aplicacion + ".srv.nuc." + servicio + ".msg." + operacion + ".").toUpperCase());
				}
				if(cambioMajor) {
					listaParaComposites.add(("http://telefonica.com/" + aplicacion + "/srv-exp-" + servicio + "-v").toUpperCase());
					listaParaComposites.add(("http://telefonica.com/" + proceso + "/srv-exp-" + servicio + "-v").toUpperCase());
					listaParaComposites.add(("http://telefonica.com/" + aplicacion + "/srv-nuc-" + servicio + "-v").toUpperCase());
					listaParaComposites.add(("http://telefonica.com/" + proceso + "/srv-nuc-" + servicio + "-v").toUpperCase());
					
					buscarOperacionesServicio(servicio, tipoActivoCausal, proceso, aplicacion, listaOperaciones);

					listaSRNU_NS_EX.add(">CLIEWS-" + servicio.toUpperCase() + "<");

					if(!Arrays.asList("INFA", "PRTE", "TERC").contains(aplicacion)){
						listaServiciosMajor.add(("com.telefonica." + proceso + ".srv.exp." + servicio + ".msg.").toUpperCase());
						listaServiciosMajor.add(("com.telefonica." + aplicacion + ".srv.exp." + servicio + ".msg.").toUpperCase());
					}
				}
			}
		}else {
            switch (tipoActivoCausal) {
                case "CGT" -> {
                    listaCGT.add(proceso + "." + activo.toUpperCase().replace("_", "-") + "\"");
                    listaCGT.add(aplicacion + "." + activo.toUpperCase().replace("_", "-") + "\"");
                }
                case "SRNU", "SRNS", "SREX" -> {
                    listaSRNU_NS_EX.add(">CLIEWS-" + activo.toUpperCase().substring(4) + "<");
                    if (cambioMajor) {
                        listaParaComposites.add(("http://telefonica.com/" + aplicacion + "/srv-exp-" + activo.substring(4) + "-v").toUpperCase());
                        listaParaComposites.add(("http://telefonica.com/" + proceso + "/srv-exp-" + activo.substring(4) + "-v").toUpperCase());
                        listaParaComposites.add(("http://telefonica.com/" + aplicacion + "/srv-nuc-" + activo.substring(4) + "-v").toUpperCase());
                        listaParaComposites.add(("http://telefonica.com/" + proceso + "/srv-nuc-" + activo.substring(4) + "-v").toUpperCase());

                        buscarOperacionesServicio(activo.substring(4), tipoActivoCausal, proceso, aplicacion, listaOperaciones);

						if(!Arrays.asList("INFA", "PRTE", "TERC").contains(aplicacion)){
							listaServiciosMajor.add(("com.telefonica." + proceso + ".srv.exp." + activo.substring(4) + ".msg.").toUpperCase());
							listaServiciosMajor.add(("com.telefonica." + aplicacion + ".srv.exp." + activo.substring(4) + ".msg.").toUpperCase());
						}
                    }
                }
                case "RES" -> listaRES.add(activo.toUpperCase().substring(4));
                case "DAO" -> {
                    Set<String> listaDaos = new HashSet<>();
                    if (listaDaosApp.get(aplicacion) != null) {
                        listaDaos = listaDaosApp.get(aplicacion);
                    }
                    listaDaos.add(activo.substring(4));
                    listaDaosApp.put(aplicacion, listaDaos);
                }
            }
		}
	}
	private void buscarOperacionesServicio(String servicio, String tipoActivo, String proceso, String aplicacion, Set<String> listaOperaciones) {
		if(proceso.equals("GTER") && (tipoActivo.equals("SRNU") || tipoActivo.equals("OPNJ")) && !serviciosOperacionesBuscadas.contains(servicio)) {
			String rutaServicios = listaRutasbuscar.stream().filter(p -> p.getAbsolutePath().contains(aplicacion)).findFirst().orElse(null).getAbsolutePath();
			rutaServicios = rutaServicios.substring(0, rutaServicios.lastIndexOf("\\"));
			rutaServicios = rutaServicios + TipoActivoEnum.SRV_NUC.getRuta1CO();
			
			List<File> files = fileUtil.findFiles(new File(rutaServicios), "srv-nuc-jee-" + servicio, true);
			List<File> rutaOperaciones = new ArrayList<>();
			for(File file: files) {
				rutaOperaciones.addAll(fileUtil.findFiles(file, ".*Command\\.java", false));
			}
			Set<String> operaciones = rutaOperaciones.stream().map(p -> p.getName().substring(0, p.getName().indexOf("Command.java"))).collect(Collectors.toSet());
			for(String operacion: operaciones) {
				listaOperaciones.add(("com.telefonica." + proceso + ".srv.exp." + servicio + ".msg." + operacion + ".").toUpperCase());
				listaOperaciones.add(("com.telefonica." + aplicacion + ".srv.exp." + servicio + ".msg." + operacion + ".").toUpperCase());
				listaOperaciones.add(("com.telefonica." + proceso + ".srv.nuc." + servicio + ".msg." + operacion + ".").toUpperCase());
				listaOperaciones.add(("com.telefonica." + aplicacion + ".srv.nuc." + servicio + ".msg." + operacion + ".").toUpperCase());
			}
			serviciosOperacionesBuscadas.add(servicio);
		}
	}
}