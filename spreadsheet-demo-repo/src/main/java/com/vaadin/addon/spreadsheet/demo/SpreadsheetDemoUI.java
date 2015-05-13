package com.vaadin.addon.spreadsheet.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.addon.spreadsheet.Spreadsheet.SelectionChangeEvent;
import com.vaadin.addon.spreadsheet.Spreadsheet.SelectionChangeListener;
import com.vaadin.addon.spreadsheet.SpreadsheetFactory;
import com.vaadin.addon.spreadsheet.demo.examples.ComponentsExample;
import com.vaadin.addon.spreadsheet.demo.examples.SpreadsheetExample;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Demo class for the Spreadsheet component.
 * <p>
 * You can upload any xls or xlsx file using the upload component. You can also
 * place spreadsheet files on the classpath, under the folder /testsheets/, and
 * they will be picked up in a combobox in the menu.
 *
 *
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
@Theme("demo-theme")
@Title("Vaadin Spreadsheet Demo")
public class SpreadsheetDemoUI extends UI implements ValueChangeListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SpreadsheetDemoUI.class, widgetset = "com.vaadin.addon.spreadsheet.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	private Tree tree;
	private HorizontalSplitPanel horizontalSplitPanel;

	public SpreadsheetDemoUI() {
		super();
		setSizeFull();
		SpreadsheetFactory.logMemoryUsage();
	}

	@Override
	protected void init(VaadinRequest request) {

		horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setSplitPosition(300, Unit.PIXELS);
		horizontalSplitPanel.addStyleName("main-layout");

		final Link github = new Link("Source code on Github",
				new ExternalResource(
						"https://github.com/vaadin/spreadsheet-demo"));
		github.setIcon(FontAwesome.GITHUB);
		github.addStyleName("link");

		setContent(new CssLayout() {
			{
				setSizeFull();
				addComponent(horizontalSplitPanel);
				addComponent(github);
			}
		});

		final VerticalLayout content = new VerticalLayout();
		content.setSpacing(true);
		content.setMargin(new MarginInfo(false, false, true, false));

		final Label logo = new Label("Vaadin Spreadsheet");
		logo.addStyleName("h3");
		logo.addStyleName("logo");

		tree = new Tree();
		tree.setImmediate(true);
		tree.setContainerDataSource(getContainer());
		tree.setItemCaptionPropertyId("displayName");
		tree.setNullSelectionAllowed(false);
		tree.setWidth("100%");
		tree.addValueChangeListener(this);

		content.addComponents(logo, tree);
		horizontalSplitPanel.setFirstComponent(content);

		initSelection();
	}

	private void initSelection() {
		final Iterator<?> iterator = tree.getItemIds().iterator();
		if (iterator.hasNext()) {
			iterator.next();
		}
		if (iterator.hasNext()) {
			tree.select(iterator.next());
		}
	}

	private Container getContainer() {
		final HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
		hierarchicalContainer.addContainerProperty("displayName", String.class,
				"");

		final Item groupItem = hierarchicalContainer
				.addItem(ComponentsExample.class);
		groupItem.getItemProperty("displayName").setValue(
				splitCamelCase(ComponentsExample.class.getSimpleName()));
		hierarchicalContainer
		.setChildrenAllowed(ComponentsExample.class, false);

		final Collection<File> files = getFiles();
		for (final File file : files) {
			final Item fileItem = hierarchicalContainer.addItem(file);
			fileItem.getItemProperty("displayName").setValue(file.getName());
			hierarchicalContainer.setChildrenAllowed(file, false);
		}

		return hierarchicalContainer;
	}

	private Collection<File> getFiles() {
		File root = null;
		try {
			final ClassLoader classLoader = SpreadsheetDemoUI.class
					.getClassLoader();
			final URL resource = classLoader.getResource("testsheets"
					+ File.separator);
			if (resource != null) {
				root = new File(resource.toURI());
			}
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}

		final FilesystemContainer testSheetContainer = new FilesystemContainer(
				root);
		testSheetContainer.setRecursive(false);
		testSheetContainer.setFilter(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name != null
						&& (name.endsWith(".xls") || name.endsWith(".xlsx"))) {
					return true;
				} else {
					return false;
				}
			}
		});
		return testSheetContainer.getItemIds();
	}

	static String splitCamelCase(String s) {
		String replaced = s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
		replaced = replaced.replaceAll("Example", "");
		return replaced.trim();
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		final Object value = event.getProperty().getValue();
		if (value instanceof File || value instanceof Class) {
			open(value);
		} else {
			tree.expandItemsRecursively(value);
			if (tree.hasChildren(value)) {
				final Object firstChild = tree.getChildren(value).iterator()
						.next();
				open(firstChild);
				tree.setValue(firstChild);
			}
		}
	}

	private void open(Object value) {
		if (value instanceof File) {
			openFile((File) value);
		} else if (value instanceof Class) {
			openExample((Class) value);
		}
	}

	private void openExample(Class value) {
		try {
			final SpreadsheetExample example = (SpreadsheetExample) value
					.newInstance();
			horizontalSplitPanel.setSecondComponent(example.getComponent());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void openFile(File file) {
		try {
			final Spreadsheet spreadsheet = new Spreadsheet(file);
			horizontalSplitPanel.setSecondComponent(spreadsheet);

			spreadsheet
			.addSelectionChangeListener(new SelectionChangeListener() {

				@Override
				public void onSelectionChange(SelectionChangeEvent event) {

					spreadsheet.setStatusLabelValue(event
							.getAllSelectedCells().size()
							+ " cells selected");
				}
			});

		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
