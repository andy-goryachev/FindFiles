// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.conf;
import goryachev.common.util.KeepAll;
import goryachev.fx.FX;
import javafx.collections.ObservableList;


/**
 * Top level configuration object.
 */
@KeepAll
public class Locations
{
	private ObservableList<Location> locations = FX.observableArrayList();
	
	
	public Locations()
	{
	}
}
