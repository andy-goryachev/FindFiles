// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CList;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.edit.CTextFlow;
import goryachev.fx.edit.FxEditor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;


/**
 * TextFlow With Highlights.
 * 
 * Since we can't style Text background directly, this has to be done in TextFlow.
 */
public class TextFlowWithHighlights
	extends CTextFlow
{
	public static final CssStyle HIGHLIGHT = new CssStyle("TextFlowWithHighlights_HIGHLIGHT");

	private final int[] indices;
	private final Path highlights;
	
	
	public TextFlowWithHighlights(String text, int[] indices)
	{
		this.indices = indices;

		highlights = new Path();
		FX.style(highlights, FxEditor.SELECTION_HIGHLIGHT);
		highlights.setManaged(false);
		highlights.setStroke(null);
		highlights.setFill(Color.rgb(255, 255, 0));
		
		add(highlights);
		add(new Text(text));
	}
	
	
	protected void layoutChildren()
	{
		super.layoutChildren();
		updateHighlights();
	}
	
	
	protected void updateHighlights()
	{
		CList<PathElement> es = new CList();
		
		for(int i=0; i<indices.length; )
		{
			int beg = indices[i++];
			int end = indices[i++];
			
			PathElement[] p = getRange(beg, end);
			es.addAll(p);
		}
		
		highlights.getElements().setAll(es);
	}
}
