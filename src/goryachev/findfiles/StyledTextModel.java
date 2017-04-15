// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CList;
import goryachev.fx.edit.CTextFlow;
import goryachev.fx.edit.FxEditorModel;
import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;


/**
 * Styled Text FxEditor Model.
 */
public class StyledTextModel
	extends FxEditorModel
{
	private final CList<Line> lines = new CList();
	private static final Insets HEADING_INSETS = new Insets(0, 0, 0, 0);
	private static final Insets LINE_INSETS = new Insets(0, 0, 0, 20);
	
	
	public StyledTextModel()
	{
	}
	
	
	public void setFile(File f)
	{
		if(f == null)
		{
			lines.clear();
		}
		else
		{
//			CList<String> names = rep.getSectionNames();
//			CSorter.sort(names);
//			
//			CList<Line> rv = new CList<>();
//			for(String k: names)
//			{
//				Object x = rep.getSection(k);
//				if(x != null)
//				{
//					rv.add(new Line(true, k));
//					
//					if(x instanceof String)
//					{
//						rv.add(new Line(false, (String)x));
//					}
//					else if(x instanceof String[])
//					{
//						for(String s: (String[])x)
//						{
//							rv.add(new Line(false, s));
//						}
//					}
//				}
//			}
//			
//			lines.setAll(rv);
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
		return getLine(ix).text;
	}


	public Region getDecoratedLine(int ix)
	{
		Line line = getLine(ix);
		
		Text t = new Text(line.text);
//		t.setFill(ColorScheme.getDetailColor(line.heading));
		
		CTextFlow flow = new CTextFlow();
		flow.add(t);
		flow.setPadding(line.heading ? HEADING_INSETS : LINE_INSETS);
		return flow;
	}
	
	
	//
	
	
	protected static class Line
	{
		public final boolean heading;
		public final String text;
		
		
		public Line(boolean heading, String text)
		{
			this.heading = heading;
			this.text = text;
		}
	}
}
