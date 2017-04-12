// Copyright © 2016-2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles;
import goryachev.fx.CommonStyles;
import goryachev.fx.FxStyleSheet;
import javafx.scene.paint.Color;


/**
 * ReqTraq style sheet.
 */
public class Styles
	extends FxStyleSheet
{
	public Styles()
	{
		// TODO themes
		
		add
		(
			// basic styles
			new Selector(".root").defines
			(
				prop("-fx-accent", Color.RED),
				prop("-fx-focus-color", Color.RED),
				prop("-fx-faint-focus-color", Color.BLACK)
			),
			
			new Selector(".text").defines
			(
				prop("-fx-font-smoothing-type", "gray")
			),
			
			new Selector(".scroll-pane").defines
			(
//				new Selector(FOCUSED).defines
//				(
//					// removes focused border from scroll pane
//					// TODO do it specifically for the content pane
//					backgroundInsets(1)
//				),
				new Selector(" > .viewport").defines
				(
					backgroundColor(Color.WHITE)
				)
			),
			
			new Selector(DetailPane.PANE).defines
			(
				fontFamily("monospace")
			),
			
			// common fx styles
			new CommonStyles()
		);
	}
}
