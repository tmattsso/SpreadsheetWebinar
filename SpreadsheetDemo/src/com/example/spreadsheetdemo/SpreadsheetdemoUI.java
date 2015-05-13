package com.example.spreadsheetdemo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("spreadsheetdemo")
public class SpreadsheetdemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SpreadsheetdemoUI.class, widgetset = "com.example.spreadsheetdemo.widgetset.SpreadsheetdemoWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		setContent(layout);

		final Spreadsheet sheet = new Spreadsheet();
		layout.addComponent(sheet);
	}

}