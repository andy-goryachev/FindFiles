// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CList;
import goryachev.fx.edit.FxEditorModel;
import java.util.Collection;
import javafx.scene.layout.Region;


/**
 * Styled Text FxEditor Model.
 */
public class StyledTextModel
	extends FxEditorModel
{
	public static interface Line
	{
		public String getPlainText();
		
		public Region getDecoratedLine();
	}
	
	//
	
	private final CList<Line> lines = new CList();
	
	
	public StyledTextModel()
	{
	}
	
	
	public void clear()
	{
		lines.clear();
		fireAllChanged();
	}
	
	
	public void setLines(Collection<StyledTextModel.Line> newLines)
	{
		if(newLines == null)
		{
			lines.clear();
		}
		else
		{
			lines.setAll(newLines);
		}
		
		fireAllChanged();
	}


	public LoadInfo getLoadInfo()
	{
		return null;
	}


	public int getLineCount()
	{
		return lines.size();
	}
	
	
	protected Line getLine(int ix)
	{
		return lines.get(ix);
	}


	public String getPlainText(int ix)
	{
		return getLine(ix).getPlainText();
	}


	public Region getDecoratedLine(int ix)
	{
		return getLine(ix).getDecoratedLine();
	}
}
