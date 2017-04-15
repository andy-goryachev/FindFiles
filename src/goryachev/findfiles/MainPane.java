// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import goryachev.findfiles.conf.Locations;
import goryachev.findfiles.search.FileEntry;
import goryachev.fx.CAction;
import goryachev.fx.CButton;
import goryachev.fx.CComboBox;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxThread;
import goryachev.fx.table.FxTable;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;


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
	protected final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	
	
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
		
		table = new FxTable<>();
		table.addColumn("File");
		table.addColumn("Path");
		table.addColumn("Size");
		table.addColumn("Last Modified");
		table.setResizePolicyConstrained();
		
		detailPane = new DetailPane();
		
		split = new SplitPane(table, detailPane);
		split.setOrientation(Orientation.HORIZONTAL);
		split.setDividerPositions(0.8);
		
		setTop(p);
		setCenter(split);
		
		FX.listen(this::updateSplit, true, horizontalSplit);
		
		FX.later(this::loadLocations);
	}
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}
	
	
	protected void loadLocations()
	{
		new FxThread("load locations")
		{
			protected void process() throws Throwable
			{
				String s = CKit.readString(MainPane.class, "test.json");
				Locations loc = new GsonBuilder().registerTypeAdapter(ObservableList.class, new InstanceCreator()
				{
					public Object createInstance(Type type)
					{
						return FX.observableArrayList();
					}
				}).create().fromJson(s, Locations.class);
				D.describe(loc);
			}

			protected void processSuccess()
			{
			}
		}.start();
	}
	
	
	protected void search()
	{
		
	}
}
