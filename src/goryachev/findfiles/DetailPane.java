// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.search.FileEntry;
import goryachev.findfiles.search.NaiveFileReader;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.edit.FxEditor;
import goryachev.fx.table.FxTable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;


/**
 * Detail Pane.
 */
public class DetailPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("DetailPane_PANE");

	public final FxTable<Integer> table;
	public final FxEditor textField;
	public final SplitPane split;
	protected NaiveFileReader reader;
	protected TextModelWithHighlights model;
	
	
	public DetailPane()
	{
		FX.style(this, PANE);

		table = new FxTable();
		table.addColumn().setRenderer((ix) -> createPreviewRenderer(ix));
		table.setResizePolicyConstrained();
		table.hideHeader();
		
		textField = new FxEditor();
		textField.setDisplayCaret(true);
		textField.setWrapText(true);
		textField.setContentPadding(FX.insets(2.5, 4.5));
		
		split = new SplitPane(table, textField);
		split.setDividerPositions(0.1);
		setCenter(split);
	}
	
	
	protected Node createPreviewRenderer(Integer ix)
	{
		Region r = model.getDecoratedLine(ix);
		r.setMaxHeight(100);
		r.setMaxWidth(Double.MAX_VALUE);
		
		Label t = new Label();
		t.setWrapText(false);
		t.setGraphic(r);
		return t;
	}
	
	
	public void closeReader()
	{
		if(reader != null)
		{
			reader.close();
			reader = null;
		}
	}
	
	
	public void clear()
	{
		closeReader();
		
		if(model != null)
		{
			model.clear();
		}
	}


	public void setFileEntry(FileEntry f, ZQuery query)
	{
		closeReader();
		clear();
		
		if(f != null)
		{
			// TODO in bg thread
			
			reader = new NaiveFileReader(f.file, query);
			model = reader.readFile();
			textField.setTextModel(model);
			table.setItems(model.getHighlights());
		}
	}
}
