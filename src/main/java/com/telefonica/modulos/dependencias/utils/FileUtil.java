package com.telefonica.modulos.dependencias.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.telefonica.modulos.dependencias.service.CGTNodeService;
import com.telefonica.modulos.dependencias.service.CNTNodeService;
import com.telefonica.modulos.dependencias.service.CNTService;
import com.telefonica.modulos.dependencias.service.JTService;
import com.telefonica.modulos.dependencias.service.RESService;
import com.telefonica.modulos.dependencias.service.SRNSService;
import com.telefonica.modulos.dependencias.service.SRNUService;
import com.telefonica.modulos.dependencias.service.SRPRService;

@Component
public class FileUtil {
	@Autowired
	private CGTNodeService cGTNodeService;
	@Autowired
	private CNTNodeService cNTNodeService;
	@Autowired
	private CNTService cNTService;
	@Autowired
	private JTService jTService;
	@Autowired
	private RESService rESService;
	@Autowired
	private SRNSService sRNSService;
	@Autowired
	private SRNUService sRNUService;
	@Autowired
	private SRPRService sRPRService;

	private Map<String, Set<String>> listaDaosApp = new HashMap<>();
	private Set<String> listaOperaciones = new HashSet<>();
	private Set<String> listaSRPR = new HashSet<>();
	private Set<String> listaCGT = new HashSet<>();
	private Set<String> listaRES = new HashSet<>();
	private Set<String> listaSRNU_NS_EX = new HashSet<>();
	private Set<String> listaParaComposites = new HashSet<>();
	private Set<String> listaServiciosMajor = new HashSet<>();

	public static Set<String> listaActivos = new HashSet<>();
	public static Set<String> activosBuscados = new LinkedHashSet<>();
	public static Map<String, List<String>> mapaCausalArbol = new HashMap<>();

	private String vcAnalisis;
	private String rutaINFA;
	
	private static boolean encontrado2 = false;
	private static List<String> rutasDondeBuscar = null;
	
	public void inicializarListaRutas(String rutaINFA) {
		rutasDondeBuscar = Arrays.asList(
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\cgt-node\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\cnt\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\cnt-node\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\jt-nuc-jee\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\res-nuc-jee\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\srv-nuc-jee\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\srv-nuc-soa\\",
				rutaINFA.substring(rutaINFA.lastIndexOf("\\")) + "\\srv-pres\\"
				);
    }
	
	public void fetchFiles(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		if (rutasDondeBuscar.stream().anyMatch(dir.getAbsolutePath()::contains)){
			if(dir.isDirectory() && dir.getName().equalsIgnoreCase("BRANCHES")) {
				try {
					fetchLastVersion(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}else if(dir.isDirectory() && dir.getName().equalsIgnoreCase("TRUNK")) {
				if(!encontrado2) {
					Arrays.stream(dir.listFiles()).forEach(file -> {
						try {
							fetchFiles(file, activoBuscar, arbolDependencias, ocurrenciasCausal);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					});
				}
				encontrado2 = false; 
			}else if(dir.isDirectory()) {
				Arrays.stream(dir.listFiles()).forEach(file -> {
					try {
						fetchFiles(file, activoBuscar, arbolDependencias, ocurrenciasCausal);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				});
			}else {
				redirigirTipoActivo(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}
	}
	
	private void redirigirTipoActivo(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		if(dir.getAbsolutePath().contains(TipoActivoEnum.CGT_NODE.getRuta1CO())) {
			if(TipoActivoEnum.CGT_NODE.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				cGTNodeService.setListaBuscar1(listaRES);
				cGTNodeService.setRutaINFA(rutaINFA);
				cGTNodeService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.CNT.getRuta1CO())) {
			if(TipoActivoEnum.CNT.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				cNTService.setListaBuscar1(listaSRPR);
				cNTService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.CNT_NODE.getRuta1CO())) {
			if(TipoActivoEnum.CNT_NODE.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				cNTNodeService.setListaBuscar1(listaCGT);
				cNTNodeService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.JT.getRuta1CO())) {
			if(TipoActivoEnum.JT.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				jTService.setListaBuscar1(listaOperaciones);
				jTService.buscar(dir, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.RES.getRuta1CO())) {
			if(TipoActivoEnum.RES.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				rESService.setListaBuscar1(listaOperaciones);
				rESService.buscar(dir, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_NUC.getRuta1CO())) {
			sRNUService.setListaBuscar1(listaOperaciones);
			sRNUService.setListaDaosApp(listaDaosApp);
			sRNUService.setListaBuscar3(listaServiciosMajor);
			sRNUService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_SOA.getRuta1CO())) {
			if(TipoActivoEnum.SRV_SOA.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				sRNSService.setListaBuscar1(listaParaComposites);
				sRNSService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_PRES.getRuta1CO())) {
			if(TipoActivoEnum.SRV_PRES.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
				sRPRService.setRutaINFA(rutaINFA);
				sRPRService.setListaBuscar1(listaSRNU_NS_EX);
				sRPRService.buscar(dir, activoBuscar, arbolDependencias, ocurrenciasCausal);
			}
		}
	}
	
	private void fetchLastVersion(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) throws Exception {
		File opFile = null;
		if(dir.listFiles().length > 0) {
			vcAnalisis = vcAnalisis.substring(vcAnalisis.indexOf("_")+1);
			int vcAnalisisInt = Integer.parseInt(vcAnalisis);
			
			File[] branches = dir.listFiles();
			int j=0;
			int versionFinal = 0;
			while(j<branches.length) {
				if(branches[j].getName().contains("_PESP_")) {
					String version = branches[j].getName().substring(branches[j].getName().indexOf("PESP_"), branches[j].getName().lastIndexOf("_"));
					version = version.substring(version.indexOf("_")+1);
					int versionInt = Integer.parseInt(version);
					if(versionInt <= vcAnalisisInt && versionInt > versionFinal) {
						opFile = branches[j];
						versionFinal = versionInt;
					}
				}
				j++;
			}
			if(opFile != null) {
		        fetchFiles(opFile, activoBuscar, arbolDependencias, ocurrenciasCausal);
		        encontrado2 = true;	
			}else {
                encontrado2 = branches.length == 1 && branches[0].getName().contains("MIGRADO");
		    }
    	}
	}
	
	public File fetchLastVersion2(File dir, String pesp) {
		if(pesp.isEmpty()) {
			return null;
		}else {
			File opFileBranch = null;
			File opFileTrunk = null;
			
			String vcAnalisis = pesp.substring(pesp.indexOf("_")+1);
			int vcAnalisisInt = Integer.parseInt(vcAnalisis);
			for(File file: dir.listFiles()) {
				if(file.isDirectory() && file.getName().equalsIgnoreCase("BRANCHES")) {
					if(file.listFiles().length > 0) {
						File[] branches = file.listFiles();
						int j=0;
						int versionFinal = 0;
						while(j<branches.length) {
							if(branches[j].getName().contains("_PESP_")) {
								String version = branches[j].getName().substring(branches[j].getName().indexOf("PESP_"), branches[j].getName().lastIndexOf("_"));
								version = version.substring(version.indexOf("_")+1);
								int versionInt = Integer.parseInt(version);
								if(versionInt <= vcAnalisisInt && versionInt > versionFinal) {
									opFileBranch = branches[j];
									versionFinal = versionInt;
								}
							}
							j++;
						}
						if(opFileBranch != null) {
					        break;
						}
			    	}
				}else{
					opFileTrunk = file;
				}
			}
			return opFileBranch!=null?opFileBranch:opFileTrunk;
		}
	}
	
	public boolean comprobarRutaBuscar(String dir) {
		// como al buscar cogemos la raiz de 1-CO, no queremos meternos en URLs que no nos interesan, como cliews, cliers, etc
		boolean seguir = true;
		if(dir.contains(TipoActivoEnum.CGT_NODE.getRuta1CO()) && listaRES.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.CNT.getRuta1CO()) && listaSRPR.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.CNT_NODE.getRuta1CO()) && listaCGT.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.JT.getRuta1CO()) && listaOperaciones.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.RES.getRuta1CO()) && listaOperaciones.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.SRV_NUC.getRuta1CO()) && listaOperaciones.isEmpty()
				&& listaDaosApp.isEmpty() && listaServiciosMajor.isEmpty()){
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.SRV_SOA.getRuta1CO()) && listaParaComposites.isEmpty()) {
			seguir = false;
		}else if(dir.contains(TipoActivoEnum.SRV_PRES.getRuta1CO()) && listaSRNU_NS_EX.isEmpty()) {
			seguir = false;
		}
		return seguir;
	}
	
	public List<File> findFiles(File baseDir, String pattern, boolean findDirectory) {
		List<File> files = new ArrayList<>();
		findFiles_recursive(baseDir, pattern, files, findDirectory);
		return files;
	}

	private static void findFiles_recursive(File baseDir, String pattern, List<File> files, boolean findDirectory) {
		Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		File[] children = baseDir.listFiles();
        for (File f : children) {
            if (f.isDirectory()) {
                if (findDirectory) {
                    String filename = f.getName();
                    Matcher m = p.matcher(filename);
                    if (m.matches()) {
                        files.add(f);
                    } else {
                        findFiles_recursive(f, pattern, files, true);
                    }
                } else {
                    findFiles_recursive(f, pattern, files, false);
                }
            } else {
                if (!findDirectory) {
                    String filename = f.getName();
                    Matcher m = p.matcher(filename);
                    if (m.matches()) {
                        files.add(f);
                    }
                }
            }
        }
	}
	
	public String seleccionarDirectorio(int fileSelectionMode, String ruta) {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File(ruta));
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileSelectionMode(fileSelectionMode);
	    
	    String directorio = "";
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	directorio = chooser.getSelectedFile().getAbsolutePath();
	    }
	    return directorio;
	}
	
	
	public void guardarFichero(String nombre, Workbook workbook, String directorio) throws IOException {
		
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File(directorio));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Libro de Excel 97-2003 (*.xlsx)", "xlsx");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File(nombre));
	    
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	File dirDestino = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + chooser.getSelectedFile().getName() + ".xlsx");
	        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
	        workbook.write(fileOut);
	        fileOut.close();
	        workbook.close();
	    }
	}
	
	public void buscarNombreMetodos(List<File> opFiles, Set<String> rutaOperaciones, String anotacion) throws FileNotFoundException {
        for (File opFile : opFiles) {
            ParserConfiguration config = new ParserConfiguration();
            config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
            JavaParser parser = new JavaParser(config);
            CompilationUnit cu = parser.parse(opFile).getResult().get();
            List<Node> nodes = cu.getChildNodes();

            for (Node node : nodes) {
                // find class node
                if (node instanceof ClassOrInterfaceDeclaration) {
                    List<Node> children = node.getChildNodes();
                    // find public method declarations
                    for (Node child : children) {
                        if (child instanceof MethodDeclaration md) {
                            if (md.isPublic() && md.getAnnotations().stream().anyMatch(p -> p.getNameAsString().equals(anotacion))) {
                                String fn = md.getNameAsString();
                                rutaOperaciones.add(fn);
                            }
                        }
                    }
                }
            }
        }
	}
	
	public String obtenerAplicacion(String ruta) {
		String aplicacion = "INFA";
		if(ruta.contains("\\PRTE\\")) {
			aplicacion = "PRTE";
		}else if(ruta.contains("\\TERC\\")) {
			aplicacion = "TERC";
		}
		return aplicacion;
	}
	
	public void mostrarMensajeInformativo(String mensaje) {
		JOptionPane.showMessageDialog(null, mensaje);
	}
	public void setListaDaosApp(Map<String, Set<String>> listaDaosApp) {
		this.listaDaosApp = listaDaosApp;
	}
	public void setListaOperaciones(Set<String> listaOperaciones) {
		this.listaOperaciones = listaOperaciones;
	}
	public void setListaSRPR(Set<String> listaSRPR) {
		this.listaSRPR = listaSRPR;
	}
	public void setListaCGT(Set<String> listaCGT) {
		this.listaCGT = listaCGT;
	}
	public void setListaRES(Set<String> listaRES) {
		this.listaRES = listaRES;
	}
	public void setListaSRNU_NS_EX(Set<String> listaSRNU_NS_EX) {
		this.listaSRNU_NS_EX = listaSRNU_NS_EX;
	}
	public void setListaParaComposites(Set<String> listaParaComposites) {
		this.listaParaComposites = listaParaComposites;
	}
	public void setListaServiciosMajor(Set<String> listaServiciosMajor) {
		this.listaServiciosMajor = listaServiciosMajor;
	}
	public void setVcAnalisis(String vcAnalisis) {
		this.vcAnalisis = vcAnalisis;
	}
	public void setRutaINFA(String rutaINFA) {
		this.rutaINFA = rutaINFA;
	}
}
