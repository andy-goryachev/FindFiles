// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.util.FH;
import goryachev.common.util.Hex;
import java.util.Arrays;


/**
 * Matching Result.
 */
public class MatchingResult
{
	private final int end;
	private final byte[] pattern;


	public MatchingResult(int end, byte[] pattern)
	{
		this.end = end;
		this.pattern = pattern;
	}
	

	public int hashCode()
	{
		int h = FH.hash(MatchingResult.class);
		h = FH.hash(h, end);
		return FH.hash(h, pattern);
	}


	public boolean equals(Object x)
	{
		if(this == x)
		{
			return true;
		}
		else if(x instanceof MatchingResult)
		{
			MatchingResult m = (MatchingResult)x;
			return 
				(end == m.end) &&
				Arrays.equals(pattern, m.pattern);
		}
		else
		{
			return false;
		}
	}

	
	public int getStartIndex()
	{
		return end - pattern.length;
	}
	
	
	public int getEndIndex()
	{
		return end;
	}


	public String toString()
	{
		return "MatchingResult[end=" + end + ", pattern=" + Hex.toHexString(pattern) + "]";
	}
}