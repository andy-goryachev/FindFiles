// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.io.CReader;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CancelledException;
import goryachev.common.util.Log;
import goryachev.common.util.text.QuerySegment;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.DetailPane;
import goryachev.findfiles.StyledTextModel;
import goryachev.fx.FX;
import java.io.Closeable;
import java.io.File;
import java.util.function.Consumer;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Naive File Reader for Search.
 * 
 * This is a fast and dirty reader, proof of concept.  Will optimize later.  
 * Does not search byte arrays.
 * Does not use NIO or memory mapped files to speed up the search.
 * Does not work with binary files (well, sort of).
 * Does not have a check on long strings.
 */
public class NaiveFileReader
	implements Closeable
{
	private final File file;
	private final ZQuery query;
	
	
	public NaiveFileReader(File f, ZQuery q)
	{
		this.file = f;
		this.query = q;
	}
	
	
	public boolean contains()
	{
		try
		{
			boolean rv = false;
			CReader rd = new CReader(file);
			try
			{
				String s;
				while((s = rd.readLine()) != null)
				{
					CKit.checkCancelled();
					
					if(query.isExcluded(s))
					{
						return false;
					}
					
					if(query.isIncluded(s))
					{
						rv = true;
					}
				}
			}
			finally
			{
				CKit.close(rd);
			}
			return rv;
		}
		catch(CancelledException e)
		{
			return false;
		}
		catch(Exception e)
		{
			Log.ex(e);
			return false;
		}
	}


	public void close()
	{
		// TODO
	}


	// FIX this is done in the fx thread
	public void readFile(Consumer<CList<StyledTextModel.Line>> handler)
	{
		// TODO read file in a cancellable bg thread;
		// try to determine if a known binary file
		// create a bunch of lines
		// if binary is detected in a text file, default to binary
		CList<StyledTextModel.Line> lines = new CList();
		
		try
		{
			CReader rd = new CReader(file);
			try
			{
				String s;
				while((s = rd.readLine()) != null)
				{
					CKit.checkCancelled();
					
					StyledTextModel.Line line;
					if(query.isIncluded(s))
					{
						line = createHighlightedLine(s);
					}
					else
					{
						line = createLine(s);
					}
					lines.add(line);
				}
			}
			finally
			{
				CKit.close(rd);
			}
		}
		catch(Exception e)
		{
			Log.ex(e);
		}

		handler.accept(lines);
	}


	private StyledTextModel.Line createLine(String text)
	{
		return new StyledTextModel.Line()
		{
			public String getPlainText()
			{
				return text;
			}

			public Region getDecoratedLine()
			{
				Text t = new Text(text);
				TextFlow f = new TextFlow(t);
				return f;
			}
		};
	}


	private StyledTextModel.Line createHighlightedLine(String text)
	{
		ZQuery q = query;
		
		return new StyledTextModel.Line()
		{
			public String getPlainText()
			{
				return text;
			}

			public Region getDecoratedLine()
			{
				TextFlow f = new TextFlow();
				int start = 0;
				int len = text.length();
				
				int ct = q.includedSegmentCount();
				for(int i=0; i<ct; i++)
				{
					if(start >= len)
					{
						break;
					}
					
					QuerySegment seg = q.getIncludeSegment(i);
					for(;;)
					{
						int ix = seg.indexOf(text, start);
						if(ix < 0)
						{
							break;
						}
						else
						{
							if(ix > start)
							{
								String s = text.substring(start, ix);
								Text t = new Text(s);
								f.getChildren().add(t);
								start = ix;
							}
							
							String s = text.substring(start, start + seg.length());
							start = ix + seg.length();
							Text t = new Text(s);
							// FIX need to add a highlight to text flow
							// because a) Text has no background property
							// and b) Text might be on several lines
							t.setFill(Color.MAGENTA);
							FX.style(t, DetailPane.HIGHLIGHT);
							f.getChildren().add(t);
						}
					}
				}
				
				if(start < len)
				{
					f.getChildren().add(new Text(text.substring(start)));
				}
				
				return f;
			}
		};
	}
}
