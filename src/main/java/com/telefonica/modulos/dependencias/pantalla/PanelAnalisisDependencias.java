package com.telefonica.modulos.dependencias.pantalla;

import com.telefonica.modulos.dependencias.beans.DependenciaBean;
import com.telefonica.modulos.dependencias.renderer.StatusColumnCellRenderer;
import com.telefonica.modulos.dependencias.service.AnalisisDependenciasCodiService;
import com.telefonica.modulos.dependencias.utils.Constants;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import com.telefonica.interfaz.PantallaPrincipal;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes" })
@Component
public class PanelAnalisisDependencias extends JPanel {
	
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private AnalisisDependenciasCodiService analisisDependenciasCodiService;
	
	@Value("${ruta.carpeta.ficheros.analisis}")
	private String rutaFicheroAnalisis;
	@Value("${ruta.1CO}")
	private String ruta1CO;
	@Value("${nombre.carpeta.analisis.recientes}")
	private String carpetaAnalisisRecientes;
	@Value("${nombre.carpeta.analisis.recientes.consola}")
	private String carpetaAnalisisRecientesConsola;

    private final JTextField txtRutaficheroDependencias;
    public static JTextField txtPesp;
	
	public static JTable analisisExcel;
	public static JTable analisisCODI;
	public static JTable impactosManual;
	
	private List<String> listaActivosCodi = new ArrayList<>();
	private List<String> listaActivosDise = new ArrayList<>();
	
	public static JTextField txtRuta1COInfa;
	public static JTextField txtRuta1COPrte;
	public static JTextField txtRuta1COTerc;
	
	private static JButton btnGuardarAnlisis;
	public static JButton btnCargarAnalisis;
	private static JButton btnGenerarExcelAnlisis;
	
	public static JLabel lblAnalisisDise;
	public static JLabel lblAnalisisCodi;
	public static JLabel lblGuia;
	
	public static DefaultListModel<String> modelo = new DefaultListModel<>();
	public static JList<String> listRecientes;
	
	private boolean nuevoAnalisisDisponible;
	private final JButton btnEliminar;
	
	private final JCheckBox chkAnalizarDise;
	private JCheckBox chkAnalizarCodi;
	private PanelConsola panelConsola;
	private final JButton btnAbrirConsola;
	
	public PanelAnalisisDependencias() {

        JButton btnEmpezar = new JButton("EMPEZAR");
		btnEmpezar.setBounds(15, 168, 125, 33);
        btnEmpezar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if((!chkAnalizarDise.isEnabled() || !chkAnalizarDise.isSelected()) && (!chkAnalizarCodi.isEnabled() || !chkAnalizarCodi.isSelected())) {
        			fileUtil.mostrarMensajeInformativo("Seleccione un análisis a realizar");
        		}else {
					if(comprobarSiProcesar()) {
						llamarDialogoEspera();
						nuevoAnalisisDisponible = true;
					}
        		}
        	}
        });

        JLabel lblRutaFicheroDependencias = new JLabel("* Ruta fichero análisis dependencias");
        lblRutaFicheroDependencias.setBounds(325, 60, 264, 14);
        
        txtRutaficheroDependencias = new JTextField();
        txtRutaficheroDependencias.setName("txtRutaficheroDependencias");
        txtRutaficheroDependencias.setBounds(325, 80, 221, 20);
        txtRutaficheroDependencias.setEnabled(false);
        txtRutaficheroDependencias.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtRutaficheroDependencias.setText(fileUtil.seleccionarDirectorio(JFileChooser.FILES_ONLY, rutaFicheroAnalisis + txtPesp.getText()));
        		if(txtRutaficheroDependencias.getText().isEmpty()) {
        			chkAnalizarDise.setEnabled(false);
        			if(impactosManual.getModel().getRowCount() == 0) {
        				chkAnalizarCodi.setEnabled(false);
        			}
        		}else {
        			chkAnalizarDise.setEnabled(true);
        			chkAnalizarCodi.setEnabled(true);
        		}
        	}
        });
        txtRutaficheroDependencias.setColumns(10);

        JLabel lblVersionComercial = new JLabel("* Versión comercial");
        lblVersionComercial.setBounds(325, 11, 137, 13);
        
        txtPesp = new JTextField();
        txtPesp.setName("txtPesp");
        txtPesp.setBounds(325, 30, 147, 20);
        txtPesp.setColumns(10);
        
        analisisExcel = crearTablas(true);
        analisisCODI = crearTablas(true);
        impactosManual = crearTablas(false);
        
        JScrollPane scrollPane = new JScrollPane(analisisExcel);
        JScrollPane scrollPane_1 = new JScrollPane(analisisCODI);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setModel(scrollPane_1.getVerticalScrollBar().getModel());
        
        lblAnalisisDise = new JLabel("Análisis Excel");
        lblAnalisisDise.setBounds(15, 257, 129, 13);
        lblAnalisisCodi = new JLabel("Análisis CODI");
        lblAnalisisCodi.setBounds(611, 257, 133, 13);
        JLabel lblRutaDirectorio = new JLabel("* Ruta directorio 1-CO local INFA");
        lblRutaDirectorio.setBounds(15, 10, 235, 14);
        
        txtRuta1COInfa = new JTextField();
        txtRuta1COInfa.setName("txtRuta1COInfa");
        txtRuta1COInfa.setBounds(15, 30, 235, 20);
        txtRuta1COInfa.setEnabled(false);
        txtRuta1COInfa.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		String directorio = fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "INFA"));
        		txtRuta1COInfa.setText(directorio);
        		if(directorio.contains("INFA")) {
        			directorio = directorio.replace("INFA", "?");
        			txtRuta1COPrte.setText(directorio.replace("?", "PRTE"));
        			txtRuta1COTerc.setText(directorio.replace("?", "TERC"));
        		}
        	}
        });
        
        txtRuta1COInfa.setColumns(10);
        
        JLabel lblRutaDirectorio_1 = new JLabel("* Ruta directorio 1-CO local PRTE");
        lblRutaDirectorio_1.setBounds(15, 60, 235, 14);
        
        txtRuta1COPrte = new JTextField();
        txtRuta1COPrte.setName("txtRuta1COPrte");
        txtRuta1COPrte.setBounds(15, 80, 235, 20);
        txtRuta1COPrte.setEnabled(false);
        txtRuta1COPrte.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtRuta1COPrte.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "PRTE")));
        	}
        });
        
        txtRuta1COPrte.setColumns(10);
        
        JLabel lblRutaDirectorio_2 = new JLabel("* Ruta directorio 1-CO local TERC");
        lblRutaDirectorio_2.setBounds(15, 104, 235, 14);
        
        txtRuta1COTerc = new JTextField();
        txtRuta1COTerc.setName("txtRuta1COTerc");
        txtRuta1COTerc.setBounds(15, 124, 235, 20);
        txtRuta1COTerc.setEnabled(false);
        txtRuta1COTerc.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtRuta1COTerc.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "TERC")));
        	}
        });
        
        txtRuta1COTerc.setColumns(10);
        
        chkAnalizarCodi = new JCheckBox("Analizar codi");
        chkAnalizarCodi.setSelected(true);
        
        JCheckBox chkCompararAnalisis = new JCheckBox("Comparar excel y codi");
        chkCompararAnalisis.setEnabled(false);
        
        listRecientes = new JList<>();
        listRecientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRecientes.addListSelectionListener(arg0 -> {
            if (!arg0.getValueIsAdjusting()) {
                btnCargarAnalisis.setEnabled(listRecientes.getSelectedValue() != null);
            }
        });
        
        JScrollPane scrollPaneRecientes = new JScrollPane(listRecientes);
        scrollPaneRecientes.setBounds(979, 31, 208, 122);
        
        JLabel lblAnalisisRecientes = new JLabel("Análisis recientes");
        lblAnalisisRecientes.setBounds(979, 11, 175, 13);
        
        btnCargarAnalisis = new JButton("CARGAR");
        btnCargarAnalisis.setBounds(1087, 167, 98, 33);
        btnCargarAnalisis.setEnabled(false);
        btnCargarAnalisis.addActionListener(e -> {
            String mensaje = "¿Desea cargar el análisis seleccionado?";
            if(nuevoAnalisisDisponible) {
                mensaje = "Existe un análisis sin guardar. ¿Está seguro que quiere cargar el análisis seleccionado?";
            }
            int opcion = mostrarMensaje(mensaje);
            if(opcion == 0) {
                try {
                    cargarAnalisis();
                    btnGenerarExcelAnlisis.setEnabled(true);
                    nuevoAnalisisDisponible = false;
                }catch(IOException | ParseException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
        btnGuardarAnlisis = new JButton("GUARDAR");
        btnGuardarAnlisis.setBounds(979, 168, 98, 33);
        btnGuardarAnlisis.setEnabled(false);
        btnGuardarAnlisis.addActionListener(e -> {
            String mensaje = "¿Desea guardar el análisis?";
            if(existeAnalisisReciente()) {
                mensaje = "Ya existe un análisis guardado de la VC " + txtPesp.getText() + ". ¿Está seguro de que quiere guardar otro?";
            }
            int opcion = mostrarMensaje(mensaje);
            if(opcion == 0) {
                guardarAnalisis();
                nuevoAnalisisDisponible = false;
            }
        });
        
        JPanel panel_1 = new JPanel();
        panel_1.setBounds(15, 280, 1179, 375);
        
        btnGenerarExcelAnlisis = new JButton("GENERAR EXCEL ANÁLISIS");
        btnGenerarExcelAnlisis.setBounds(158, 168, 221, 33);
        btnGenerarExcelAnlisis.setEnabled(false);
        btnGenerarExcelAnlisis.addActionListener(e -> generarExcelAnalisis());
        panel_1.setLayout(new GridLayout(0, 2, 0, 0));
        panel_1.add(scrollPane);
        panel_1.add(scrollPane_1);
        setLayout(null);
        add(lblRutaDirectorio);
        add(lblVersionComercial);
        add(lblAnalisisRecientes);
        add(btnEmpezar);
        add(btnGenerarExcelAnlisis);
        add(btnGuardarAnlisis);
        add(btnCargarAnalisis);
        add(panel_1);
        add(txtRuta1COInfa);
        add(txtRuta1COPrte);
        add(txtRuta1COTerc);
        add(lblRutaDirectorio_1);
        add(lblRutaDirectorio_2);
        add(lblRutaFicheroDependencias);
        add(txtRutaficheroDependencias);
        add(txtPesp);
        add(lblAnalisisDise);
        add(scrollPaneRecientes);
        add(lblAnalisisCodi);
        
        JScrollPane jscroll = new JScrollPane(impactosManual);
        jscroll.setBounds(611, 32, 334, 152);
        
        add(jscroll);
        
        JLabel lblImpactosManual = new JLabel("Impactos manual");
        lblImpactosManual.setBounds(611, 11, 90, 13);
        add(lblImpactosManual);
        
        lblGuia = new JLabel("(Ver guía de impactos)");
        lblGuia.setToolTipText("Guía de impactos");
        lblGuia.setForeground(new Color(0, 0, 255));
        lblGuia.setBounds(711, 11, 147, 13);
        lblGuia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblGuia.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
            	try {
            		PantallaPrincipal appFrame = appContext.getBean(PantallaPrincipal.class);
					new GuiaImpactosModal(appFrame, true);
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
            }
        }); 
        
        add(lblGuia);
        
        JButton btnAniadir = new JButton("AÑADIR");
        btnAniadir.addActionListener(e -> {
            if(txtRuta1COInfa.getText().isEmpty() || txtRuta1COPrte.getText().isEmpty() || txtRuta1COTerc.getText().isEmpty()) {
                String mensaje = "Antes de continuar, informe las rutas de 1-CO";
                fileUtil.mostrarMensajeInformativo(mensaje);
            }else if(txtPesp.getText().isEmpty()) {
                String mensaje = "Antes de continuar, informe la versión comercial";
                fileUtil.mostrarMensajeInformativo(mensaje);
            }else if(!txtPesp.getText().startsWith("PESP_") || txtPesp.getText().length() < 9){
                String mensaje = "Por favor, informe una versión comercial correcta";
                fileUtil.mostrarMensajeInformativo(mensaje);
            }else {
                try {
                    new PanelImpactoManual(appContext, txtRuta1COInfa.getText(), txtRuta1COPrte.getText(), txtRuta1COTerc.getText(), txtPesp.getText(), impactosManual, chkAnalizarCodi);
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
            }
        });
        btnAniadir.setBounds(679, 194, 107, 33);
        add(btnAniadir);
        
        btnEliminar = new JButton("ELIMINAR");
        btnEliminar.addActionListener(e -> {
            int opcion = mostrarMensaje("¿Desea eliminar el registro?");
            if(opcion == 0) {
                ((DefaultTableModel)impactosManual.getModel()).removeRow(impactosManual.getSelectedRow());

                if(impactosManual.getModel().getRowCount() == 0 && txtRutaficheroDependencias.getText().isEmpty()) {
                    chkAnalizarCodi.setEnabled(false);
                }
            }
        });
        btnEliminar.setEnabled(false);
        btnEliminar.setBounds(807, 194, 107, 33);
        add(btnEliminar);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, Color.LIGHT_GRAY));
        panel.setBounds(588, 0, 376, 244);
        add(panel);
        
        chkAnalizarDise = new JCheckBox("Analizar dise");
        chkAnalizarDise.setEnabled(false);
        chkAnalizarDise.setBounds(325, 123, 98, 21);
        add(chkAnalizarDise);
        
        chkAnalizarCodi = new JCheckBox("Analizar codi");
        chkAnalizarCodi.setEnabled(false);
        chkAnalizarCodi.setBounds(425, 123, 98, 21);
        add(chkAnalizarCodi);
        
        btnAbrirConsola = new JButton("ABRIR CONSOLA");
        btnAbrirConsola.addActionListener(e -> panelConsola.setVisible(true));
        btnAbrirConsola.setEnabled(false);
        btnAbrirConsola.setBounds(1057, 241, 137, 33);
        add(btnAbrirConsola);
	}

	private void generarExcelAnalisis() {
		String mensa = "¿Desea generar el excel con el resultado del análisis? En ese ";
		String mensa2 = "caso, seleccione la tabla de la que desea generarlo.";
		Set<String> tablaSeleccionada = new HashSet<>();
		tablaSeleccionada.add("Diseño");

		JRadioButton rbtn1 = new JRadioButton("Diseño", true);
		rbtn1.setFont(new Font("Arial", Font.PLAIN, 15));
		JRadioButton rbtn2 = new JRadioButton("Codificación", false);
		rbtn2.setFont(new Font("Arial", Font.PLAIN, 15));

		rbtn1.addItemListener(e13 -> {
			if(rbtn1.isSelected()) {
				tablaSeleccionada.add(rbtn1.getText());
				tablaSeleccionada.remove(rbtn2.getText());
			}
		});
		rbtn2.addItemListener(e12 -> {
			if(rbtn2.isSelected()) {
				tablaSeleccionada.add(rbtn2.getText());
				tablaSeleccionada.remove(rbtn1.getText());
			}
		});

		ButtonGroup grupo1 = new ButtonGroup();
		grupo1.add(rbtn1);
		grupo1.add(rbtn2);

		JPanel prueba = new JPanel();
		prueba.add(rbtn1);
		prueba.add(rbtn2);

		Object[] params = {mensa, mensa2, prueba};

		int opcion = JOptionPane.showConfirmDialog(null, params, "", JOptionPane.YES_NO_OPTION);
		if(opcion == 0) {
			JTable tabla;
			if(tablaSeleccionada.contains("Diseño")) {
				tabla = analisisExcel;
			}else {
				tabla = analisisCODI;
			}

			try {
				InputStream is = getClass().getClassLoader().getResourceAsStream(Constants.NOMBRE_PLANTILLA_DEPENDENCIAS);
				XSSFWorkbook workbook = new XSSFWorkbook(is);
				XSSFSheet sheet = workbook.getSheetAt(0);
				sheet.getRow(1).getCell(1).setCellValue("IMPACTOS POR DEPENDENCIAS " + txtPesp.getText());

				int rowCount = tabla.getModel().getRowCount();
				for (int i=0; i<rowCount; i++) {
					if(!tabla.getModel().getValueAt(i, 0).toString().equals("---")) {
						int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
						XSSFRow fila = sheet.createRow(rowTotal);
						fila.createCell(1).setCellValue(tabla.getModel().getValueAt(i, 0).toString());
						fila.createCell(2).setCellValue(tabla.getModel().getValueAt(i, 1).toString());
						fila.createCell(3).setCellValue(tabla.getModel().getValueAt(i, 2).toString());
					}
				}
				fileUtil.guardarFichero("Dependencias_" + txtPesp.getText() + "_Analisis_FINAL", workbook, rutaFicheroAnalisis + txtPesp.getText());
				is.close();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
			btnGenerarExcelAnlisis.setEnabled(false);
		}
	}

	private boolean comprobarSiProcesar() {
		boolean procesar = false;
		if(txtRuta1COInfa.getText().isEmpty() || txtRuta1COPrte.getText().isEmpty() ||
				txtRuta1COTerc.getText().isEmpty() || txtPesp.getText().isEmpty()) {
			fileUtil.mostrarMensajeInformativo("Hay campos obligatorios sin rellenar");
		}else {
			if(txtRutaficheroDependencias.getText().isEmpty() && impactosManual.getModel().getRowCount() == 0) {
				fileUtil.mostrarMensajeInformativo("Rellene la ruta del fichero de dependencias y/o \nañada impactos manual antes de continuar");
			}else {
				if(impactosManual.getModel().getRowCount() > 0) {
					// comprobamos si hay algun campo informado, pero NO todos, en cuyo caso sacamos mensaje
					if(txtRutaficheroDependencias.getText().isEmpty()) {
						int opcion = mostrarMensaje("Faltan campos por informar. Si continúa, solo se procesará el análisis manual.\n¿Está seguro de que quiere continuar?");
						if(opcion == 0) {
							if(existeAnalisisReciente()) {
								int opcion2 = mostrarMensaje("Ya existe un análisis guardado de la VC " + txtPesp.getText() + ". ¿Está seguro de que quiere continuar?");
								if(opcion2 == 0) {
									procesar = true;
								}
							}else {
								procesar = true;
							}
						}
					}else {
						String mensaje = "Se va a realizar la operación.\n¿Está seguro de que quiere continuar?";
						if(existeAnalisisReciente()) {
							mensaje = "Ya existe un análisis guardado de la VC " + txtPesp.getText() + ". ¿Está seguro de que quiere continuar?";
						}
						int opcion = mostrarMensaje(mensaje);
						if(opcion == 0) {
							procesar = true;
						}
					}
				}else {
					String mensaje = "Se va a realizar la operación.\n¿Está seguro de que quiere continuar?";
					if(existeAnalisisReciente()) {
						mensaje = "Ya existe un análisis guardado de la VC " + txtPesp.getText() + ". ¿Está seguro de que quiere continuar?";
					}
					int opcion = mostrarMensaje(mensaje);
					if(opcion == 0) {
						procesar = true;
					}
				}
			}
		}
		return procesar;
	}

	private void cargarAnalisis() throws IOException, ParseException {
		borrarTablas();
		String nombreArchivoList = listRecientes.getSelectedValue();
		
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMATO_FECHA_PANTALLA);
		String dateInString = nombreArchivoList.split(" - ")[0];
		Date date = formatter.parse(dateInString);
		String sdf = new SimpleDateFormat(Constants.FORMATO_FECHA_FICHERO).format(date);
		
		String nombreArchivo = sdf + " " + nombreArchivoList.split(" - ")[1];
		
		btnCargarAnalisis.setEnabled(false);
		txtPesp.setText(nombreArchivoList.split(" - ")[1]);
		
		File file = new File("../" + carpetaAnalisisRecientes);
		for(File file2: file.listFiles()) {
			if(file2.getName().contains(nombreArchivo)) {
				if(file2.getName().endsWith("dise")) {
					cargarTabla(file2, analisisExcel);
				}else if(file2.getName().endsWith("codi")) {
					cargarTabla(file2, analisisCODI);
				}
			}
		}

		file = new File("../" + carpetaAnalisisRecientes + carpetaAnalisisRecientesConsola);
		for(File file2: file.listFiles()) {
			if(file2.getName().contains(nombreArchivo)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file2));
					List<String> lines = br.lines().toList();
					br.close();
					if(PanelConsola.textTA == null)  {
						panelConsola = new PanelConsola(appContext);
					}
					PanelConsola.textTA.setText(String.join("\n", lines));
					btnAbrirConsola.setEnabled(true);
					PanelConsola.closeBtn.setEnabled(true);
				} catch(IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	private void cargarTabla(File file2, JTable tabla) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file2));
			String line = br.readLine();
		    while (line != null) {
		    	String[] fila = line.split("\\|");
		    	((DefaultTableModel) tabla.getModel()).addRow(fila);
		        line = br.readLine();
		    }
		    br.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	private void borrarTablas() {
		clearTable((DefaultTableModel)analisisExcel.getModel());
		clearTable((DefaultTableModel)analisisCODI.getModel());
	}
	
	private void guardarAnalisis() {
		File rutaFichero;
		Date fecha = new Date();
        generarFichero("dise", analisisExcel, fecha);
        rutaFichero = generarFichero("codi", analisisCODI, fecha);
		aniadirLinea(rutaFichero);
		crearFicheroConsola(fecha);
	}

	private void crearFicheroConsola(Date fecha) {
		try {
			File rutaFichero = crearFichero("consola", fecha, carpetaAnalisisRecientesConsola);
			Files.write(rutaFichero.toPath(), Collections.singletonList(PanelConsola.textTA.getText()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private File generarFichero(String tipo, JTable analisis, Date fecha) {
		File rutaFichero = null;
		try {
			rutaFichero = crearFichero(tipo, fecha, "");
			
			List<String> listaFilasExcel = new ArrayList<>();
			for(int i=0; i<analisis.getModel().getRowCount(); i++) {
				StringBuilder fila = new StringBuilder();
				for(int j=0; j<analisis.getModel().getColumnCount(); j++) {
					fila.append(analisis.getModel().getValueAt(i, j)).append("|");
				}
				fila = new StringBuilder(fila.substring(0, fila.lastIndexOf("|")));
				listaFilasExcel.add(fila.toString());
			}
			Files.write(rutaFichero.toPath(), listaFilasExcel, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return rutaFichero;
	}
	
	private File crearFichero(String tabla, Date fecha, String rutaConsola) {
		File rutaFichero = null;
		try {
			// generamos el fichero
			String sdf = new SimpleDateFormat(Constants.FORMATO_FECHA_FICHERO).format(fecha);
			File rutaCarpeta = new File("../" + carpetaAnalisisRecientes + rutaConsola);
			if(!rutaCarpeta.exists()) {
				rutaCarpeta.mkdir();
			}
			
			String nombre = sdf + " " + txtPesp.getText() + "_" + tabla;
			rutaFichero = new File(rutaCarpeta.getAbsolutePath() + "\\" + nombre);
			FileOutputStream fileOut = new FileOutputStream(rutaFichero.getAbsolutePath());
			fileOut.close();
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
		return rutaFichero;
	}
	
	private void aniadirLinea(File rutaFichero) {
		// añadimos linea al listado
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMATO_FECHA_FICHERO);
			String dateInString = rutaFichero.getName().split(" ")[0];
			Date date = formatter.parse(dateInString);
			String s = new SimpleDateFormat(Constants.FORMATO_FECHA_PANTALLA).format(date);
			
			String aux = rutaFichero.getName().split(" ")[1];
			aux = aux.substring(0, aux.lastIndexOf("_"));
			String nombre = s  + " - " + aux;
			
			modelo.add(0, nombre);
			listRecientes.setModel(modelo);
			btnGuardarAnlisis.setEnabled(false);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private boolean existeAnalisisReciente() {
		boolean encontrado = false;
		for(int i=0; i<listRecientes.getModel().getSize(); i++) {
			String reciente = listRecientes.getModel().getElementAt(i);
			if(reciente.contains(txtPesp.getText())) {
				encontrado = true;
				break;
			}
		}
		return encontrado;
	}
	
	private JTable crearTablas(boolean renderer){
		JTable tabla = new JTable() {
			@Serial
			private static final long serialVersionUID = 1L;
            public String getToolTipText(MouseEvent e) {
                String tip;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                tip = getValueAt(rowIndex, colIndex)!=null?getValueAt(rowIndex, colIndex).toString():"";

                return tip;
            }
		};
		if(renderer) {
			tabla.setModel(new DefaultTableModel(new Object[] { "ACTIVO", "APLICACIÓN", "TIPO" }, 0));
			tabla.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnCellRenderer());
			tabla.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer());
			tabla.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
		}else {
			
			DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "ACTIVO", "APP", "TIPO", "MAJOR" }, 0) {
			    @Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			
			tabla.setModel(tableModel);
			tabla.getSelectionModel().addListSelectionListener(arg0 -> {
                if (!arg0.getValueIsAdjusting()) {
                    btnEliminar.setEnabled(tabla.getSelectedRow() >= 0);
                }
            });
		}
		tabla.getColumnModel().getColumn(0).setPreferredWidth(300);
		return tabla;
	}
	
	private void ejecutarAnalisisDependenciasCodi(String rutaFicheroDependencias) throws Exception {
		List<DependenciaBean> impactosManuales = new ArrayList<>();
		for(int i=0; i<impactosManual.getModel().getRowCount(); i++) {
			DependenciaBean bean = new DependenciaBean();
			bean.setNombre(impactosManual.getModel().getValueAt(i, 0).toString());
			bean.setAplicacion(impactosManual.getModel().getValueAt(i, 1).toString());
			bean.setTipo(impactosManual.getModel().getValueAt(i, 2).toString());

			if(!Arrays.asList("INFA", "PRTE", "TERC").contains(bean.getAplicacion())){
				bean.setProceso("NO_GTER");
			}else{
				bean.setProceso("GTER");
			}

			bean.setVersion("3.3.1");
			bean.setCambioMajor(impactosManual.getModel().getValueAt(i, 3).toString().equals("Si"));
			impactosManuales.add(bean);
		}
		analisisDependenciasCodiService.ejecutarAnalisisDependencias(rutaFicheroDependencias, listaActivosCodi, impactosManuales, txtPesp.getText(),
				txtRuta1COInfa.getText(), txtRuta1COPrte.getText(), txtRuta1COTerc.getText());
	}
	
	private void clearTable(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }
	
	private void ejecutarAnalisisDependencias(String rutaFicheroDependencias) throws IOException {
		Map<String, DependenciaBean> mapaActivos = new HashMap<>();
		
		FileInputStream fis = new FileInputStream(rutaFicheroDependencias);
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		try{
			for(int i=0; i<wb.getNumberOfSheets(); i++) {
				XSSFSheet sheet = wb.getSheetAt(i);
				if(sheet.getSheetName().equals("Análisis dependencias completo")) {
					for(Row row: sheet){
						if(row.getRowNum() >= 2){
							String nombreServicio = row.getCell(11).getStringCellValue();
							String tipoServicio = row.getCell(10).getStringCellValue();
							String aplicacion = row.getCell(9)!=null&&!row.getCell(9).getStringCellValue().isEmpty()?row.getCell(9).getStringCellValue().substring(2):"";
							String proyecto = row.getCell(8).getStringCellValue();
							String versionDependencia = row.getCell(6).getStringCellValue();
							String causal = row.getCell(5).getStringCellValue();
							String keyMapa = aplicacion + "-" + tipoServicio + "-" + nombreServicio;

							DependenciaBean servicio = new DependenciaBean();
							servicio.setNombre(nombreServicio);
							servicio.setTipo(tipoServicio);
							servicio.setAplicacion(aplicacion);
							servicio.setCausal(causal);

							// columna 7 es "Proyecto"
							if(proyecto.contains("GTER")){
								if(tipoServicio.equals("CNT")){
									mapaActivos.put(keyMapa, servicio);
								}else if(tipoServicio.equals("RES") ||
										tipoServicio.equals("SRNU") ||
										tipoServicio.equals("SRPR") ||
										tipoServicio.equals("JTNU") ||
										tipoServicio.equals("CGT") ||
										tipoServicio.equals("SRNS")){

									double profundidad = row.getCell(12).getNumericCellValue();

									if(profundidad == 1 && versionDependencia.endsWith(".1") && (tipoServicio.equals("JTNU") || tipoServicio.equals("CGT"))){
										mapaActivos.put(keyMapa, servicio);
									}else if(profundidad == 2 && versionDependencia.endsWith(".1") && (tipoServicio.equals("RES"))){
										mapaActivos.put(keyMapa, servicio);
									}else if(profundidad > 1 && versionDependencia.endsWith(".1") && (tipoServicio.equals("SRNU"))) {
										mapaActivos.put(keyMapa, servicio);
									}else if(profundidad == 3 && versionDependencia.endsWith(".1") && (tipoServicio.equals("SRPR"))) {
										mapaActivos.put(keyMapa, servicio);
									}else if(profundidad == 2 && versionDependencia.contains(".0.1") && (tipoServicio.equals("SRNS"))) {
										mapaActivos.put(keyMapa, servicio);
									}
								}
							}
						}
					}
				}
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}

		wb.close();
		fis.close();
		
		for (Entry<String, DependenciaBean> entry : mapaActivos.entrySet()) {
			DependenciaBean activo = entry.getValue();
			listaActivosDise.add(activo.getAplicacion() + " -> " + activo.getTipo() + " -> " + activo.getNombre() + " -> " + activo.getCausal());
		}
		Collections.sort(listaActivosDise);
	}
	
	private int mostrarMensaje(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
	
	private void llamarDialogoEspera() {
		
		panelConsola = new PanelConsola(appContext);
		PanelConsola.addText("-------- Empieza el proceso de análisis de dependencias --------\n\n");
		
		final JDialog waitForTrans = new JDialog();
		JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Procesando análisis...");
		final JOptionPane optionPane = new JOptionPane(progressBar, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        waitForTrans.setSize(200,200);
    	waitForTrans.setLocationRelativeTo(null);
    	waitForTrans.setTitle("Espere...");
    	waitForTrans.setModal(true);
    	waitForTrans.setContentPane(optionPane);
    	waitForTrans.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		SwingWorker worker = new SwingWorker() {
			public String doInBackground()  {
					try {
						Date startDate = new Date();
						
						listaActivosDise = new ArrayList<>();
						listaActivosCodi = new ArrayList<>();
						
						List<String> listaActivos1 = new ArrayList<>();
						List<String> listaActivos2 = new ArrayList<>();
						boolean primeroEsDise = true;
						if(chkAnalizarDise.isEnabled() && chkAnalizarDise.isSelected()) {
							ejecutarAnalisisDependencias(txtRutaficheroDependencias.getText());
							listaActivos1 = listaActivosDise;
							listaActivos2 = listaActivosCodi;
                        }
						if(chkAnalizarCodi.isEnabled() && chkAnalizarCodi.isSelected()) {
							ejecutarAnalisisDependenciasCodi(txtRutaficheroDependencias.getText());
							if(!chkAnalizarDise.isEnabled() || !chkAnalizarDise.isSelected()) {
								listaActivos1 = listaActivosCodi;
								listaActivos2 = listaActivosDise;
								primeroEsDise = false;
							}
						}
		    			compararTablas(listaActivos1, listaActivos2, primeroEsDise);
		    			btnGuardarAnlisis.setEnabled(true);
		    			btnGenerarExcelAnlisis.setEnabled(true);
		    			
		    			Date endDate = new Date();
		    			int numSeconds = (int)((endDate.getTime() - startDate.getTime()) / 1000);
		    			fileUtil.mostrarMensajeInformativo("Tiempo en procesar el análisis: " + numSeconds + " segundos");
						PanelConsola.addText("\n-------- Proceso de análisis de dependencias finalizado --------");
		    			PanelConsola.closeBtn.setEnabled(true);
		    			btnAbrirConsola.setEnabled(true);
					} catch (Exception e) {
						System.out.println(e.getLocalizedMessage());
					}
				return null;
			}
			public void done() {
//				waitForTrans.setVisible(false);
//				waitForTrans.dispose();
//				panelConsola.setVisible(false);
//				panelConsola.dispose();
			}
		};
		
		worker.execute();
//		waitForTrans.pack();
//		waitForTrans.setVisible(true);
		panelConsola.setVisible(true);
	}
	
	private void compararTablas(List<String> listaActivos1, List<String> listaActivos2, boolean primeroEsDise) {
		for(int i=0; i<listaActivos1.size(); i++) {
			String aux1 = listaActivos1.get(i);
			String activoCodi = aux1.split(" -> ")[2] + aux1.split(" -> ")[0] + aux1.split(" -> ")[1];
			
			String aux2 = "";
			try {
				aux2 = listaActivos2.get(i);
			}catch(IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
			}
			
			if(aux2.isEmpty()) {
				listaActivos2.add(i, "--- -> --- -> --- -> ---");
			}else {
				String activoDise = aux2.split(" -> ")[2] + aux2.split(" -> ")[0] + aux2.split(" -> ")[1];
				if(!activoCodi.equals(activoDise)) {
					// la posicion del listado codi no coincide con la posicion del listado dise
					// intentamos buscar el activoDise en el listado de activoCodi
					int posiciones = 0;
					boolean encontrado = false;
					for(int j=i+1; j<listaActivos2.size(); j++) {
						posiciones++;
						aux2 = listaActivos2.get(j);
						activoDise = aux2.split(" -> ")[2] + aux2.split(" -> ")[0] + aux2.split(" -> ")[1];
						
						if(activoCodi.equals(activoDise)) {
							encontrado = true;
							break;
						}
					}
					if(encontrado) {
						for(int j=0; j<posiciones; j++) {
							listaActivos1.add(i, "--- -> --- -> --- -> ---");
						}
						i+=posiciones-1;
					}else {
						listaActivos2.add(i, "--- -> --- -> --- -> ---");
					}
				}
			}
		}
		if(listaActivos2.size() > listaActivos1.size()) {
			for(int i=listaActivos1.size(); i< listaActivos2.size(); i++) {
				listaActivos1.add(i, "--- -> --- -> --- -> ---");
			}
		}
		
		if(primeroEsDise) {
			DefaultTableModel modelDise = (DefaultTableModel) analisisExcel.getModel();
			clearTable(modelDise);
			for(String activo: listaActivos1){
				modelDise.addRow(new Object[] { activo.split(" -> ")[2], activo.split(" -> ")[0],  activo.split(" -> ")[1]});
			}
			DefaultTableModel modelCodi = (DefaultTableModel) analisisCODI.getModel();
			clearTable(modelCodi);
			for(String activo: listaActivos2){
				modelCodi.addRow(new Object[] { activo.split(" -> ")[2], activo.split(" -> ")[0],  activo.split(" -> ")[1] });
			}
		}else {
			DefaultTableModel modelCodi = (DefaultTableModel) analisisCODI.getModel();
			clearTable(modelCodi);
			for(String activo: listaActivos1){
				modelCodi.addRow(new Object[] { activo.split(" -> ")[2], activo.split(" -> ")[0],  activo.split(" -> ")[1] });
			}
			DefaultTableModel modelDise = (DefaultTableModel) analisisExcel.getModel();
			clearTable(modelDise);
			for(String activo: listaActivos2){
				modelDise.addRow(new Object[] { activo.split(" -> ")[2], activo.split(" -> ")[0],  activo.split(" -> ")[1] });
			}
		}
	}
}