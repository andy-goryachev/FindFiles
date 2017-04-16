// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.search.FileEntry;
import goryachev.findfiles.search.NaiveFileReader;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.edit.FxEditor;


/**
 * Detail Pane.
 */
public class DetailPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("DetailPane_PANE");
	public static final CssStyle HIGHLIGHT = new CssStyle("HIGHLIGHT");

	public final StyledTextModel model;
	public final FxEditor textField;
	protected NaiveFileReader reader;
	
	
	public DetailPane()
	{
		FX.style(this, PANE);
		
		model = new StyledTextModel();
		
		textField = new FxEditor(model);
		textField.setDisplayCaret(true);
		textField.setWrapText(true);
		FX.style(textField, FX.insets(2.5, 4.5));
		
		setCenter(textField);
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
		model.clear();
	}


	public void setFileEntry(FileEntry f, ZQuery query)
	{
		closeReader();
		clear();
		
		if(f != null)
		{
			reader = new NaiveFileReader(f.file, query);
			reader.readFile((lines) -> model.setLines(lines));
		}
	}
}
