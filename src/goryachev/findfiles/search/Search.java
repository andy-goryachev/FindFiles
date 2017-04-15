// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import goryachev.common.util.CancellableThread;
import goryachev.common.util.FileTools;
import goryachev.common.util.Log;
import goryachev.common.util.RFileFilter;
import goryachev.findfiles.MainPane;
import goryachev.findfiles.conf.Location;
import java.io.File;


/**
 * Search.
 */
public class Search
	extends CancellableThread
{
	protected final MainPane parent;
	protected final Location location;
	protected final String query;
	protected final String filterSpec;
	protected final CList<FileEntry> found = new CList();
	protected RFileFilter filter;
	
	
	public Search(MainPane p, Location loc, String query)
	{
		this.parent = p;
		this.location = loc;
		this.query = query;
		// TODO filters
		this.filterSpec = loc.filterSpec;
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
					CSorter.sort(fs);
					
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
		// TODO search content
		
		return true;
	}
}
