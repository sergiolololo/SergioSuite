package com.telefonica.modulos.cargaunva.pantalla;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.modulos.cargaunva.beans.*;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import com.telefonica.nomodulos.utilidades.Constantes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Component
public class PanelCargaUNVA extends JPanel {
	@Autowired
	private FileUtil fileUtil;
    private final JTextField txtPesp1, txtPesp2, txtDrsInfa, txtDrsPrte, txtDrsTerc, txtRutaJson, txtRutaInfoOPs,
			txtRuta2DFInfa, txtRuta2DFPrte, txtRuta2DFTerc, txtFicheroAnalisis;
	private final JLabel lblDrsPrte, lblDrsTerc, lblRutaInfoOPs;
    private final JCheckBox chkExcelAsignarDRS;
	private final JRadioButton rdbEtapa21, rdbEtapa22;
	@Value("${ruta.2DF}")
	private String RUTA_2DF;
	@Value("${ruta.ficheros.aris}")
	private String RUTA_FICHEROS_ARIS;
	@Value("${ruta.carpeta.ficheros.analisis}")
	private String RUTA_CARPETA_ANALISIS_DEPENDENCIAS;
	
	/**
	 * Create the panel.
     */
	public PanelCargaUNVA() {
		//setBounds(0, 0, 1106, 641);
		setBackground(SystemColor.inactiveCaption);
		setLayout(null);

        JPanel panelArriba = new JPanel();
		panelArriba.setBorder(new TitledBorder(new LineBorder(new Color(192, 192, 192), 2, true), "Carga en UNVA", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		panelArriba.setBounds(0, 0, 1214, 658);
		panelArriba.setVisible(true);
		panelArriba.setLayout(null);
		add(panelArriba);
        
        txtPesp1 = new JTextField();
        txtPesp1.setBounds(59, 231, 137, 20);
        panelArriba.add(txtPesp1);
        txtPesp1.setColumns(10);

        JLabel lblPesp1 = new JLabel("* Versión comercial (ej: PESP_2207_731326)");
        lblPesp1.setBounds(60, 206, 258, 14);
        panelArriba.add(lblPesp1);

        JButton btnCrearEstructura = new JButton("EMPEZAR");
        btnCrearEstructura.addActionListener(e -> {

            int opcion = mostrarMensaje();
            if(opcion == 0) {
                try {
                    ejecutarProceso();
                } catch (IOException e1) {
                    System.out.println("Error al ejecutar el proceso");
                }
            }
        });
        btnCrearEstructura.setBounds(463, 463, 158, 33);
        panelArriba.add(btnCrearEstructura);
        
        JLabel lblPesp2 = new JLabel("* Código aplicación (ej: PPRO_GTER_v0.0.180.1)");
        lblPesp2.setBounds(349, 206, 358, 14);
        panelArriba.add(lblPesp2);
        
        txtPesp2 = new JTextField();
        txtPesp2.setColumns(10);
        txtPesp2.setBounds(349, 231, 137, 20);
        panelArriba.add(txtPesp2);
        
        JLabel lblDrsInfa = new JLabel("DRS INFA (ej: 735191)");
        lblDrsInfa.setBounds(59, 264, 200, 14);
        panelArriba.add(lblDrsInfa);
        
        txtDrsInfa = new JTextField();
        txtDrsInfa.setColumns(10);
        txtDrsInfa.setBounds(59, 291, 137, 20);
        panelArriba.add(txtDrsInfa);
        
        lblDrsPrte = new JLabel("DRS PRTE (ej: 735191)");
        lblDrsPrte.setBounds(59, 324, 200, 14);
        panelArriba.add(lblDrsPrte);
        
        txtDrsPrte = new JTextField();
        txtDrsPrte.setColumns(10);
        txtDrsPrte.setBounds(59, 351, 137, 20);
        panelArriba.add(txtDrsPrte);
        
        txtDrsTerc = new JTextField();
        txtDrsTerc.setColumns(10);
        txtDrsTerc.setBounds(59, 411, 137, 20);
        panelArriba.add(txtDrsTerc);
        
        lblDrsTerc = new JLabel("DRS TERC (ej: 735191)");
        lblDrsTerc.setBounds(60, 384, 199, 14);
        panelArriba.add(lblDrsTerc);

        JLabel lblRutaJson = new JLabel("* Ruta archivos json");
        lblRutaJson.setBounds(349, 264, 136, 14);
        panelArriba.add(lblRutaJson);
        
        lblRutaInfoOPs = new JLabel("Ruta archivo información de objetos");
        lblRutaInfoOPs.setBounds(349, 323, 291, 14);
        panelArriba.add(lblRutaInfoOPs);

        JLabel lblRuta2DFInfa = new JLabel("* Ruta directorio 2-DF local INFA");
        lblRuta2DFInfa.setBounds(349, 383, 199, 14);
        panelArriba.add(lblRuta2DFInfa);
        
        txtRutaJson = new JTextField();
        txtRutaJson.setEnabled(false);
        txtRutaJson.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				String pesp = txtPesp1.getText().substring(0, StringUtils.ordinalIndexOf(txtPesp1.getText(), "_", 2));
				txtRutaJson.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, RUTA_FICHEROS_ARIS + pesp));
        	}
        });
        txtRutaJson.setColumns(10);
        txtRutaJson.setBounds(348, 290, 137, 20);
        panelArriba.add(txtRutaJson);
        
        txtRutaInfoOPs = new JTextField();
        txtRutaInfoOPs.setEnabled(false);
        txtRutaInfoOPs.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				String pesp = txtPesp1.getText().substring(0, StringUtils.ordinalIndexOf(txtPesp1.getText(), "_", 2));
				txtRutaInfoOPs.setText(fileUtil.seleccionarDirectorio(JFileChooser.FILES_ONLY, RUTA_FICHEROS_ARIS + pesp));
        	}
        });
        txtRutaInfoOPs.setColumns(10);
        txtRutaInfoOPs.setBounds(349, 350, 137, 20);
        panelArriba.add(txtRutaInfoOPs);
        
        txtRuta2DFInfa = new JTextField();
        txtRuta2DFInfa.setEnabled(false);
        txtRuta2DFInfa.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		String directorio = fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, RUTA_2DF);
        		txtRuta2DFInfa.setText(directorio);
        		if(directorio.contains("INFA")) {
        			directorio = directorio.replace("INFA", "?");
        			txtRuta2DFPrte.setText(directorio.replace("?", "PRTE"));
        			txtRuta2DFTerc.setText(directorio.replace("?", "TERC"));
        		}
        	}
        });
        txtRuta2DFInfa.setColumns(10);
        txtRuta2DFInfa.setBounds(349, 410, 137, 20);
        panelArriba.add(txtRuta2DFInfa);

        JLabel lblNotas = new JLabel("NOTAS:");
        lblNotas.setBounds(24, 515, 98, 14);
        panelArriba.add(lblNotas);

        JLabel lblRutaArchivoInformacin_1 = new JLabel("Ruta archivo información de objetos: es un excel que se obtiene de ARIS. Pulsamos sobre la carpeta y lanzamos informe \"Descarga Atributos \tServicios Tecnicos\"");
        lblRutaArchivoInformacin_1.setBounds(24, 569, 969, 14);
        panelArriba.add(lblRutaArchivoInformacin_1);

        JLabel lblRutaArchivoInformacin_2 = new JLabel("Ruta archivos json: ruta donde tendremos recopilados los json que hemos generado de los activos que queremos tener en cuenta");
        lblRutaArchivoInformacin_2.setBounds(24, 542, 969, 14);
        panelArriba.add(lblRutaArchivoInformacin_2);
        
        txtRuta2DFPrte = new JTextField();
        txtRuta2DFPrte.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		String rutaDFPRTE = RUTA_2DF.replace("INFA", "PRTE");
				txtRuta2DFPrte.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, rutaDFPRTE));
        	}
        });
        txtRuta2DFPrte.setEnabled(false);
        txtRuta2DFPrte.setColumns(10);
        txtRuta2DFPrte.setBounds(628, 411, 137, 20);
        panelArriba.add(txtRuta2DFPrte);

        JLabel lblRuta2DFPrte = new JLabel("* Ruta directorio 2-DF local PRTE");
        lblRuta2DFPrte.setBounds(628, 384, 199, 14);
        panelArriba.add(lblRuta2DFPrte);
        
        txtRuta2DFTerc = new JTextField();
        txtRuta2DFTerc.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				String rutaTERC = RUTA_2DF.replace("INFA", "TERC");
        		txtRuta2DFTerc.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, rutaTERC));
        	}
        });
        txtRuta2DFTerc.setEnabled(false);
        txtRuta2DFTerc.setColumns(10);
        txtRuta2DFTerc.setBounds(839, 411, 137, 20);
        panelArriba.add(txtRuta2DFTerc);

        JLabel lblRuta2DFTerc = new JLabel("* Ruta directorio 2-DF local TERC");
        lblRuta2DFTerc.setBounds(839, 384, 219, 14);
        panelArriba.add(lblRuta2DFTerc);

        JLabel lblRutaArchivoInformacin_3 = new JLabel("Ruta directorio 2-DF local INFA: ruta raíz donde se encuentra el directorio 2-DF de INFA. Una vez seleccionado este directorio, el de PRTE y TERC se rellenarán automáticamente");
        lblRutaArchivoInformacin_3.setBounds(24, 596, 1050, 14);
        panelArriba.add(lblRutaArchivoInformacin_3);
        
        JTextArea txtrAsdasd = new JTextArea();
        txtrAsdasd.setEditable(false);
        txtrAsdasd.setBackground(SystemColor.menu);
        txtrAsdasd.setText("Proceso que se encarga de:\r\n- Crear la estructura del repositorio en 2-DF de los activos cuyo json encuentre en el directorio especificado. Crea tanto tag como branch, este último con el nombre del PESP que se ha \r\nindicado en la caja de texto. Estos directorios los crea copiando todo lo del directorio de la versión anterior, es decir, crea las carpetas doc, json y xsd, con la misma información que la \r\nversión anterior, renombrando el DTD con el nombre de la nueva versión, y sustituyendo el json por el nuevo\r\n- Generar el excel de carga de UNVA de Etapa21 de los activos cuyo json encuentre en el directorio especificado\r\n- Generar el excel de asignación de activos a los DRS especificados de los activos cuyo json encuentre en el directorio especificado");
        txtrAsdasd.setBounds(23, 41, 1159, 122);
        panelArriba.add(txtrAsdasd);
        
        chkExcelAsignarDRS = new JCheckBox("Generar excel asignación DRS");
        chkExcelAsignarDRS.setEnabled(false);
        chkExcelAsignarDRS.setBounds(839, 259, 229, 25);
        panelArriba.add(chkExcelAsignarDRS);
        
        rdbEtapa21 = new JRadioButton("Excel carga UNVA Etapa21");
        rdbEtapa21.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chkExcelAsignarDRS.setEnabled(true);
                lblDrsInfa.setText("* " + lblDrsInfa.getText());
                lblDrsPrte.setText("* " + lblDrsPrte.getText());
                lblDrsTerc.setText("* " + lblDrsTerc.getText());
                lblRutaInfoOPs.setText("* " + lblRutaInfoOPs.getText());
				txtDrsInfa.setEnabled(true);
				txtDrsPrte.setEnabled(true);
				txtDrsTerc.setEnabled(true);
            }
        });
        
        rdbEtapa21.setBounds(628, 259, 209, 25);
        panelArriba.add(rdbEtapa21);
        
        rdbEtapa22 = new JRadioButton("Excel carga UNVA Etapa22 (dependencias)");
		rdbEtapa22.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if(lblDrsInfa.getText().contains("*")) {
					lblDrsInfa.setText(lblDrsInfa.getText().substring(2));
					lblDrsPrte.setText(lblDrsPrte.getText().substring(2));
					lblDrsTerc.setText(lblDrsTerc.getText().substring(2));
					lblRutaInfoOPs.setText(lblRutaInfoOPs.getText().substring(2));
				}
				chkExcelAsignarDRS.setEnabled(false);
				txtDrsInfa.setEnabled(false);
				txtDrsPrte.setEnabled(false);
				txtDrsTerc.setEnabled(false);
			}
		});
        rdbEtapa22.setBounds(628, 286, 291, 25);
        panelArriba.add(rdbEtapa22);
        
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(rdbEtapa21);
        bgroup.add(rdbEtapa22);
        
        JLabel lblFicheroAnlisisDe = new JLabel("Fichero análisis de dependencias");
        lblFicheroAnlisisDe.setBounds(760, 206, 358, 14);
        panelArriba.add(lblFicheroAnlisisDe);
        
        txtFicheroAnalisis = new JTextField();
        txtFicheroAnalisis.setEnabled(false);
        txtFicheroAnalisis.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				String pesp = txtPesp1.getText().substring(0, StringUtils.ordinalIndexOf(txtPesp1.getText(), "_", 2));
				txtFicheroAnalisis.setText(fileUtil.seleccionarDirectorio(JFileChooser.FILES_ONLY, RUTA_CARPETA_ANALISIS_DEPENDENCIAS + pesp));
        	}
        });
        
        txtFicheroAnalisis.setColumns(10);
        txtFicheroAnalisis.setBounds(760, 231, 137, 20);
        panelArriba.add(txtFicheroAnalisis);
	}
	
	
	private void ejecutarProceso() throws IOException {
		
		File ruta = new File(txtRutaJson.getText());
		
		XSSFWorkbook workbook_cargaUnva = new XSSFWorkbook();
		XSSFSheet sheet_cargaUnva = workbook_cargaUnva.createSheet();
		
		XSSFWorkbook workbook_asignarDRS = new XSSFWorkbook();
		XSSFSheet sheet_asignarDRS = workbook_asignarDRS.createSheet();
		crearCabeceraExcelASignarDRS(sheet_asignarDRS);
		
		List<String> scriptDirectoryList = new ArrayList<>();
		BufferedWriter writer0AT = guardarFicheroSH("ficheroSH_0AT", scriptDirectoryList);
		BufferedWriter writer1CO = guardarFicheroSH("ficheroSH_1CO", scriptDirectoryList);
		
		ObjectMapper objectMapper = new ObjectMapper();
		for(File archivo: Objects.requireNonNull(ruta.listFiles())){
			if(!archivo.isDirectory()){
				System.out.println(archivo);
				JsonNode node = objectMapper.readTree(archivo);
				String aplicacion = node.get("name").asText().toUpperCase();
				
				String drs = "";
				String ruta2DF = switch (aplicacion) {
                    case "INFA" -> {
                        drs = txtDrsInfa.getText() + " - AINF - GTER";
                        yield txtRuta2DFInfa.getText();
                    }
                    case "PRTE" -> {
                        drs = txtDrsPrte.getText() + " - PRTE - GTER";
                        yield txtRuta2DFPrte.getText();
                    }
                    case "TERC" -> {
                        drs = txtDrsTerc.getText() + " - TERC - GTER";
                        yield txtRuta2DFTerc.getText();
                    }
                    default -> "";
                };

                ServicioUnvaBean servicioBean;
				if(node.get("srv-nuc-v4") != null || node.get("srv-nuc") != null) {
					boolean esV4 = node.get("srv-nuc-v4") != null;
					Iterator<JsonNode> iterator = node.get(esV4?"srv-nuc-v4":"srv-nuc").elements();
					JsonNode nodeInfoSRV = iterator.next();
					String implementacion = nodeInfoSRV.get("implementationTechnology").asText();
					if(implementacion.equals("JEE")) {
						servicioBean = new SRNUUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF, esV4);
					}else {
						servicioBean = new SRNSUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF, esV4);
					}
				}else if(node.get("srv-pres") != null) {
					servicioBean = new SRPRUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
				}else if(node.get("cgt") != null) {
					servicioBean = new CGTUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
                }else if(node.get("cnt") != null) {
					servicioBean = new CNTUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
                }else if(node.get("dao") != null) {
					servicioBean = new DAOUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
				}else if(node.get("res-nuc") != null) {
					servicioBean = new RESUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
				}else if(node.get("jt-nuc") != null) {
					servicioBean = new JTNUUnvaBean(node, aplicacion, txtPesp1.getText(), ruta2DF);
				}else{
					servicioBean = new ServicioUnvaBean();
				}
				
				// creamos fila de carga en UNVA del SRV
				List<String> rutas0AT = new ArrayList<>();
				if(rdbEtapa21.isSelected()) {
					crearFilaExcelCargaUnva(sheet_cargaUnva, servicioBean.getGuid(), servicioBean.getFqn(), servicioBean.getFqn(), servicioBean.getTipoActivo(), servicioBean.getVersion() + "." + servicioBean.getRevision(), servicioBean.getAplicacion(), "DISE_E21");
					if(chkExcelAsignarDRS.isEnabled() && chkExcelAsignarDRS.isSelected()) {
						crearFilaExcelASignarDRS(sheet_asignarDRS, servicioBean.getFqn(), servicioBean.getTipoActivo(), servicioBean.getVersion() + "." + servicioBean.getRevision(), drs);
					}
				}else if(rdbEtapa22.isSelected()) {
					
					crearLineaSH(writer0AT, writer1CO, servicioBean, servicioBean.getTipoActivoCodi());
					rutas0AT.add(servicioBean.getRuta0AT(servicioBean.getTipoActivoCodi()));
					if(servicioBean.isGenerarNode()) {
						crearLineaSH(writer0AT, writer1CO, servicioBean, servicioBean.getTipoActivoCodi() + "-node");
						rutas0AT.add(servicioBean.getRuta0AT(servicioBean.getTipoActivoCodi() + "-node"));
					}
					
					crearFilaExcelCargaUnva(sheet_cargaUnva, "", servicioBean.getFqn(), servicioBean.getFqn(), servicioBean.getTipoActivo(), servicioBean.getVersion() + ".0-" + servicioBean.getRevision() + "-1", servicioBean.getAplicacion(), "DISE_E22");
					if(servicioBean.getTipoActivo().equals("SRNU") || servicioBean.getTipoActivo().equals("SRNS")) {
						String hasExpEndPoints = servicioBean.getNodeInfoSRV().get("hasExpEndPoints")!=null?servicioBean.getNodeInfoSRV().get("hasExpEndPoints").asText():null;
						String tecnologiaInterfaz = servicioBean.getNodeInfoSRV().get("interfaceTechnology")!=null?servicioBean.getNodeInfoSRV().get("interfaceTechnology").asText():null;
						if(hasExpEndPoints != null && hasExpEndPoints.equals("true") && tecnologiaInterfaz != null && tecnologiaInterfaz.contains("SOAP")) {
							crearFilaExcelCargaUnva(sheet_cargaUnva, "", "CLIEWS_" + servicioBean.getNombreActivo(), "CLIEWS_" + servicioBean.getNombreActivo(), "CLIEWS", servicioBean.getVersion() + ".0-" + servicioBean.getRevision() + "-1", servicioBean.getAplicacion(), "DISE_E22");
							crearLineaSH(writer0AT, writer1CO, servicioBean, "cliews");
							rutas0AT.add(servicioBean.getRuta0AT("cliews"));
						}
						if(servicioBean.getArquitectura().equals("4") && tecnologiaInterfaz != null && tecnologiaInterfaz.contains("REST")) {
							crearFilaExcelCargaUnva(sheet_cargaUnva, "", "CLIERS_" + servicioBean.getNombreActivo(), "CLIERS_" + servicioBean.getNombreActivo(), "CLIERS", servicioBean.getVersion() + ".0-" + servicioBean.getRevision() + "-1", servicioBean.getAplicacion(), "DISE_E22");
							crearLineaSH(writer0AT, writer1CO, servicioBean, "cliers");
							rutas0AT.add(servicioBean.getRuta0AT("cliers"));
						}
					}
					aniadiarRegeneracionAFicheroAnalisisFinal(servicioBean, rutas0AT);
				}
				if(rdbEtapa21.isSelected()) {
					crearFilaExcelCargaUnva(sheet_cargaUnva, servicioBean.getGuidDTD(), servicioBean.getNombreDTD(), servicioBean.getNombreDTD(), "DTD", servicioBean.getVersion() + "-" + servicioBean.getRevision() + "-1", aplicacion, "DISE_E21");
					if(chkExcelAsignarDRS.isEnabled() && chkExcelAsignarDRS.isSelected()) {
						crearFilaExcelASignarDRS(sheet_asignarDRS, servicioBean.getNombreDTD(), "DTD", servicioBean.getVersion() + "-" + servicioBean.getRevision() + "-1", drs);
					}
					
					FileInputStream fis = new FileInputStream(txtRutaInfoOPs.getText());
					HSSFWorkbook wb_infoOPs = new HSSFWorkbook(fis);
					HSSFSheet sheet_infoOPs = wb_infoOPs.getSheetAt(0);
					if(servicioBean.getNodeInfoSRV().get("op") != null) {
						Iterator<JsonNode> nodeOPs = servicioBean.getNodeInfoSRV().get("op").elements();
						while(nodeOPs.hasNext()){
							JsonNode nodeOP = nodeOPs.next();
							String nombreOP = nodeOP.get("name").asText();
							String fqnOP = Constantes.FQN_OP;
							fqnOP = StringUtils.replaceOnce(fqnOP, "?", servicioBean.getNombreActivo());
							fqnOP = StringUtils.replaceOnce(fqnOP, "?", nombreOP);
							
							// buscamos el GUID de la OP en el excel de informaci�n descargado de UNVA
							String guidOP = "";
							for(Row row: sheet_infoOPs){
								if(row.getRowNum() >= 1){
									String guid_excel = row.getCell(0).getStringCellValue();
									String nombreOP_excel = row.getCell(1).getStringCellValue();
									String tipoActivo_excel = row.getCell(3).getStringCellValue();
									
									if(tipoActivo_excel.equals("Operación Núcleo") && nombreOP_excel.equals("OP_" + nombreOP)){
										guidOP = guid_excel;
										break;
									}
								}
							}
							
							// creamos fila de carga en UNVA de la OP
							if(rdbEtapa21.isSelected()) {
								crearFilaExcelCargaUnva(sheet_cargaUnva, guidOP, "OP_" + nombreOP, fqnOP, "OPNJ", servicioBean.getVersion() + "." + servicioBean.getRevision(), aplicacion, "DISE_E21");
								if(chkExcelAsignarDRS.isEnabled() && chkExcelAsignarDRS.isSelected()) {
									crearFilaExcelASignarDRS(sheet_asignarDRS, fqnOP, "OPNJ", servicioBean.getVersion() + "." + servicioBean.getRevision(), drs);
								}
							}
						}
					}
					wb_infoOPs.close();
					fis.close();
				}
				crearDirectorio2DF(archivo, servicioBean);
			}
		}
		
		if(writer0AT != null) {
			writer0AT.close();
		}
		if(writer1CO != null) {
			writer1CO.close();
		}
		
		//ejecutarScriptSH(scriptDirectoryList);
		
		if(rdbEtapa21.isSelected()) {
			String nombreFicheroUnva = "ficheroCargaUNVA_" + txtPesp1.getText();
			guardarFichero(nombreFicheroUnva, workbook_cargaUnva);
			
			if(chkExcelAsignarDRS.isEnabled() && chkExcelAsignarDRS.isSelected()) {
				String nombreFicheroAsignarDRS = "ficheroAsignarDRS_" + txtPesp1.getText();
				guardarFichero(nombreFicheroAsignarDRS, workbook_asignarDRS);
			}
		}else if(rdbEtapa22.isSelected()) {
			String nombreFicheroUnva = "ficheroCargaUNVA_" + txtPesp1.getText();
			guardarFichero(nombreFicheroUnva, workbook_cargaUnva);
		}
		fileUtil.mostrarMensajeInformativo("Se han generado los branches correctamente en 2-DF.\nPuede realizar el commit de los cambios en el repositorio local de 2-DF.");
	}
	
	private void aniadiarRegeneracionAFicheroAnalisisFinal(ServicioUnvaBean servicioBean, List<String> rutas0AT) throws IOException {
		File file = new File(txtFicheroAnalisis.getText());
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		
		for(Row row: sheet){
			if(row.getRowNum() >= 1){
				String nombreActivo = row.getCell(1).getStringCellValue();
				String aplicacion = row.getCell(2).getStringCellValue().contains("-")?row.getCell(2).getStringCellValue().substring(2):row.getCell(2).getStringCellValue();
				String tipo = row.getCell(3).getStringCellValue();
				
				String nombreActivoBean = servicioBean.getFqn();
				if(servicioBean.getAplicacion().equals(aplicacion) &&
						nombreActivoBean.equals(nombreActivo) &&
						servicioBean.getTipoActivo().equals(tipo)) {
					
					StringBuilder regeneracion = new StringBuilder(rutas0AT.get(0).substring(0, rutas0AT.get(0).lastIndexOf(".") - 1));
					for(int i=1; i<rutas0AT.size(); i++) {
						regeneracion.append("\n").append(rutas0AT.get(i), 0, rutas0AT.get(i).lastIndexOf(".") - 1);
					}
					if(row.getCell(9) == null){
						row.createCell(9);
					}
					row.getCell(9).setCellValue(regeneracion.toString());
					break;
				}
			}
		}
		fis.close();
        FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
        wb.write(fileOut);
        fileOut.close();
        wb.close();
	}

	private void ejecutarScriptSH(List<String> scripts) {
		try {
			for(String script: scripts) {
				ProcessBuilder builder = new ProcessBuilder();
				
			    builder.command("cmd.exe", "/c", script);
			
			    String ruta = script.substring(0, script.lastIndexOf("\\"));
				builder.directory(new File(ruta));
				Process process = builder.start();
				int exitCode = process.waitFor();
				assert exitCode == 0;
			}
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
		}
    }
	
	private void guardarFichero(String nombre, Workbook workbook) throws IOException {
		
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Libro de Excel 97-2003 (*.xlsx)", "xlsx");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File(nombre));
	    
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// Write the output to a file
	    	File dirDestino = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + chooser.getSelectedFile().getName() + ".xlsx");
	        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
	        workbook.write(fileOut);
	        fileOut.close();

	        // Closing the workbook
	        workbook.close();
	    }
	}
	
	private BufferedWriter guardarFicheroSH(String nombre, List<String> scriptDirectoryList) throws IOException {
		BufferedWriter writer = null;
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Shell", "sh");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File(nombre));
	    
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// Write the output to a file
	    	writer = new BufferedWriter(new FileWriter(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + nombre + ".sh"));
	    	scriptDirectoryList.add(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + nombre + ".sh");
	    }
	    return writer;
	}
	
	
	private int mostrarMensaje() {
		return JOptionPane.showConfirmDialog(this, "Se va a realizar la operación.\n¿Está seguro de que quiere continuar?", "", JOptionPane.YES_NO_OPTION);
	}
	
	
	private void crearDirectorio2DF(File json, ServicioUnvaBean servicioBean) throws IOException{
		String ruta2DF = servicioBean.getInicioRuta2DF();
		ruta2DF = StringUtils.replaceOnce(ruta2DF, "?", servicioBean.getNombreActivo());

		String ruta2DFBranch = ruta2DF + "\\branches\\v" + servicioBean.getVersion() + "." + servicioBean.getRevision() + "_" + txtPesp1.getText() + "_v" + servicioBean.getArquitectura();
		String ruta2DFTag = ruta2DF + "\\tags\\" + servicioBean.getVersion() + "-" + servicioBean.getRevision();
		
		File directory = new File(ruta2DF + "\\tags\\");
		
		int majorMaximaRecorrida = 0;
		int minorMaximaRecorrida = 0;
		int revisionMaximaRecorrida = 0;
		
		int majorNueva = Integer.parseInt(servicioBean.getVersion().split("\\.")[0]);
		int minorNueva = Integer.parseInt(servicioBean.getVersion().split("\\.")[1]);
		int revisionNueva = Integer.parseInt(servicioBean.getRevision());
		
		for(File direc: Objects.requireNonNull(directory.listFiles())){
			String versionRecorriendo = direc.getCanonicalPath().substring(direc.getCanonicalPath().lastIndexOf("\\") + 1);
			int majorRecorriendo = Integer.parseInt(versionRecorriendo.split("[-.]")[0]);
			int minorRecorriendo = Integer.parseInt(versionRecorriendo.split("[-.]")[1]);
			int revisionRecorriendo = Integer.parseInt(versionRecorriendo.split("[-.]")[2]);
			if(majorMaximaRecorrida == 0){
				majorMaximaRecorrida = majorRecorriendo;
				minorMaximaRecorrida = minorRecorriendo;
				revisionMaximaRecorrida = revisionRecorriendo;
			}else{
				boolean cumple = majorRecorriendo < majorNueva || (majorRecorriendo == majorNueva && minorRecorriendo < minorNueva) || (majorRecorriendo == majorNueva && minorRecorriendo == minorNueva && revisionRecorriendo < revisionNueva);
				if(majorRecorriendo > majorMaximaRecorrida){
					if(cumple){
						majorMaximaRecorrida = majorRecorriendo;
						minorMaximaRecorrida = minorRecorriendo;
						revisionMaximaRecorrida = revisionRecorriendo;
					}
				}else if(majorRecorriendo == majorMaximaRecorrida && minorRecorriendo > minorMaximaRecorrida){
					if(cumple){
						minorMaximaRecorrida = minorRecorriendo;
						revisionMaximaRecorrida = revisionRecorriendo;
					}
				}else if(majorRecorriendo == majorMaximaRecorrida && minorRecorriendo == minorMaximaRecorrida && revisionRecorriendo > revisionMaximaRecorrida){
					if(cumple){
						revisionMaximaRecorrida = revisionRecorriendo;	
					}
				}
			}
		}
		String versionTagDondeCopiar = majorMaximaRecorrida + "." + minorMaximaRecorrida + "-" + revisionMaximaRecorrida;
		
		
		
		// comprobamos si la version nueva del activo es MENOR que la version maxima del activo en SVN
		// eso quiere decir que, por ejemplo, hay un PESP superior creado al que queremos crear
		// queremos crear PESP_2205 -> v3.5.1
		// existe 		  PESP_2207 -> v3.7.1
		boolean continuar = true;
		if(majorNueva < majorMaximaRecorrida) {
			continuar = false;
		}else if(majorNueva == majorMaximaRecorrida && minorNueva < minorMaximaRecorrida) {
			continuar = false;
		}else if(majorNueva == majorMaximaRecorrida && minorNueva == minorMaximaRecorrida && revisionNueva < revisionMaximaRecorrida) {
			continuar = false;
		}
		
		if(continuar) {
			File directorioCopiarCarpetas = new File(ruta2DF + "\\tags\\" + versionTagDondeCopiar);
			File directorioTag = new File(ruta2DFTag);
			File directorioBranch = new File(ruta2DFBranch);
			
			if(!directorioCopiarCarpetas.getAbsolutePath().equals(directorioTag.getAbsolutePath())) {
				FileUtils.copyDirectory(directorioCopiarCarpetas, directorioTag);
			}
			if(!directorioCopiarCarpetas.getAbsolutePath().equals(directorioBranch.getAbsolutePath())) {
				FileUtils.copyDirectory(directorioCopiarCarpetas, directorioBranch);	
			}
			
			File rutaDTDTag = new File(ruta2DFTag + "\\doc");
			File rutaDTDBranch = new File(ruta2DFBranch + "\\doc");
			int numeroArchivos = Objects.requireNonNull(rutaDTDTag.listFiles()).length;
			for(int i=0; i<numeroArchivos-1; i++){
				Objects.requireNonNull(rutaDTDTag.listFiles())[0].delete();
				Objects.requireNonNull(rutaDTDBranch.listFiles())[0].delete();
			}
			
			System.out.println(rutaDTDTag.getAbsolutePath());
			
			Objects.requireNonNull(rutaDTDTag.listFiles())[0].renameTo(new File(rutaDTDTag + "\\" + servicioBean.getNombreDTD() + ".docx"));
			Objects.requireNonNull(rutaDTDBranch.listFiles())[0].renameTo(new File(rutaDTDBranch + "\\" + servicioBean.getNombreDTD() + ".docx"));
			
			String nombreJsonNuevo = json.getAbsolutePath().substring(json.getAbsolutePath().lastIndexOf("\\") + 1);
			
			// eliminar el json que se ha copiado en la carpeta json tanto de branch como tag, y copiar el nuevo
			File rutaJsonBranch = new File(ruta2DFBranch + "\\json");
			
			if(!rutaJsonBranch.exists()){
				rutaJsonBranch.mkdir();
			}else{
				for(File file: Objects.requireNonNull(rutaJsonBranch.listFiles())) {
					file.delete();
				}
			}
			Files.copy(json.toPath(), (new File(ruta2DFBranch + "\\json\\" + nombreJsonNuevo)).toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			File rutaJsonTag = new File(ruta2DFTag + "\\json");
			if(!rutaJsonTag.exists()){
				rutaJsonTag.mkdir();
			}else{
				for(File file: Objects.requireNonNull(rutaJsonTag.listFiles())) {
					file.delete();
				}
			}
			Files.copy(json.toPath(), (new File(ruta2DFTag + "\\json\\" + nombreJsonNuevo)).toPath(), StandardCopyOption.REPLACE_EXISTING);	
		}else {
			System.out.println("No se ha tenido en cuenta el activo " + servicioBean.getNombreActivo() + " porque la version de donde se quiere copiar es mayor que la que se quiere crear");
		}
	}
	
	
	private void crearFilaExcelCargaUnva(XSSFSheet sheet, String guid, String nombre, String fqn, String tipoActivo, String version, String aplicacion, String etapaDise){
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		XSSFRow fila = sheet.createRow(rowTotal);
		
		if(etapaDise.equals("DISE_E21")) {
			crearCelda(fila, 0, guid);
			crearCelda(fila, 15, version.equals("3.0.1")?"Nueva":"Modificacion");
		}else {
			crearCelda(fila, 15, "Solo Dependencias");
		}
		crearCelda(fila, 1, txtPesp1.getText());
		crearCelda(fila, 2, txtPesp2.getText());
		crearCelda(fila, 3, nombre);
		crearCelda(fila, 4, fqn);
		crearCelda(fila, 5, tipoActivo);
		crearCelda(fila, 6, version);
		crearCelda(fila, 7, "Media");
		crearCelda(fila, 8, "Baja");
		crearCelda(fila, 9, etapaDise);
		crearCelda(fila, 10, "SLB1");
		crearCelda(fila, 11, etapaDise.equals("DISE_E21")?"Lote 1":"Lote 15");
		crearCelda(fila, 12, "INDRA");
		crearCelda(fila, 17, aplicacion);
	}

	private void crearCelda(XSSFRow fila, int celda, String valor){
		fila.createCell(celda).setCellValue(valor);
	}
	
	private void crearLineaSH(BufferedWriter writer0AT, BufferedWriter writer1CO, ServicioUnvaBean servicioBean, String tipoActivoCodi){
		try {
			servicioBean.writeFichero0AT(writer0AT, tipoActivoCodi);
			servicioBean.writeFichero1CO(writer1CO, tipoActivoCodi);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private void crearCabeceraExcelASignarDRS(XSSFSheet sheet){
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		XSSFRow fila = sheet.createRow(rowTotal);

		crearCelda(fila, 0, "Versión comercial");
		crearCelda(fila, 1, "PDP");
		crearCelda(fila, 2, "FQN");
		crearCelda(fila, 3, "Tipo");
		crearCelda(fila, 4, "Versión");
		crearCelda(fila, 5, "DRS Sistema");
		crearCelda(fila, 6, "Esfuerzo");
		crearCelda(fila, 7, "Eliminar relación");
	}
	
	
	// formato DRS: 735191 - AINF - GTER
	private void crearFilaExcelASignarDRS(XSSFSheet sheet, String fqn, String tipoActivo, String version, String drs){
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		XSSFRow fila = sheet.createRow(rowTotal);

		crearCelda(fila, 0, txtPesp1.getText());
		crearCelda(fila, 1, Constantes.PDP);
		crearCelda(fila, 2, fqn);
		crearCelda(fila, 3, tipoActivo);
		crearCelda(fila, 4, version);
		crearCelda(fila, 5, drs);
	}
}