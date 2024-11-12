package com.telefonica.modulos.dependencias.utils;

public class JComboBoxValue {

    private final String key, plantilla, nombreEnTabla, value;
    private String plantilla2;
    private String plantilla3;

    public JComboBoxValue(String key, String value, String plantilla, String nombreEnTabla) {
        this.key = key;
        this.value = value;
        this.plantilla = plantilla;
        this.nombreEnTabla = nombreEnTabla;
    }

	@Override
    public String toString() {
        return value;
    }
	
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public String getPlantilla() {
		return plantilla;
	}
	public String getPlantilla2() {
		return plantilla2;
	}
	public void setPlantilla2(String plantilla2) {
		this.plantilla2 = plantilla2;
	}
	public String getPlantilla3() {
		return plantilla3;
	}
	public void setPlantilla3(String plantilla3) {
		this.plantilla3 = plantilla3;
	}
	public String getNombreEnTabla() {
		return nombreEnTabla;
	}
}