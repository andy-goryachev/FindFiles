// Copyright © 2016-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.fx.CommonStyles;
import goryachev.fx.FxStyleSheet;
import goryachev.fx.Theme;
import javafx.scene.control.OverrunStyle;


/**
 * Application style sheet.
 */
public class Styles
	extends FxStyleSheet
{
	public Styles()
	{
		Theme theme = Theme.current();
		
		add
		(
			new CommonStyles(),
			
			new Selector(DetailPane.PANE).defines
			(
				fontFamily("monospace"),
				
				new Selector(DetailPane.HIT_TABLE_RENDERER).defines
				(
					fontFamily("dialog"),
					padding(0),
					textOverrun(OverrunStyle.CLIP)
				)
			)
		);
	}
}
