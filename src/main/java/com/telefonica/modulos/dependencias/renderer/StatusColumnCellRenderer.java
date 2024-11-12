package com.telefonica.modulos.dependencias.renderer;

import java.awt.Component;
import java.awt.Font;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.telefonica.modulos.dependencias.pantalla.PanelAnalisisDependencias;

public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int col) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if(PanelAnalisisDependencias.analisisExcel.getModel().getRowCount() == PanelAnalisisDependencias.analisisCODI.getModel().getRowCount()) {
        	if (!Objects.equals(PanelAnalisisDependencias.analisisExcel.getModel().getValueAt(row, 0), PanelAnalisisDependencias.analisisCODI.getModel().getValueAt(row, 0))) {
            	this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont(14f));
            }
        }
        return this;
    }
}