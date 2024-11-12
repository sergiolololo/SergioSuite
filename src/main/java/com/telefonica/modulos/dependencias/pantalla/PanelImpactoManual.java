package com.telefonica.modulos.dependencias.pantalla;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.telefonica.modulos.dependencias.enumm.TipoActivoEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.telefonica.modulos.dependencias.utils.FileUtil;
import com.telefonica.modulos.dependencias.utils.JComboBoxValue;
import com.telefonica.interfaz.PantallaPrincipal;

@SuppressWarnings({"unchecked", "rawtypes" })
public class PanelImpactoManual extends JDialog {

	private final FileUtil fileUtil;

	private final JTable impactosManual;
	private final JTextField txtImpactoManual;
	private final JLabel lblQuery;

	private final JComboBox<String> cmbApp, cmbTipo, cmbServicios, cmbOperaciones, cmbQueries;
	private DefaultComboBoxModel modelCmbApp, modelCmbTipo, modelCmbServicio, modelCmbOperacion, modelCmbQueries;

	private final JCheckBox chkNuevaMajor;

	private final String ruta1COInfa, ruta1COPrte, ruta1COTerc, pesp;
	private final JLabel lblMetodo, lblServicio;
    private final JTextField txtApp;
	private final JRadioButton rdbOtro;

	public PanelImpactoManual(ApplicationContext appContext, String ruta1COInfa, String ruta1COPrte, String ruta1COTerc, String pesp, JTable impactosManual, JCheckBox chkAnalizarCodi) throws IOException  {
		super(appContext.getBean(PantallaPrincipal.class), true);

		fileUtil = appContext.getBean(FileUtil.class);

		this.impactosManual = impactosManual;
		this.ruta1COInfa = ruta1COInfa;
		this.ruta1COPrte = ruta1COPrte;
		this.ruta1COTerc = ruta1COTerc;
		this.pesp = pesp;

		setResizable(false);
		setFont(new Font("Lucida Sans", Font.PLAIN, 14));
		setTitle("Añadir impacto manual");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setShape(new RoundRectangle2D.Double(0, 0, 499, 305, 30, 30));
		setSize(499, 305);

		inicializarModelCombos();

		JCheckBox chkCompararAnalisis = new JCheckBox("Comparar excel y codi");
		chkCompararAnalisis.setEnabled(false);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 485, 268);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtImpactoManual = new JTextField();
		txtImpactoManual.setEnabled(false);
		txtImpactoManual.setBounds(77, 13, 383, 20);
		txtImpactoManual.setColumns(10);
		txtImpactoManual.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				txtImpactoManual.setToolTipText(txtImpactoManual.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtImpactoManual.setToolTipText(txtImpactoManual.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent e) {}
		});
		panel.add(txtImpactoManual);

		JLabel lblNewLabel = new JLabel("Impacto");
		lblNewLabel.setBounds(22, 16, 53, 13);
		panel.add(lblNewLabel);

		cmbApp = new JComboBox<>();
		cmbApp.setBounds(120, 90, 74, 21);
		cmbApp.setEnabled(false);
		cmbApp.setModel(modelCmbApp);
		cmbApp.setName("cmbApp");
		cmbApp.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(cmbApp.getSelectedIndex() > 0) {
						JComboBoxValue combo = (JComboBoxValue)e.getItem();
						crearModelCmbTipo(combo.getKey());
					}else {
						deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones, cmbServicios, cmbTipo));
						mostrarComboQueries(false);
					}
				}
			}
		});
		panel.add(cmbApp);

		txtApp = new JTextField();
		txtApp.setVisible(false);
		txtApp.setBounds(120, 90, 74, 20);
		txtApp.setColumns(10);
		panel.add(txtApp);

		cmbTipo = new JComboBox<>();
		cmbTipo.setBounds(229, 90, 74, 21);
		cmbTipo.setEnabled(false);
		cmbTipo.setModel(modelCmbTipo);
		cmbTipo.setName("cmbTipo");
		cmbTipo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(cmbTipo.getSelectedIndex() > 0) {
						JComboBoxValue item = (JComboBoxValue)e.getItem();
						txtImpactoManual.setText(item.getPlantilla());

						txtImpactoManual.setEnabled(item.toString().equals("OPEX") || item.toString().equals("OPNS"));
						crearModelCmbServicio(item);
					}else {
						txtImpactoManual.setText("");
						deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones, cmbServicios));
						mostrarComboQueries(false);
					}
				}
			}
		});
		panel.add(cmbTipo);

		cmbServicios = new JComboBox<>();
		cmbServicios.setBounds(77, 121, 383, 21);
		cmbServicios.setSelectedIndex(-1);
		cmbServicios.setEnabled(false);
		cmbServicios.setModel(modelCmbServicio);
		cmbServicios.setName("cmbServicios");
		cmbServicios.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(cmbServicios.getSelectedIndex() > 0) {
						JComboBoxValue item = (JComboBoxValue)e.getItem();
						String plantilla = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla();
						txtImpactoManual.setText(plantilla.replace("?", item.getNombreEnTabla()));

						String patron = "";
						String patronEliminar = "";
						if(cmbTipo.getSelectedItem().toString().equals("DAO")) {
							patron = ".*Repository\\.java";
							patronEliminar = ".java";
						}else if(cmbTipo.getSelectedItem().toString().equals("SRNU")) {
							patron = ".*Command\\.java";
							patronEliminar = "Command.java";
						}else if(cmbTipo.getSelectedItem().toString().equals("SRPR")) {
							patron = ".*ServiceImpl\\.java";
						}
						try {
							crearModelCmbOperacion(item, patron, patronEliminar);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}
					}else {
						String plantilla = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla();
						txtImpactoManual.setText(plantilla);
						deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones));
						mostrarComboQueries(false);
					}
				}
			}
		});
		panel.add(cmbServicios);

		cmbOperaciones = new JComboBox<>();
		cmbOperaciones.setBounds(77, 152, 383, 21);
		cmbOperaciones.setSelectedIndex(-1);
		cmbOperaciones.setEnabled(false);
		cmbOperaciones.setModel(modelCmbOperacion);
		cmbOperaciones.setName("cmbOperaciones");
		cmbOperaciones.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(cmbOperaciones.getSelectedIndex() > 0) {
						JComboBoxValue item = (JComboBoxValue)e.getItem();

						String plantilla2 = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla2();
						String nombreServicio = ((JComboBoxValue)cmbServicios.getSelectedItem()).getNombreEnTabla();
						if(plantilla2 != null) {
							String texto1 = StringUtils.replaceOnce(plantilla2, "?", nombreServicio);
							txtImpactoManual.setText(texto1.replace("?", item.getNombreEnTabla()));
						}

						try {
							crearModelCmbQueries(item);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}
					}else {
						String plantilla = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla();
						String nombreEnTabla = ((JComboBoxValue)cmbServicios.getSelectedItem()).getNombreEnTabla();
						txtImpactoManual.setText(!nombreEnTabla.isEmpty() ?plantilla.replace("?", nombreEnTabla):plantilla);

						deshabilitarCombos(Collections.singletonList(cmbQueries));
					}
				}
			}
		});
		panel.add(cmbOperaciones);

		cmbQueries = new JComboBox<>();
		cmbQueries.setBounds(77, 183, 383, 21);
		cmbQueries.setSelectedIndex(-1);
		cmbQueries.setEnabled(false);
		cmbQueries.setName("cmbQueries");
		cmbQueries.setModel(modelCmbQueries);
		cmbQueries.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                if(cmbQueries.getSelectedIndex() > 0) {
                    JComboBoxValue item = (JComboBoxValue)e.getItem();

                    String plantilla3 = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla3();
                    String nombreServicio = ((JComboBoxValue)cmbServicios.getSelectedItem()).getNombreEnTabla();
                    String nombreOperacion = ((JComboBoxValue)cmbOperaciones.getSelectedItem()).getValue();

                    String texto = StringUtils.replaceOnce(plantilla3, "?", nombreServicio);
                    texto = StringUtils.replaceOnce(texto, "?", nombreOperacion);
                    texto = StringUtils.replaceOnce(texto, "?", item.toString());

                    txtImpactoManual.setText(texto);
                }else {
                    String plantilla2 = ((JComboBoxValue)cmbTipo.getSelectedItem()).getPlantilla2();
                    String nombreServicio = ((JComboBoxValue)cmbServicios.getSelectedItem()).getNombreEnTabla();
                    String nombreOperacion = ((JComboBoxValue)cmbOperaciones.getSelectedItem()).getNombreEnTabla();
                    if(plantilla2 != null && nombreServicio != null && nombreOperacion != null) {
                        String texto = StringUtils.replaceOnce(plantilla2, "?", nombreServicio);
                        texto = StringUtils.replaceOnce(texto, "?", nombreOperacion);

                        txtImpactoManual.setText(texto);
                    }else {
                        txtImpactoManual.setText("");
                    }
                }
            }
        });
		panel.add(cmbQueries);

        JLabel lblApp = new JLabel("App");
		lblApp.setBounds(87, 94, 25, 13);
		panel.add(lblApp);

		JLabel lblNewLabel_1 = new JLabel("Tipo");
		lblNewLabel_1.setBounds(205, 94, 25, 13);
		panel.add(lblNewLabel_1);

		inicializarCombos(Arrays.asList(cmbApp, cmbTipo, cmbServicios, cmbOperaciones, cmbQueries));

		chkNuevaMajor = new JCheckBox("Nueva major");
		chkNuevaMajor.setBounds(323, 90, 98, 21);
		panel.add(chkNuevaMajor);

		lblServicio = new JLabel("Servicio");
		lblServicio.setBounds(22, 125, 63, 13);
		panel.add(lblServicio);

		lblMetodo = new JLabel("Método");
		lblMetodo.setBounds(22, 156, 63, 13);
		panel.add(lblMetodo);

		lblQuery = new JLabel("Query");
		lblQuery.setBounds(22, 187, 45, 13);
		panel.add(lblQuery);

		JButton btnAniadir = new JButton("AÑADIR");
		btnAniadir.setBounds(196, 225, 107, 33);
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtImpactoManual.getText().isEmpty() || txtImpactoManual.getText().contains("?")) {
					fileUtil.mostrarMensajeInformativo("Rellene todos los campos para continuar");
				}else if(existeImpactoManual()){
					fileUtil.mostrarMensajeInformativo("Ya existe el activo que quiere añadir");
				}else {
					String tipoActivo = getTipoActivoReal();
					String[] fila = new String[] { txtImpactoManual.getText(), rdbOtro.isSelected()?txtApp.getText():cmbApp.getSelectedItem().toString(), tipoActivo, chkNuevaMajor.isSelected()?"Si":"No" };
					((DefaultTableModel) impactosManual.getModel()).addRow(fila);
					txtImpactoManual.setText("");
					chkAnalizarCodi.setEnabled(true);
					deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones, cmbServicios, cmbTipo));
					cmbApp.setSelectedIndex(0);
					cmbTipo.setSelectedIndex(-1);
				}
			}
		});
		panel.add(btnAniadir);

		JRadioButton rdbGter = new JRadioButton("GTER");
		rdbGter.setSelected(true);
		rdbGter.setBounds(145, 51, 103, 21);
		rdbGter.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				setVisibleComponents(Arrays.asList(cmbApp,
						lblServicio,
						cmbServicios,
						lblMetodo,
						cmbOperaciones,
						lblQuery,
						cmbQueries), true);
				cmbTipo.setEnabled(false);
				txtApp.setVisible(false);
			}
		});

		panel.add(rdbGter);

		rdbOtro = new JRadioButton("Otro");
		rdbOtro.setBounds(261, 51, 103, 21);
		rdbOtro.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				setVisibleComponents(Arrays.asList(cmbApp,
						lblServicio,
						cmbServicios,
						lblMetodo,
						cmbOperaciones,
						lblQuery,
						cmbQueries), false);
				cmbTipo.setEnabled(true);
				txtApp.setVisible(true);
				crearModelCmbTipo("");
			}
		});
		panel.add(rdbOtro);

		ButtonGroup grupo1 = new ButtonGroup();
		grupo1.add(rdbGter);
		grupo1.add(rdbOtro);

		mostrarComboQueries(false);

		crearModelApp();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void setVisibleComponents(List<Object> listaComponentes, boolean setVisible){
		for(Object object: listaComponentes){
			((Component)object).setVisible(setVisible);
		}
	}

	private void inicializarModelCombos() {
		modelCmbApp = new DefaultComboBoxModel();
		modelCmbApp.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbTipo = new DefaultComboBoxModel();
		modelCmbTipo.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbServicio = new DefaultComboBoxModel();
		modelCmbServicio.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbOperacion = new DefaultComboBoxModel();
		modelCmbOperacion.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbQueries = new DefaultComboBoxModel();
		modelCmbQueries.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
	}

	private void inicializarCombos(List<JComboBox<String>> combos) {
		combos.forEach(jc -> {
			((DefaultComboBoxModel)jc.getModel()).addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
			jc.setSelectedIndex(0);
		});
	}

	private void deshabilitarCombos(List<JComboBox<String>> combos) {
		combos.forEach(jc -> jc.setSelectedIndex(0));
	}

	private void crearModelApp() {
		modelCmbApp = new DefaultComboBoxModel();
		modelCmbApp.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbApp.addElement(new JComboBoxValue(ruta1COInfa.substring(0, ruta1COInfa.lastIndexOf("\\")), "INFA", "", ""));
		modelCmbApp.addElement(new JComboBoxValue(ruta1COPrte.substring(0, ruta1COPrte.lastIndexOf("\\")), "PRTE", "", ""));
		modelCmbApp.addElement(new JComboBoxValue(ruta1COTerc.substring(0, ruta1COTerc.lastIndexOf("\\")), "TERC", "", ""));

		cmbApp.setEnabled(true);
		cmbApp.setModel(modelCmbApp);
		cmbApp.setSelectedIndex(0);
		deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones, cmbServicios, cmbTipo));
		mostrarComboQueries(false);
	}

	private void crearModelCmbTipo(String key) {
		modelCmbTipo = new DefaultComboBoxModel();
		modelCmbTipo.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		modelCmbTipo.addElement(new JComboBoxValue(key + TipoActivoEnum.CGT_NODE.getRuta1CO(), "CGT", TipoActivoEnum.CGT_NODE.getPlantillaNombre(), ""));

		JComboBoxValue combo = new JComboBoxValue(key + TipoActivoEnum.DAO.getRuta1CO(), "DAO", TipoActivoEnum.DAO.getPlantillaNombre(), "");
		combo.setPlantilla2(TipoActivoEnum.DAO.getPlantillaNombre() + ".?");
		combo.setPlantilla3(TipoActivoEnum.DAO.getPlantillaNombre() + ".?.?");
		modelCmbTipo.addElement(combo);

		modelCmbTipo.addElement(new JComboBoxValue("", "OPEX", TipoActivoEnum.OPEX.getPlantillaNombre(), ""));
		if(key.isEmpty()){
			modelCmbTipo.addElement(new JComboBoxValue("", "OPNJ", TipoActivoEnum.OPNJ.getPlantillaNombre(), ""));
		}
		modelCmbTipo.addElement(new JComboBoxValue("", "OPNS", TipoActivoEnum.OPNS.getPlantillaNombre(), ""));

		modelCmbTipo.addElement(new JComboBoxValue(key + TipoActivoEnum.RES.getRuta1CO(), "RES", TipoActivoEnum.RES.getPlantillaNombre(), ""));
		modelCmbTipo.addElement(new JComboBoxValue(key + TipoActivoEnum.SRV_EXP.getRuta1CO(), "SREX", TipoActivoEnum.SRV_EXP.getPlantillaNombre(), ""));
		modelCmbTipo.addElement(new JComboBoxValue(key + TipoActivoEnum.SRV_SOA.getRuta1CO(), "SRNS", TipoActivoEnum.SRV_SOA.getPlantillaNombre(), ""));

		combo = new JComboBoxValue(key + TipoActivoEnum.SRV_NUC.getRuta1CO(), "SRNU", TipoActivoEnum.SRV_NUC.getPlantillaNombre(), "");
		combo.setPlantilla2(TipoActivoEnum.OPNJ.getPlantillaNombre());
		modelCmbTipo.addElement(combo);
		combo = new JComboBoxValue(key + TipoActivoEnum.SRV_PRES.getRuta1CO(), "SRPR", TipoActivoEnum.SRV_PRES.getPlantillaNombre(), "");
		combo.setPlantilla2(TipoActivoEnum.OPPR.getPlantillaNombre());
		modelCmbTipo.addElement(combo);

		cmbTipo.setEnabled(true);
		cmbTipo.setModel(modelCmbTipo);
		cmbTipo.setSelectedIndex(0);
		deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones, cmbServicios));
		mostrarComboQueries(false);
	}

	private void crearModelCmbServicio(JComboBoxValue combo) {
		modelCmbServicio = new DefaultComboBoxModel();
		modelCmbServicio.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));
		File ruta = new File(combo.getKey());
		if(ruta.listFiles() != null) {
			for(File activo: ruta.listFiles()) {
				modelCmbServicio.addElement(new JComboBoxValue(activo.getAbsolutePath(), activo.getName(), "", activo.getName().substring(activo.getName().lastIndexOf("-")+1)));
			}
			cmbServicios.setEnabled(true);
		}else {
			cmbServicios.setEnabled(false);
		}
		cmbServicios.setModel(modelCmbServicio);
		cmbServicios.setSelectedIndex(0);
		deshabilitarCombos(Arrays.asList(cmbQueries, cmbOperaciones));
		mostrarComboQueries(false);

		if(rdbOtro.isSelected()){
			txtImpactoManual.setEnabled(true);
		}
	}

	private void crearModelCmbOperacion(JComboBoxValue combo, String patron, String patronEliminar) throws Exception {
		modelCmbOperacion = new DefaultComboBoxModel();
		modelCmbOperacion.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));

		Map<String, String> mapaKeyValue = new HashMap<>();
		Set<String> rutaOperaciones = new HashSet<>();

		File rutaUltimaVersion = fileUtil.fetchLastVersion2(new File(combo.getKey()), pesp);
		if(rutaUltimaVersion != null) {
			List<File> operaciones = fileUtil.findFiles(rutaUltimaVersion, patron, false);
			if(patron.equals(".*ServiceImpl\\.java")){
				fileUtil.buscarNombreMetodos(operaciones, rutaOperaciones, "Override");
			}else {
				for(File uu: operaciones) {
					String operacion = uu.getName().substring(0, 1).toLowerCase() + uu.getName().substring(1, uu.getName().indexOf(patronEliminar));
					rutaOperaciones.add(operacion);
					if(patron.equals(".*Repository\\.java")) {
						mapaKeyValue.put(operacion, uu.getAbsolutePath());
					}
				}
			}
			if(!rutaOperaciones.isEmpty()) {
				ArrayList<String> lstFinal = new ArrayList<>(rutaOperaciones);
				Collections.sort(lstFinal);

				lstFinal.forEach(p -> modelCmbOperacion.addElement(new JComboBoxValue(mapaKeyValue.get(p), p, "", p)));
				cmbOperaciones.setEnabled(true);
			}else {
				cmbOperaciones.setEnabled(false);
			}
		}else {
			cmbOperaciones.setEnabled(false);
		}
		cmbOperaciones.setModel(modelCmbOperacion);
		cmbOperaciones.setSelectedIndex(0);
		deshabilitarCombos(Collections.singletonList(cmbQueries));
		mostrarComboQueries(false);
	}

	private void crearModelCmbQueries(JComboBoxValue combo) throws Exception {
		modelCmbQueries = new DefaultComboBoxModel();
		modelCmbQueries.addElement(new JComboBoxValue(" ----- ", " ----- ", "", ""));

		if(combo.getKey() == null) {
			cmbQueries.setEnabled(false);
			mostrarComboQueries(false);

		}else {
			mostrarComboQueries(true);

			Set<String> rutaOperaciones = new HashSet<>();
			List<File> operaciones = List.of(new File(combo.getKey()));
			fileUtil.buscarNombreMetodos(operaciones, rutaOperaciones, "Query");

			if(!rutaOperaciones.isEmpty()) {
				ArrayList<String> lstFinal = new ArrayList<>(rutaOperaciones);
				Collections.sort(lstFinal);

				lstFinal.forEach(p -> modelCmbQueries.addElement(new JComboBoxValue(p, p, "", "")));
				cmbQueries.setEnabled(true);
			}else {
				cmbQueries.setEnabled(false);
			}
		}

		cmbQueries.setModel(modelCmbQueries);
		cmbQueries.setSelectedIndex(0);
	}

	private void mostrarComboQueries(boolean mostrar) {
		lblQuery.setVisible(mostrar);
		cmbQueries.setVisible(mostrar);
	}

	private boolean existeImpactoManual() {
		String tipoActivoReal = getTipoActivoReal();
		boolean encontrado = false;
		int rowCount = impactosManual.getModel().getRowCount();
		for (int i=0; i<rowCount; i++) {
			String activo = impactosManual.getModel().getValueAt(i, 0).toString();
			String app = impactosManual.getModel().getValueAt(i, 1).toString();
			String tipo = impactosManual.getModel().getValueAt(i, 2).toString();

			if(txtImpactoManual.getText().equals(activo) && cmbApp.getSelectedItem().toString().equals(app) && tipoActivoReal.equals(tipo)) {
				encontrado = true;
				break;
			}
		}
		return encontrado;
	}

	private String getTipoActivoReal() {
		String tipoActivo = ((JComboBoxValue)cmbTipo.getSelectedItem()).getValue();
		if(cmbOperaciones.getSelectedIndex() > 0) {
			switch (tipoActivo) {
				case "DAO": {
					break;
				}
				case "SRNU": {
					tipoActivo = "OPNJ";
					break;
				}
				case "SRPR": {
					tipoActivo = "OPPR";
					break;
				}
			}
		}
		return tipoActivo;
	}
}