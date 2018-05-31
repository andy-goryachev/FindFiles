// Copyright © 2017-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import goryachev.common.util.CancellableThread;
import goryachev.common.util.CancelledException;
import goryachev.common.util.FileTools;
import goryachev.common.util.Log;
import goryachev.common.util.RFileFilter;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.MainPane;
import goryachev.findfiles.conf.Location;
import java.io.File;


/**
 * Search Thread.
 */
public class SearchThread
	extends CancellableThread
{
	protected final MainPane parent;
	protected final Location location;
	protected final ZQuery query;
	protected final String filterSpec;
	protected final CList<FileEntry> found = new CList();
	protected RFileFilter filter;
	
	
	public SearchThread(MainPane p, Location loc, ZQuery query)
	{
		this.parent = p;
		this.location = loc;
		this.query = query;
		this.filterSpec = loc.filterSpec;
		
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}
	
	
	public String getExpression()
	{
		return query.getExpression();
	}
	
	
	public void run()
	{
		try
		{
			filter = RFileFilter.parse(location.filterSpec);

			CList<File> dirs = new CList(location.directories);
			for(File f: dirs)
			{
				search(f, f);
			}
		}
		catch(CancelledException e)
		{
		}
		catch(InterruptedException e)
		{
		}
		catch(Exception e)
		{
			Log.ex(e);
		}
		
		parent.finishedSearch(this, found);
	}


	protected void search(File root, File f)
	{
		CKit.checkCancelled();
		
		String path = CKit.pathToRoot(root, f);
		boolean dir = f.isDirectory();
		boolean hidden = FileTools.isHidden(f);
		
		if(filter.accept(path, f.getName(), dir, hidden))
		{
			if(dir)
			{
				File[] fs = f.listFiles();
				if(fs != null)
				{
					CSorter.collate(fs);
					
					for(File ch: fs)
					{
						search(root, ch);
					}
				}
			}
			else
			{
				if(searchFile(f))
				{
					found.add(new FileEntry(f, path));
				}
			}
		}
	}


	protected boolean searchFile(File f)
	{
		NaiveFileReader rd = new NaiveFileReader(f, query);
		try
		{
			return rd.contains();
		}
		finally
		{
			CKit.close(rd);
		}
	}
}
