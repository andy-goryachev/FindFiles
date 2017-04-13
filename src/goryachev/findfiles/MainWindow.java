// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CKit;
import goryachev.fx.CMenu;
import goryachev.fx.CMenuBar;
import goryachev.fx.FX;
import goryachev.fx.FxDump;
import goryachev.fx.FxWindow;
import goryachev.fx.HPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;


/**
 * Find Files Main Window.
 */
public class MainWindow
	extends FxWindow
{
	public static final Duration PERIOD = Duration.millis(200);
	public final DetailPane detailPane;
	public final SplitPane split;
	protected final Clipboard clipboard;
	protected String oldContent;
	protected final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	
	
	public MainWindow()
	{
		super("MainWindow");
				
		
		detailPane = new DetailPane();
		
		split = new SplitPane(new Pane(), detailPane);
		split.setOrientation(Orientation.HORIZONTAL);
		split.setDividerPositions(0.8);
		
		setTitle("Find Files " + Version.VERSION);
		setTop(createMenu());
		setCenter(split);
		setBottom(createStatusBar());
		setSize(600, 700);

		clipboard = Clipboard.getSystemClipboard();
		
		Timeline timeline = new Timeline(new KeyFrame(PERIOD, (ev) ->
		{
			if(clipboard.hasString())
			{
				String s = clipboard.getString();
				if(CKit.notEquals(oldContent, s))
				{
					updateContent(s);
					oldContent = s;
				}
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		
		// preferences
		bind("HSPLIT", horizontalSplit);
		FX.listen(this::updateSplit, true, horizontalSplit);

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
//		m.add(new CCheckMenuItem("Detail Pane Below", horizontalSplit));
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
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}
	
	
	protected void updateContent(String s)
	{
		detailPane.clear();
	}

	
	protected void updateDetailPane()
	{
//		detailPane.setFile(rep);
	}
}