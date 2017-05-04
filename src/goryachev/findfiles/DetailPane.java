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
import javafx.scene.control.SplitPane;


/**
 * Detail Pane.
 */
public class DetailPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("DetailPane_PANE");
	public static final CssStyle HIT_TABLE_RENDERER = new CssStyle("DetailPane_HIT_TABLE_RENDERER");

	public final FxTable<Integer> table;
	public final FxEditor textField;
	public final SplitPane split;
	protected NaiveFileReader reader;
	protected TextModelWithHighlights model;
	
	
	public DetailPane()
	{
		FX.style(this, PANE);

		table = new FxTable();
		table.setPlaceholder("");
		table.addColumn().setRenderer((ix) -> createPreviewRenderer(ix));
		table.setResizePolicyConstrained();
		table.hideHeader();
		table.selectedItemProperty().addListener((s) -> scrollToVisible());
		
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
		String text = model.getPlainText(ix).trim();
		
		// TODO with highlighted text
		return FX.label(HIT_TABLE_RENDERER, text); 
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
		
		if(f == null)
		{
			table.clearItems();
		}
		else
		{
			// TODO in bg thread
			
			reader = new NaiveFileReader(f.file, query);
			model = reader.readFile();
			textField.setTextModel(model);
			table.setItems(model.getHighlights());
			table.selectFirst();
			
			// TODO scroll textField to the first hit
		}
	}
	
	
	protected void scrollToVisible()
	{
		Integer ix = table.getSelectedItem();
		if(ix != null)
		{
			textField.scrollToVisible(ix);
		}
	}
}
