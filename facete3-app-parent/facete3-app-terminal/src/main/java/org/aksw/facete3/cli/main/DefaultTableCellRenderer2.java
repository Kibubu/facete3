package org.aksw.facete3.cli.main;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableCellRenderer;


// Copy of DefaultTableCellRenderer2 with getContent made protected
/**
 * Default implementation of {@code TableCellRenderer}
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public class DefaultTableCellRenderer2<V>
	implements TableCellRenderer<V>
{
    @Override
    public TerminalSize getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex) {
        String[] lines = getContent(cell, columnIndex, rowIndex);
        int maxWidth = 0;
        for(String line: lines) {
            int length = TerminalTextUtils.getColumnWidth(line);
            if(maxWidth < length) {
                maxWidth = length;
            }
        }
        return new TerminalSize(maxWidth, lines.length);
    }

    @Override
    public void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics) {
        ThemeDefinition themeDefinition = table.getThemeDefinition();
        if((table.getSelectedColumn() == columnIndex && table.getSelectedRow() == rowIndex) ||
                (table.getSelectedRow() == rowIndex && !table.isCellSelection())) {
            if(table.isFocused()) {
                textGUIGraphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                textGUIGraphics.applyThemeStyle(themeDefinition.getSelected());
            }
            textGUIGraphics.fill(' ');  //Make sure to fill the whole cell first
        }
        else {
            textGUIGraphics.applyThemeStyle(themeDefinition.getNormal());
        }
        String[] lines = getContent(cell, columnIndex, rowIndex);
        int rowCount = 0;
        for(String line: lines) {
            textGUIGraphics.putString(0, rowCount++, line);
        }
    }

    protected String[] getContent(V cell, int columnIndex, int rowIndex) {
        String[] lines;
        if(cell == null) {
            lines = new String[] { "" };
        }
        else {
        	String str = toString(cell, columnIndex, rowIndex);
            lines = str.split("\r?\n");
        }
        return lines;
    }
    
    protected String toString(V cell, int columnIndex, int rowIndex) {
    	return cell.toString();
    }
}
