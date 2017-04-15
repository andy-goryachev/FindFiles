// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.findfiles.search.FileEntry;
import goryachev.fx.CAction;
import goryachev.fx.CButton;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.table.FxTable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


/**
 * Main Panel.
 */
public class MainPane
	extends CPane
{
	public final CAction searchAction = new CAction(this::search);
	public final TextField searchField;
	public final CButton searchButton;
	public final FxTable<FileEntry> table;
	public final DetailPane detailPane;
	public final SplitPane split;
	protected final SimpleBooleanProperty horizontalSplit = new SimpleBooleanProperty(false);
	
	
	public MainPane()
	{
		searchField = new TextField();
		searchButton = new CButton("Search", searchAction);
		
		CPane p = new CPane();
		p.setPadding(2);
		p.setHGap(10);
		p.addColumns
		(
			CPane.FILL,
			CPane.PREF
		);
		p.add(0, 0, searchField);
		p.add(1, 0, searchButton);
		
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
	}
	
	
	protected void updateSplit()
	{
		boolean hor = horizontalSplit.get();
		split.setOrientation(hor ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}
	
	
	protected void search()
	{
		
	}
}
