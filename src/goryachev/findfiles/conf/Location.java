// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.conf;
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
	private ObservableList<File> directories = FX.observableArrayList();
	
	
	public Location()
	{
	}
}
