package com.telefonica.modulos.dependencias.pantalla;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GuiaImpactosModal extends JDialog {

	@Serial
	private static final long serialVersionUID = 1L;
	private JTable table;

	public GuiaImpactosModal(JFrame padre, boolean modal) throws IOException  {
		super(padre,modal);
		setResizable(false);
		setFont(new Font("Arial", Font.PLAIN, 12));
		setFont(new Font("Lucida Sans", Font.PLAIN, 14));
		setTitle("Guía de impactos");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		crearTabla();
		setLayout(new GridLayout(0, 1, 0, 0));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		
	    // Se visualiza la ventana.
	    pack();
	    setLocationRelativeTo(null);
	    setVisible(true);
	}
	
	private void crearTabla() throws IOException{
		table = new JTable();
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "TIPO", "IMPACTA A" }, 0) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		table.setRowSelectionAllowed(false);
		table.setModel(tableModel);
		rellenarTabla();
	}
	
	private void rellenarTabla() throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("guiaImpactos");
		if(is != null) {
			try (InputStreamReader streamReader =
                    new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(streamReader)) {

	            String line = reader.readLine();
                while (line != null) {
	            	((DefaultTableModel)table.getModel()).addRow(line.split("\\|"));
	    			line = reader.readLine();
	            }
            } catch (IOException a) {
	        	JOptionPane.showMessageDialog(null, a.getMessage());
	            System.out.println(a.getMessage());
	        }
			is.close();
		}
	}
}