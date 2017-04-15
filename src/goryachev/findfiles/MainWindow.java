// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.fx.CCheckMenuItem;
import goryachev.fx.CMenu;
import goryachev.fx.CMenuBar;
import goryachev.fx.FX;
import goryachev.fx.FxDump;
import goryachev.fx.FxWindow;
import goryachev.fx.HPane;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Color;


/**
 * Find Files Main Window.
 */
public class MainWindow
	extends FxWindow
{
	public final MainPane pane;
	
	
	public MainWindow()
	{
		super("MainWindow");
		
		pane = new MainPane();
				
		setTitle("Find Files " + Version.VERSION);
		setTop(createMenu());
		setCenter(pane);
		setBottom(createStatusBar());
		setSize(600, 700);

		// preferences
		bind("HSPLIT", pane.horizontalSplit);

		// debugging
		FxDump.attach(this);
	}
	
	
	protected CMenuBar createMenu()
	{
		CMenu m;
		CMenuBar mb = new CMenuBar();
		// file
		mb.add(m = new CMenu("File"));
		m.add("Preferences");
		m.separator();
		m.add("Quit", FX.exitAction());
		// edit
		mb.add(m = new CMenu("Edit"));
//		m.add("Save Selection As...");
		// view
		mb.add(m = new CMenu("View"));
		m.add(new CCheckMenuItem("Detail Pane Below", pane.horizontalSplit));
		// help
		mb.add(m = new CMenu("Help"));
		m.add("About");
		return mb;
	}
	
	
	protected Node createStatusBar()
	{
		HPane p = new HPane();
		p.fill();
		p.add(FX.label("copyright © 2017 andy goryachev", Color.GRAY, new Insets(1, 10, 1, 2)));
		return p;
	}
}