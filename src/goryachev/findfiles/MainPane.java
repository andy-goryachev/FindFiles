// Copyright © 2017-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.conf.Location;
import goryachev.findfiles.conf.Locations;
import goryachev.findfiles.search.FileEntry;
import goryachev.findfiles.search.SearchThread;
import goryachev.fx.CComboBox;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxButton;
import goryachev.fx.FxDateFormatter;
import goryachev.fx.FxDecimalFormatter;
import goryachev.fx.FxThread;
import goryachev.fx.icon.ProcessingIcon;
import goryachev.fx.table.FxTable;
import java.io.File;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


/**
 * Main Pane.
 */
public class MainPane
	extends CPane
{
	public final FxAction searchAction = new FxAction(this::startSearch);
	public final CComboBox sourceField;
	public final TextField searchField;
	public final FxButton searchButton;
	public final Label progressField;
	public final FxTable<FileEntry> table;
	public final DetailPane detailPane;
	public final SplitPane split;
	public final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	protected final SimpleStringProperty statusProperty;
	protected final SimpleObjectProperty<SearchThread> searchProperty = new SimpleObjectProperty();
	protected ZQuery query;
	protected FxDecimalFormatter numberFormat = new FxDecimalFormatter("#,##0");
	protected FxDateFormatter dateFormat = new FxDateFormatter("yyyy/MM/dd HH:mm:ss");
	
	
	public MainPane(SimpleStringProperty statusProperty)
	{
		this.statusProperty = statusProperty;
		
		sourceField = new CComboBox();
		sourceField.setMinWidth(150);
		sourceField.setPrefWidth(150);
		
		searchField = new TextField();
		searchField.addEventHandler(KeyEvent.KEY_PRESSED, (ev) -> handleSearchKeyPress(ev));
		
		searchButton = new FxButton("Search", searchAction);
		
		progressField = new Label();
		
		CPane p = new CPane();
		p.setPadding(2, 10);
		p.setHGap(5);
		p.addColumns
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF,
			CPane.PREF
		);
		p.add(0, 0, FX.label("Source:"));
		p.add(1, 0, sourceField);
		p.add(2, 0, FX.label("Find:"));
		p.add(3, 0, searchField);
		p.add(4, 0, searchButton);
		p.add(5, 0, progressField);
		
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
		FX.listen(this::updateProgress, true, searchProperty);
		
		FX.later(this::initLocations);
	}
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}
	
	
	protected SearchThread getSearch()
	{
		return searchProperty.get();
	}
	
	
	protected void setSearch(SearchThread s)
	{
		searchProperty.set(s);
	}
	
	
	protected void updateProgress()
	{
		SearchThread search = getSearch();
		String text = getStatusText(search);
		boolean on = (search != null);
		
		progressField.setGraphic(on ? ProcessingIcon.create(25) : FX.spacer(25));
		table.setPlaceholder(on ? text : "No content in table");
		
		statusProperty.set(text);
	}
	
	
	protected String getStatusText(SearchThread search)
	{
		if(search == null)
		{
			int sz = table.getItems().size();
			return String.format("%s file(s) found.", numberFormat.format(sz));
		}
		else
		{
			return "Searching [" + search.getExpression() + "]...";
		}
	}


	protected void handleSearchKeyPress(KeyEvent ev)
	{
		switch(ev.getCode())
		{
		case ENTER:
			startSearch();
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
	
	
	protected void startSearch()
	{
		String expr = searchField.getText();
		if(CKit.isBlank(expr))
		{
			return;
		}
		
		SearchThread search = getSearch();
		if(search != null)
		{
			search.cancel();
		}
		
		table.clearItems();
		detailPane.clear();
		
		query = new ZQuery(expr);
		
		Location loc = (Location)sourceField.getSelectionModel().getSelectedItem();
		search = new SearchThread(this, loc, query);
		search.start();
		setSearch(search);
	}


	public void finishedSearch(SearchThread s, CList<FileEntry> found)
	{
		FX.later(() ->
		{
			SearchThread search = getSearch();
			if(search == s)
			{
				table.setItems(found);
				FX.later(() -> table.selectFirst());
				
				setSearch(null);
			}
		});
	}
	
	
	protected void updateSelection()
	{
		FileEntry f = table.getSelectedItem();
		detailPane.setFileEntry(f, query);
	}
}
