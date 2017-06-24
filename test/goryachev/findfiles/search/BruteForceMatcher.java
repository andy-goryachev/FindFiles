package goryachev.findfiles.search;
import goryachev.findfiles.search.MatchingResult;
import java.util.function.Consumer;


/**
 * This class implements a brute force algorithm for solving multiple exact string matching problem.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 2, 2016)
 * @author Wanja Gayk (byte[] instead of String)
 * @version 2.0 (Oct 10, 2016)
 */
public class BruteForceMatcher
{
	public final void match(final byte[] text, final Consumer<MatchingResult> target, final byte[] ... patterns)
	{
		match(text, 0, text.length, target, patterns);
	}
	
	
	public void match(final byte[] text, final int start, final int end, final Consumer<MatchingResult> target, byte[] ... patterns)
	{
		for(int textIndex = start; textIndex < end; ++textIndex)
		{
			for(int patternIndex = 0; patternIndex < patterns.length; ++patternIndex)
			{
				tryMatch(text, patterns[patternIndex], textIndex, patternIndex, target);
			}
		}
	}


	private void tryMatch(final byte[] text, final byte[] pattern, final int endIndex, final int patternIndex, final Consumer<MatchingResult> target)
	{
		final int patternLength = pattern.length;

		if(patternLength <= endIndex + 1)
		{
			int textCursor = endIndex;
			int patternCursor = patternLength - 1;

			while(patternCursor >= 0)
			{
				if(text[textCursor] != pattern[patternCursor])
				{
					return;
				}

				--textCursor;
				--patternCursor;
			}

			target.accept(new MatchingResult(endIndex + 1, pattern));
		}
	}
}
