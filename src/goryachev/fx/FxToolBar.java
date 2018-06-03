// Copyright © 2018 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import javafx.scene.control.ToolBar;


/**
 * Fx ToolBar.
 */
public class FxToolBar
	extends ToolBar
{
	public FxToolBar()
	{
	}
	
	
	public FxButton button(String text, FxAction a)
	{
		FxButton b = new FxButton(text, a);
		getChildren().add(b);
		return b;
	}
	
	
	public void fill()
	{
		// TODO
	}
}
