// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
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


	public void setFile(File f)
	{
		model.setFile(f);
	}


	public void clear()
	{
		model.setFile(null);
	}
}
