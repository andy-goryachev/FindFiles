// Copyright © 2017-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxCheckMenuItem;
import goryachev.fx.FxDump;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxWindow;
import goryachev.fx.HPane;
import goryachev.fx.edit.FxEditor;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;


/**
 * Main Application Window.
 */
public class MainWindow
	extends FxWindow
{
	public final FxAction copyAction = new FxAction(this::copy);
	public final static FxAction newWindowAction = new FxAction(MainWindow::newWindow);
	public final MainPane pane;
	public final SimpleStringProperty statusProperty = new SimpleStringProperty();
	
	
	public MainWindow()
	{
		super("MainWindow");
		
		pane = new MainPane(statusProperty);
				
		setTitle("Find Files " + Version.VERSION);
		setTop(createMenu());
		setCenter(pane);
		setBottom(createStatusBar());
		setSize(600, 700);

		// preferences
		bind("HSPLIT", pane.horizontalSplit);
		
		FX.later(() -> pane.searchField.requestFocus());

		// debugging
		FxDump.attach(this);
	}
	
	
	protected FxMenuBar createMenu()
	{
		FxMenuBar m = new FxMenuBar();
		// file
		m.menu("FindFiles");		
		m.item("Search Locations");
		m.separator();
		m.item("New Window", newWindowAction);
		m.separator();
		m.item("Preferences");
		m.separator();
		m.item("Quit", FX.exitAction());
		// edit
		m.menu("Edit");
		m.item("Copy", copyAction);
		// view
		m.menu("View");
		m.add(new FxCheckMenuItem("Detail Pane Below", pane.horizontalSplit));
		// help
		m.menu("Help");
		m.item("Check for Update");
		m.item("Contact Support");
		m.separator();
		m.item("License");
		m.item("Open Source Licenses");
		m.separator();
		m.item("About");
		return m;
	}
	
	
	protected Node createStatusBar()
	{
		HPane p = new HPane();
		p.setPadding(new Insets(1, 10, 1, 10));
		
		p.add(FX.label(statusProperty, Color.GRAY));
		p.fill();
		p.add(FX.label(Version.COPYRIGHT, Color.GRAY));
		return p;
	}
	
	
	public static void newWindow()
	{
		new MainWindow().open();
	}
	
	
	public void copy()
	{
		// this could be a generic action
		Node n = getLastFocusOwner();
		if(n != null)
		{
			if(n instanceof TextInputControl)
			{
				((TextInputControl)n).copy();
			}
			else if(n instanceof FxEditor)
			{
				((FxEditor)n).copy();
			}
//			else if(n instanceof TableView) // damn
//			{
//				((TableView)n).copy();
//			}
		}
	}
}