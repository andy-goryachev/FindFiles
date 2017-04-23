// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.conf.Location;
import goryachev.findfiles.conf.Locations;
import goryachev.findfiles.search.FileEntry;
import goryachev.findfiles.search.Search;
import goryachev.fx.CAction;
import goryachev.fx.CButton;
import goryachev.fx.CComboBox;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxDateFormatter;
import goryachev.fx.FxDecimalFormatter;
import goryachev.fx.FxThread;
import goryachev.fx.icon.ProcessingIcon;
import goryachev.fx.table.FxTable;
import java.io.File;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


/**
 * Main Panel.
 */
public class MainPane
	extends CPane
{
	public final CAction searchAction = new CAction(this::search);
	public final CComboBox sourceField;
	public final TextField searchField;
	public final CButton searchButton;
	public final Label progressField;
	public final FxTable<FileEntry> table;
	public final DetailPane detailPane;
	public final SplitPane split;
	public final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	protected final SimpleBooleanProperty searching = new SimpleBooleanProperty(false);
	protected volatile Search search;
	protected ZQuery query;
	protected FxDecimalFormatter numberFormat = new FxDecimalFormatter("#,##0");
	protected FxDateFormatter dateFormat = new FxDateFormatter("yyyy/MM/dd HH:mm:ss");
	
	
	public MainPane()
	{
		sourceField = new CComboBox();
		sourceField.setMinWidth(120);
		sourceField.setPrefWidth(120);
		
		searchField = new TextField();
		searchField.addEventHandler(KeyEvent.KEY_PRESSED, (ev) -> handleSearchKeyPress(ev));
		
		searchButton = new CButton("Search", searchAction);
		
		progressField = new Label();
		
		CPane p = new CPane();
		p.setPadding(2, 10);
		p.setHGap(10);
		p.addColumns
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		p.add(0, 0, FX.label("Source:"));
		p.add(1, 0, sourceField);
		p.add(2, 0, progressField);
		p.add(3, 0, searchField);
		p.add(4, 0, searchButton);
		
		table = new FxTable<>();
		table.addColumn("File").setConverter((f) -> f.getName());
		table.addColumn("Path").setConverter((f) -> f.getFullPath());
		table.addColumn("Size").setConverter((f) -> numberFormat.format(f.length())).setAlignment(Pos.CENTER_RIGHT);
		table.addColumn("Last Modified").setRenderer((f) -> f == null ? null : FX.label(dateFormat.format(f.lastModified())));
		table.setResizePolicyConstrained();
		FX.listen(this::updateSelection, table.getSelectionModel().selectedItemProperty());
		
		detailPane = new DetailPane();
		
		split = new SplitPane(table, detailPane);
		split.setOrientation(Orientation.HORIZONTAL);
		split.setDividerPositions(0.8);
		
		setTop(p);
		setCenter(split);
		
		FX.listen(this::updateSplit, true, horizontalSplit);
		FX.listen(this::updateProgress, true, searching);
		
		FX.later(this::initLocations);
	}
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}
	
	
	protected void setSearching(boolean on)
	{
		searching.set(on);
	}
	
	
	protected void updateProgress()
	{
		boolean on = searching.get();
		progressField.setGraphic(on ? ProcessingIcon.create(25) : FX.spacer(25));
		table.setPlaceholder(on ? "Searching..." : "No content in table");
	}
	
	
	protected void handleSearchKeyPress(KeyEvent ev)
	{
		switch(ev.getCode())
		{
		case ENTER:
			search();
			ev.consume();
			break;
		}
	}
	
	
	protected void initLocations()
	{
		new FxThread("load locations")
		{
			private Locations loc;
			
			protected void process() throws Throwable
			{
				loc = loadLocations();
			}

			protected void processSuccess()
			{
				setLocations(loc);
			}
		}.start();
	}
	
	
	protected void setLocations(Locations loc)
	{
		sourceField.setItems(loc.locations);
		sourceField.selectFirst();
	}
	

	// TODO settings
	protected Locations loadLocations() throws Exception
	{
		String s = CKit.readStringQuiet(new File("./locations.json"));
		if(s == null)
		{
			s = CKit.readString(MainPane.class, "test.json");
		}
		return Locations.fromJson(s);
	}
	
	
	protected void search()
	{
		if(search != null)
		{
			search.cancel();
		}
		
		setSearching(true);
		table.clearItems();
		
		String expr = searchField.getText();
		query = new ZQuery(expr);
		
		Location loc = (Location)sourceField.getSelectionModel().getSelectedItem();
		search = new Search(this, loc, query);
		search.start();
	}


	public void finishedSearch(Search s, CList<FileEntry> found)
	{
		if(search == s)
		{
			FX.later(() ->
			{
				if(search == s)
				{
					search = null;
					setSearching(false);
					
					table.setItems(found);
					FX.later(() -> table.selectFirst());
				}
			});
		}
	}
	
	
	protected void updateSelection()
	{
		FileEntry f = table.getSelectedItem();
		detailPane.setFileEntry(f, query);
	}
}
