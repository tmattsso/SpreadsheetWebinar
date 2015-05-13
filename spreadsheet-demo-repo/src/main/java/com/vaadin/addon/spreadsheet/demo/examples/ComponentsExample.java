package com.vaadin.addon.spreadsheet.demo.examples;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.addon.spreadsheet.SpreadsheetComponentFactory;
import com.vaadin.addon.spreadsheet.action.SpreadsheetDefaultActionHandler;
import com.vaadin.addon.spreadsheet.demo.SpreadsheetDemoUI;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Component;

public class ComponentsExample implements SpreadsheetExample {

	private Spreadsheet spreadsheet;
	private final SpreadsheetComponentFactory spreadsheetFieldFactory = new TestComponentFactory();
	private final Handler spreadsheetActionHandler = new SpreadsheetDefaultActionHandler();

	public ComponentsExample() {

		try {
			initSpreadsheet();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Component getComponent() {
		return spreadsheet;
	}

	private void initSpreadsheet() throws IOException {
		File root = null;
		try {
			final ClassLoader classLoader = SpreadsheetDemoUI.class
					.getClassLoader();
			final URL resource = classLoader.getResource("ComponentTest.xlsx");
			if (resource != null) {
				root = new File(resource.toURI());
			}
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}

		spreadsheet = new Spreadsheet(root);
		spreadsheet.setSpreadsheetComponentFactory(spreadsheetFieldFactory);
		spreadsheet.addActionHandler(spreadsheetActionHandler);
	}

	public Spreadsheet getSpreadsheet() {
		return spreadsheet;
	}

}
