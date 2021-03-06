// Copyright © 2017-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.conf;
import goryachev.common.util.CKit;
import goryachev.common.util.KeepAll;
import goryachev.fx.FX;
import java.io.File;
import javafx.collections.ObservableList;


/**
 * Location.
 */
@KeepAll
public class Location
{
	public String name;
	public final ObservableList<File> directories = FX.observableArrayList();
	public String filterSpec;
	
	
	public Location()
	{
	}
	
	
	public String toString()
	{
		if(CKit.isBlank(name))
		{
			return directories.toString();			
		}
		return name;
	}
}
