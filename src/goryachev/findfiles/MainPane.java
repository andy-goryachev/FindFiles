// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.D;
import goryachev.findfiles.conf.Location;
import goryachev.findfiles.conf.Locations;
import goryachev.findfiles.search.FileEntry;
import goryachev.findfiles.search.Search;
import goryachev.fx.CAction;
import goryachev.fx.CButton;
import goryachev.fx.CComboBox;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxCtl;
import goryachev.fx.FxThread;
import goryachev.fx.table.FxTable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import research.fx.FxDateFormatter;
import research.fx.FxDecimalFormatter;


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
	public final FxTable<FileEntry> table;
	public final DetailPane detailPane;
	public final SplitPane split;
	public final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	protected volatile Search search;
	protected FxDecimalFormatter numberFormat = new FxDecimalFormatter("#,##0");
	protected FxDateFormatter dateFormat = new FxDateFormatter("yyyy/MM/dd HH:mm:ss");
	
	
	public MainPane()
	{
		sourceField = new CComboBox();
		sourceField.setMinWidth(120);
		
		searchField = new TextField();
		
		searchButton = new CButton("Search", searchAction);
		
		CPane p = new CPane();
		p.setPadding(2);
		p.setHGap(10);
		p.addColumns
		(
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		p.add(0, 0, FX.label("Find in:"));
		p.add(1, 0, sourceField);
		p.add(2, 0, searchField);
		p.add(3, 0, searchButton);
		
		// FIX column sorting is incorrect
		table = new FxTable<>();
		table.addColumn("File").setRenderer((f) -> f == null ? null : FX.label(f.getName()));
		table.addColumn("Path").setRenderer((f) -> f == null ? null : FX.label(f.getPath()));
		table.addColumn("Size").setRenderer((f) -> f == null ? null : FX.label(Pos.CENTER_RIGHT, FxCtl.FORCE_MAX_WIDTH, numberFormat.format(f.length())));
		table.addColumn("Last Modified").setRenderer((f) -> f == null ? null : FX.label(dateFormat.format(f.lastModified())));
		table.setResizePolicyConstrained();
		FX.listen(this::updateSelection, table.getSelectedItems());
		
		detailPane = new DetailPane();
		
		split = new SplitPane(table, detailPane);
		split.setOrientation(Orientation.HORIZONTAL);
		split.setDividerPositions(0.8);
		
		setTop(p);
		setCenter(split);
		
		FX.listen(this::updateSplit, true, horizontalSplit);
		
		FX.later(this::initLocations);
	}
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
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
		
		D.print(Locations.toJson(loc));
	}
	
	
	protected Locations loadLocations() throws Exception
	{
		String s = CKit.readString(MainPane.class, "test.json");
		return Locations.fromJson(s);
	}
	
	
	protected void search()
	{
		if(search != null)
		{
			search.cancel();
		}
		
		table.clearItems();
		
		String query = searchField.getText();
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
					// TODO clear searching boolean
					
					table.setItems(found);
				}
			});
		}
	}
	
	
	protected void updateSelection()
	{
		FileEntry f = table.getSelectedItem();
		detailPane.setFileEntry(f);
	}
}
