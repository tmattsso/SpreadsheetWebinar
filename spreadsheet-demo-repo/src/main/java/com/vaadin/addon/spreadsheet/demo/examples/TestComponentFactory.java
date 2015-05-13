package com.vaadin.addon.spreadsheet.demo.examples;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.addon.spreadsheet.SpreadsheetComponentFactory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

@SuppressWarnings("serial")
public class TestComponentFactory implements SpreadsheetComponentFactory {

	private Field<?> customEditor;
	private Cell selectedCell;

	@Override
	public Component getCustomComponentForCell(Cell cell, int rowIndex,
			int columnIndex, Spreadsheet spreadsheet, Sheet sheet) {
		if (columnIndex == 2 && rowIndex == 2) {
			return createCombobox();
		}
		if (columnIndex == 2 && rowIndex == 4) {
			return createDateField();
		}
		return null;
	}

	private Component createDateField() {
		// not tied to actual cell value
		final DateField dateField = new DateField();
		dateField.setSizeFull();
		return dateField;
	}

	private Component createCombobox() {
		// not tied to actual cell value

		final List<String> values = new ArrayList<String>();
		values.add("Value 1");
		values.add("Value 2");
		values.add("Value 3");
		values.add("Value 4");

		final ComboBox comboBox = new ComboBox(null, values);
		comboBox.setSizeFull();
		return comboBox;
	}

	@Override
	public Component getCustomEditorForCell(final Cell cell, int rowIndex,
			int columnIndex, final Spreadsheet spreadsheet, Sheet sheet) {

		if (columnIndex == 2 && rowIndex == 6) {

			selectedCell = cell;

			final List<String> values = new ArrayList<String>();
			values.add("Value 1");
			values.add("Value 2");
			values.add("Value 3");
			values.add("Value 4");

			customEditor = new NativeSelect(null, values);
			customEditor.setSizeFull();
			customEditor.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					// update data value
					selectedCell.setCellValue((String) customEditor.getValue());
					// tell spreadsheet that data changed
					spreadsheet.refreshCells(selectedCell);
				}
			});

			return customEditor;
		}
		return null;
	}

	@Override
	public void onCustomEditorDisplayed(Cell cell, int rowIndex,
			int columnIndex, Spreadsheet spreadsheet, Sheet sheet,
			Component customEditor) {

		if (cell == null) {
			// create empty cell
			cell = sheet.getRow(rowIndex).createCell(columnIndex);
		}
		selectedCell = cell;

	}

}