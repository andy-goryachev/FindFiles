// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.common.util.CList;
import java.util.List;


/**
 * Text Model With Highlights.
 */
public class TextModelWithHighlights
	extends StyledTextModel
{
	private final CList<Integer> highlightedLines;
	
	
	public TextModelWithHighlights(List<StyledTextModel.Line> lines, List<Integer> highlightedLines)
	{
		super(lines);
		
		this.highlightedLines = new CList(highlightedLines);
	}
	

	public List<Integer> getHighlights()
	{
		return highlightedLines;
	}
}
