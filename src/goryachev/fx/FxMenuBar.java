// Copyright © 2016-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;


/**
 * Convenient MenuBar.
 */
public class FxMenuBar
	extends MenuBar
{
	public FxMenuBar()
	{
	}
	
	
	public void add(FxMenu m)
	{
		getMenus().add(m);
	}
	
	
	public FxMenu menu(String text)
	{
		FxMenu m = new FxMenu(text);
		getMenus().add(m);
		return m;
	}
	
	
	public FxMenu menu(String text, FxAction a)
	{
		FxMenu m = new FxMenu(text, a);
		getMenus().add(m);
		return m;
	}
	
	
	public void addFill()
	{
		// TODO
	}
	
	
	public void add(Node n)
	{
		Menu m = new Menu();
//		m.setDisable(true);
		m.setGraphic(n);
		getMenus().add(m);
	}
	
	
	public void separator()
	{
		lastMenu().separator();
	}
	
	
	public FxMenu lastMenu()
	{
		List<Menu> ms = getMenus();
		return (FxMenu)ms.get(ms.size() - 1);
	}
	
	
	public FxMenuItem item(String name)
	{
		FxMenuItem m = new FxMenuItem(name);
		m.setDisable(true);
		add(m);
		return m;
	}
	
	
	public FxMenuItem item(String name, FxAction a)
	{
		FxMenuItem m = new FxMenuItem(name, a);
		add(m);
		return m;
	}
	
	
	public void add(MenuItem m)
	{
		lastMenu().add(m);
	}
}
