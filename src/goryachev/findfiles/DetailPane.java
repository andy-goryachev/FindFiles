// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CList;
import goryachev.findfiles.StyledTextModel;
import goryachev.findfiles.search.FileEntry;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.edit.FxEditor;
import java.io.File;


/**
 * Detail Pane.
 */
public class DetailPane
	extends CPane
{
	public static final CssStyle PANE = new CssStyle("DetailPane_PANE");
	public final StyledTextModel model;
	public final FxEditor textField;
	
	
	public DetailPane()
	{
		FX.style(this, PANE);
		
		model = new StyledTextModel();
		
		textField = new FxEditor(model);
		textField.setDisplayCaret(false);
		textField.setWrapText(true);
		FX.style(textField, FX.insets(2.5, 4.5));
		
		setCenter(textField);
	}
	
	
	public void clear()
	{
		// TODO close file
		model.clear();
	}


	public void setFileEntry(FileEntry f)
	{
		// TODO close existing file
		
		if(f == null)
		{
			clear();
		}
		else
		{
			// TODO read file in a cancellable bg thread;
			// try to determine if a known binary file
			// create a bunch of lines
			// if binary is detected in a text file, default to binary
			CList<StyledTextModel.Line> lines = readFile(f.file);
			model.setLines(lines);
		}
	}


	private CList<StyledTextModel.Line> readFile(File f)
	{
		CList<StyledTextModel.Line> rv = new CList();
		
		return rv;
	}
}
