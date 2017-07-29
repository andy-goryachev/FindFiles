// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.fx.CAction;
import goryachev.fx.CCheckMenuItem;
import goryachev.fx.CMenu;
import goryachev.fx.CMenuBar;
import goryachev.fx.FX;
import goryachev.fx.FxDump;
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
	public final CAction copyAction = new CAction(this::copy);
	public final static CAction newWindowAction = new CAction(MainWindow::newWindow);
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
	
	
	protected CMenuBar createMenu()
	{
		CMenu m;
		CMenuBar mb = new CMenuBar();
		// file
		mb.add(m = new CMenu("FindFiles"));		
		m.add("Search Locations");
		m.separator();
		m.add("New Window", newWindowAction);
		m.separator();
		m.add("Preferences");
		m.separator();
		m.add("Quit", FX.exitAction());
		// edit
		mb.add(m = new CMenu("Edit"));
		m.add("Copy", copyAction);
		// view
		mb.add(m = new CMenu("View"));
		m.add(new CCheckMenuItem("Detail Pane Below", pane.horizontalSplit));
		// help
		mb.add(m = new CMenu("Help"));
		m.add("Check for Update");
		m.add("Contact Support");
		m.separator();
		m.add("License");
		m.add("Open Source Licenses");
		m.separator();
		m.add("About");
		return mb;
	}
	
	
	protected Node createStatusBar()
	{
		HPane p = new HPane();
		p.setPadding(new Insets(1, 10, 1, 10));
		
		p.add(FX.label(statusProperty, Color.GRAY));
		p.fill();
		p.add(FX.label("copyright © 2017 andy goryachev", Color.GRAY));
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