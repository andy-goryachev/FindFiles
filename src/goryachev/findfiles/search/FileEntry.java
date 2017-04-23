// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.util.CKit;
import java.io.File;


/**
 * File Entry.
 */
public class FileEntry
	implements Comparable<FileEntry>
{
	public final File file;
	public final String path;
	
	
	public FileEntry(File f, String path)
	{
		this.file = f;
		this.path = path;
	}
	
	
	public String getName()
	{
		return file.getName();
	}
	
	
	public String getPath()
	{
		return path;
	}
	
	
	public String getFullPath()
	{
		return file.getParent();
	}
	
	
	public long length()
	{
		return file.length();
	}
	
	
	public long lastModified()
	{
		return file.lastModified();
	}


	public int compareTo(FileEntry f)
	{
		int d = file.getAbsolutePath().compareTo(f.file.getAbsolutePath());
		if(d == 0)
		{
			d = CKit.compare(f.lastModified(), lastModified());
		}
		return d;
	}
}
