package com.telefonica.modulos.dependencias.service;

import com.telefonica.modulos.dependencias.pantalla.PanelConsola;
import com.telefonica.modulos.dependencias.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public abstract class ActivoComunService {

    private Set<String> listaBuscar1;
    private Map<String, Set<String>> listaDaosApp;
    private Set<String> listaBuscar3;

	@Autowired
	protected FileUtil fileUtil;

	public void buscarOcurrenia(File dir, String activoBuscar, String arbolDependencias, List<String> ocurrenciasCausal) {
        if(activoBuscar == null){
            ocurrenciasCausal = new ArrayList<>();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(dir))) {
            AtomicBoolean terminarBucle = new AtomicBoolean();
            terminarBucle.set(false);

            String line = br.readLine();
            while (line != null && !terminarBucle.get()) {
                procesar(line, activoBuscar, dir, ocurrenciasCausal, arbolDependencias, terminarBucle, br);
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}

    protected void incluirArbolDependencias(String activoBuscar, String ocurrencia, List<String> ocurrenciasCausal) {
        if(activoBuscar == null){
            List<String> listaOcurrenciasExiste = new ArrayList<>();
            if (FileUtil.mapaCausalArbol.get(ocurrencia) != null) {
                listaOcurrenciasExiste = FileUtil.mapaCausalArbol.get(ocurrencia);
            }
            listaOcurrenciasExiste.addAll(ocurrenciasCausal);
            FileUtil.mapaCausalArbol.put(ocurrencia, listaOcurrenciasExiste);
        }
    }

    protected String buscarLinea(String line, String activoBuscar) {
		return null;
	}
	
	protected abstract String getOcurrencia(String line);
	
	protected abstract String getTipoActivo1();
	
	protected abstract String getTipoActivo2();
	
	protected abstract String getTipoActivo3();
	
	protected abstract String getNombreActivo(File dir);
	
	protected String getNombreActivo2(String nombreActivo){
		return nombreActivo;
	}
    public void setListaBuscar1(Set<String> listaBuscar1) {
        this.listaBuscar1 = listaBuscar1;
    }
    public void setListaDaosApp(Map<String, Set<String>> listaDaosApp) {
        this.listaDaosApp = listaDaosApp;
    }
    public void setListaBuscar3(Set<String> listaBuscar3) {
        this.listaBuscar3 = listaBuscar3;
    }
    public Set<String> getListaBuscar1() {
        return listaBuscar1;
    }
    public Map<String, Set<String>> getListaDaosApp() {
        return listaDaosApp;
    }
    public Set<String> getListaBuscar3() {
        return listaBuscar3;
    }
	
	protected void buscarSiguiente(String nombreActivo, String arbolDependencias, List<String> ocurrenciasCausal, File dir) throws Exception {
	}
	
	protected abstract void aniadirActivoEncontrado(String nombreActivo);

    protected void procesar(String line, String activoBuscar, File dir, List<String> ocurrenciasCausal, String arbolDependencias, AtomicBoolean terminarBucle, BufferedReader br) throws Exception {
        String ocurrencia = buscarLinea(line, activoBuscar);
        if (ocurrencia != null) {
            String nombreActivo = getNombreActivo(dir);
            aniadirActivoEncontrado(nombreActivo);

            String pintar = arbolDependencias + nombreActivo + " (" + getTipoActivo3() + ")";
            ocurrenciasCausal.add(pintar);
            PanelConsola.addText(pintar);
            System.out.println(pintar);
            arbolDependencias += "      ";

            nombreActivo = getNombreActivo2(nombreActivo);
            String aplicacion = fileUtil.obtenerAplicacion(dir.getAbsolutePath());
            FileUtil.listaActivos.add(aplicacion + " -> " + getTipoActivo3() + " -> " + nombreActivo);

            buscarSiguiente(nombreActivo, arbolDependencias, ocurrenciasCausal, dir);
            incluirArbolDependencias(activoBuscar, ocurrencia, ocurrenciasCausal);
            terminarBucle.set(true);
        }
    }
}