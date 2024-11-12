package com.telefonica.interfaz;

import com.telefonica.modulos.busqueda.pantalla.PanelBusqueda;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelComparacion;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablas;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablasFK;
import com.telefonica.modulos.comparador.poms.pantalla.PanelComparadorPoms;
import com.telefonica.modulos.dependencias.pantalla.PanelAnalisisDependencias;
import com.telefonica.modulos.cargaunva.pantalla.PanelCargaUNVA;
import com.telefonica.nomodulos.pantalla.PanelConsolidacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class PantallaPrincipal extends JFrame {

    @Autowired
	private PanelCargaUNVA panelCargaUNVA;
	@Autowired
	private PanelAnalisisDependencias panelAnalisisDependencias;
	@Autowired
	private PanelBusqueda panelBusqueda;
	@Autowired
	private PanelComparadorPoms pomComparatorMainFrame;
	@Autowired
	private PanelConsolidacion panelConsolidacion;
	@Autowired
	private PanelTablas panelTablas;
	@Autowired
	private PanelComparacion panelComparacion;
	@Autowired
	private PanelTablasFK panelTablasFK;

	@Value("${nombre.carpeta.analisis.recientes}")
	private String carpetaAnalisisRecientes;
	
	private JPanel contentPane;

    public PantallaPrincipal() {
	}
	
	public void init() throws IOException, ParseException  {
		
		addWindowListener(new WindowAdapter() {
        });
		setTitle("Sergio Suite");
		
		BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/tools_terminal_rabbit_12989.png")));
		setIconImage(image);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
		setResizable(false);
		setSize(1221, 716);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setVisible(true);
		setJMenuBar(menuBar);

        JMenu menu = new JMenu("Menú");
		menuBar.add(menu);

        JMenuItem menuAnalisisDependencias = new JMenuItem("Análisis dependencias");
		menuAnalisisDependencias.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
			setSize(1221, 716);
			setResizable(false);
			setLocationRelativeTo(null);
            CardLayout c = (CardLayout)(contentPane.getLayout());
            c.show(contentPane, "panelAnalisisDependencias");
        });
		menu.add(menuAnalisisDependencias);

		JMenuItem menuBusqueda = new JMenuItem("Búsqueda archivos");
		menuBusqueda.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 890, 650, 30, 30));
			setSize(890, 650);
			setResizable(false);
			setLocationRelativeTo(null);
			CardLayout c = (CardLayout)(contentPane.getLayout());
			c.show(contentPane, "panelBusqueda");
		});
		menu.add(menuBusqueda);

        JMenuItem menuCargaUNVA = new JMenuItem("Carga UNVA");
		menuCargaUNVA.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
			setSize(1221, 716);
			setResizable(false);
			setLocationRelativeTo(null);
            CardLayout c = (CardLayout)(contentPane.getLayout());
            c.show(contentPane, "panelCargaUNVA");
        });
		menu.add(menuCargaUNVA);

        JMenuItem menuComparadorPoms = new JMenuItem("Comparador de poms");
		menuComparadorPoms.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
			setSize(1221, 716);
			setResizable(false);
			setLocationRelativeTo(null);
            CardLayout c = (CardLayout)(contentPane.getLayout());
            c.show(contentPane, "pomComparatorMainFrame");
        });
		menu.add(menuComparadorPoms);

        JMenuItem menuComparadorCatalogacion = new JMenuItem("Comparador catalogación");
		menuComparadorCatalogacion.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
			setSize(1221, 716);
			setShape(null);
			setResizable(true);
			setLocationRelativeTo(null);
            CardLayout c = (CardLayout)(contentPane.getLayout());
            c.show(contentPane, "panelTablas");
        });
		menu.add(menuComparadorCatalogacion);

		JMenuItem menuConsolidacion = new JMenuItem("Consolidación");
		menuConsolidacion.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
			setSize(1221, 716);
			setResizable(false);
			setLocationRelativeTo(null);
			CardLayout c = (CardLayout)(contentPane.getLayout());
			c.show(contentPane, "panelConsolidacion");
		});
		menu.add(menuConsolidacion);
		
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		contentPane = new JPanel();
		getContentPane().add(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));


        JPanel panelVacio = new JPanel();
		panelVacio.setBounds(0, 0, 1106, 732);
		panelVacio.setBackground(SystemColor.inactiveCaption);
		panelVacio.setLayout(null);
		contentPane.add(panelVacio, "panelVacio");
		
		contentPane.add(panelCargaUNVA, "panelCargaUNVA");
		contentPane.add(panelAnalisisDependencias, "panelAnalisisDependencias");
		contentPane.add(panelBusqueda, "panelBusqueda");
		contentPane.add(pomComparatorMainFrame, "pomComparatorMainFrame");
		contentPane.add(panelConsolidacion, "panelConsolidacion");
		panelTablas.setContentPane(contentPane);
		contentPane.add(panelTablas, "panelTablas");
		panelComparacion.setContentPane(contentPane);
		contentPane.add(panelComparacion, "panelComparacion");
		panelTablasFK.setContentPane(contentPane);
		contentPane.add(panelTablasFK, "panelTablasFK");
		
		Set<String> listaAnalisis = new HashSet<>();
		File file = new File("../" + carpetaAnalisisRecientes);
		if(file.exists() && file.listFiles().length > 0) {
			for(int i=file.listFiles().length-1; i>0; i--) {
				File file2 = file.listFiles()[i];
				if(file2.isFile()) {
					String analisis = file2.getName().substring(0, file2.getName().lastIndexOf("_"));
					if(!listaAnalisis.contains(analisis)) {
						SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
						String dateInString = file2.getName().split(" ")[0];
						Date date = formatter.parse(dateInString);
						String s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);

						String aux = file2.getName().split(" ")[1];
						aux = aux.substring(0, aux.lastIndexOf("_"));
						String nombre = s  + " - " + aux;
						PanelAnalisisDependencias.modelo.addElement(nombre);
						PanelAnalisisDependencias.listRecientes.setModel(PanelAnalisisDependencias.modelo);

						listaAnalisis.add(analisis);
					}
				}
			}
		}
		
		changeFont(this, new Font("Arial", Font.PLAIN, 12));
		changeFontMenu(menu, new Font("Arial", Font.BOLD, 16));
		
		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 12));
        
        PanelAnalisisDependencias.lblAnalisisDise.setFont(new Font("Arial", Font.BOLD, 14));
        PanelAnalisisDependencias.lblAnalisisCodi.setFont(new Font("Arial", Font.BOLD, 14));
        PanelAnalisisDependencias.lblGuia.setFont(new Font("Arial", Font.PLAIN, 10));
		PanelComparacion.lblNombreTabla.setFont(new Font("Arial", Font.BOLD, 14));
	}
	
	private static void changeFont(Component component, Font font) {
	    component.setFont(font);
	    if (component instanceof Container) {
	        for (Component child : ((Container)component).getComponents()) {
	            changeFont(child, font);
	        }
	    }
	}
	
	private static void changeFontMenu(Component component, Font font) {
		component.setFont(font);
	    if(component instanceof JMenu) {
	    	for (int i=0; i<((JMenu)component).getItemCount(); i++) {
	    		((JMenu)component).getItem(i).setFont(new Font("Arial", Font.BOLD, 12));
	        }
	    }
	}
}