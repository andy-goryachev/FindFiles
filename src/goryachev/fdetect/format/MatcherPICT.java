// Copyright © 2012-2018 Andy Goryachev <andy@goryachev.com>
package goryachev.fdetect.format;
import goryachev.fdetect.FFMatcher;
import goryachev.fdetect.FileType;


/**
 * PICT is a graphics file format introduced on the original Apple Macintosh computer.
 * 
 * http://en.wikipedia.org/wiki/PICT
 * http://www.fileformat.info/format/macpict/egff.htm
 */
public class MatcherPICT
	extends FFMatcher
{
	private byte[] pict10 = bytes("1101");
	private byte[] pict20 = bytes("001102ff 0c00");
	
	
	public FileType match(String filename, byte[] b)
	{
		if(match(b, pict20, 522))
		{
			return FileType.PICT;
		}
		else if(match(b, pict10, 522))
		{
			return FileType.PICT;
		}
		return null;
	}
}
