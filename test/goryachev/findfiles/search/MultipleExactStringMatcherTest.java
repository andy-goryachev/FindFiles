package goryachev.findfiles.search;
import goryachev.common.test.Before;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.Assert;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


public class MultipleExactStringMatcherTest
{
	private Set<MatchingResult> bruteForceSet;
	private Set<MatchingResult> ahoCorasickSet;
	
	
	public static void main(String[] args)
	{
		TF.run();
	}


	@Before
	public void setup()
	{
		bruteForceSet = new HashSet<>();
		ahoCorasickSet = new HashSet<>();
	}


	@Test
	public void testText()
	{
		printTestName();

		final byte[] text = "der whiskymixer mixt whisky im whiskymixer".getBytes();
		final byte[][] patterns =
		{
			"der".getBytes(), "whisky".getBytes(), "mixer".getBytes(), "whiskymixer".getBytes()
		};

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testOverlapping()
	{
		final byte[] text = "aaaaa".getBytes();
		final byte[][] patterns =
		{
			"a".getBytes(), "aa".getBytes(), "aaaa".getBytes()
		};

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testRepetitivePattern()
	{
		printTestName();

		final byte[] text = "abc abc abc abcd abc abcd".getBytes();
		final byte[][] patterns =
		{
			"abc".getBytes(), "abc ".getBytes(), "abcd".getBytes(), " abc abc".getBytes()
		};

		matchAndPrintResult(text, patterns);

		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(0 + patterns[0].length, patterns[0])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(4 + patterns[0].length, patterns[0])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(8 + patterns[0].length, patterns[0])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(12 + patterns[0].length, patterns[0])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(17 + patterns[0].length, patterns[0])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(21 + patterns[0].length, patterns[0])));

		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(0 + patterns[1].length, patterns[1])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(4 + patterns[1].length, patterns[1])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(8 + patterns[1].length, patterns[1])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(17 + patterns[1].length, patterns[1])));

		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(12 + patterns[2].length, patterns[2])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(21 + patterns[2].length, patterns[2])));

		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(3 + patterns[3].length, patterns[3])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(7 + patterns[3].length, patterns[3])));
		Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(16 + patterns[3].length, patterns[3])));
	}


	@Test
	public void testRepetitivePattern2()
	{
		printTestName();

		final byte[] text = " so long, so long\n [female chorus] # So long, so long\n so long and thanks for all the fish".getBytes();
		final byte[][] patterns =
		{
			" so ".getBytes(), //
			" so l".getBytes(), //
			" so lo".getBytes(), //
			" so long".getBytes(), //
			" so long,".getBytes(), //
			" so long, ".getBytes(), //
			" so long, s".getBytes(), //
			" so long, so".getBytes(), //
			" so long, so ".getBytes(), //
			" so long, so l".getBytes(), //
			" so long, so lo".getBytes(), //
			" so long, so lon".getBytes(), //
			" so long, so long".getBytes(), //
		};

		matchAndPrintResult(text, patterns);

		for(int i = 0; i < 4; ++i)
		{
			Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(0 + patterns[i].length, patterns[i])));
			Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(9 + patterns[i].length, patterns[i])));
			Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(45 + patterns[i].length, patterns[i])));
			Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(54 + patterns[i].length, patterns[i])));
		}
		for(int i = 5; i < patterns.length; ++i)
		{
			Assert.assertTrue(ahoCorasickSet.contains(new MatchingResult(0 + patterns[i].length, patterns[i])));
		}
	}


	@Test
	public void testRandom()
	{
		printTestName();

		final Random random = new Random(123);
		for(int t = 0; t < 200; ++t)
		{
			final byte[] text = getText(200, 'a', 'c', random).getBytes();
			final byte[][] patterns = new byte[10][];
			for(int p = 0; p < patterns.length; ++p)
			{
				final byte[] pattern = getText(5, 'a', 'c', random).getBytes();
				patterns[p] = pattern;
			}

			bruteForceSet.clear();
			ahoCorasickSet.clear();

			new BruteForceMatcher().match(text, bruteForceSet::add, patterns);
			new goryachev.findfiles.search.AhoCorasickMatcher(patterns).match(text, ahoCorasickSet::add);

			if(!bruteForceSet.equals(ahoCorasickSet))
			{
				System.out.println(new String(text));
				System.out.println(bruteForceSet);
				System.out.println(ahoCorasickSet);
			}
			
			Assert.assertEquals(bruteForceSet, ahoCorasickSet);
			if(bruteForceSet.isEmpty())
			{
				--t;
			}
		}
	}


	@Test
	public void testWholeByteRange()
	{
		printTestName();

		final byte[] text = new byte[0x100];
		for(int b = 0; b < text.length; ++b)
		{
			text[b] = toUnsignedByte(b);
		}
		final byte[][] patterns = new byte[0x100 / 4][];
		for(int p = 0; p < patterns.length; ++p)
		{
			final int start = p * 4;
			final int end = start + 4;
			final byte[] pattern = Arrays.copyOfRange(text, start, end);
			patterns[p] = pattern;
		}

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testBigPatternSize()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 21;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'c', random).getBytes();

		final byte[][] patterns = new byte[][]
		{
			text, //
			Arrays.copyOfRange(text, 0, text.length / 2), //
			Arrays.copyOfRange(text, text.length / 2, text.length), //
			Arrays.copyOfRange(text, 0, text.length / 8), //
		};
		for(final byte[] pattern: patterns)
		{
			System.out.println("pattern size: " + pattern.length);
		}

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testSmallPatternInBigText()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 28;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'c', random).getBytes();

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 0, 4), //
			Arrays.copyOfRange(text, 100, 116), //
			Arrays.copyOfRange(text, 1000, 1008), //
		};
		
		for(final byte[] pattern: patterns)
		{
			System.out.println("pattern size: " + pattern.length);
		}

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testHugeNumberOfSmallPatterns()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 19;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'c', random).getBytes();

		final int chunkSize = 16;
		final byte[][] patterns = new byte[text.length / chunkSize / 10][];
		System.out.println("preparing patterns, amount:" + patterns.length);
		for(int p = 0; p < patterns.length; ++p)
		{
			final int start = p * chunkSize;
			final int end = start + chunkSize;
			final byte[] pattern = Arrays.copyOfRange(text, start, end);
			patterns[p] = pattern;
		}

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testWorstCaseTextAndSmallSinglePattern()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 22;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'a', random).getBytes();

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 0, 10),
		};
		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testWorstCaseTextAndBigSinglePattern()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 20;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'a', random).getBytes();

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 0, 1000),
		};
		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testWorstCaseTextAndSomeSmallPatterns()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 18;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'a', random).getBytes();

		final byte[][] patterns = new byte[100][];
		System.out.println("preparing patterns, amount:" + patterns.length);
		for(int p = 0; p < patterns.length; ++p)
		{
			final byte[] pattern = Arrays.copyOfRange(text, 0, p + 1);
			patterns[p] = pattern;
		}
		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testWorstCaseTextAndSomeBigPatterns()
	{
		printTestName();

		final Random random = new Random(123);
		final int textSize = 1 << 14;
		System.out.println("preparing text, size " + textSize);
		final byte[] text = getText(textSize, 'a', 'a', random).getBytes();

		final byte[][] patterns = new byte[100][];
		System.out.println("preparing patterns, amount:" + patterns.length);
		for(int p = 0; p < patterns.length; ++p)
		{
			final byte[] pattern = Arrays.copyOfRange(text, 0, (p + 1) * 100);
			patterns[p] = pattern;
		}
		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testPerfDemoOrginal()
	{
		printTestName();

		final long seed = System.nanoTime();
		final Random random = new Random(seed);
		final byte[] text = getText(500_000, 'a', 'a', random).getBytes();

		System.out.println("Seed = " + seed);

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 1000, 1220), 
			Arrays.copyOfRange(text, 2000, 2225), 
			Arrays.copyOfRange(text, 2005, 2225), 
			Arrays.copyOfRange(text, 20000, 22025), 
			Arrays.copyOfRange(text, 22000, 22025), 
			Arrays.copyOfRange(text, 22060, 22100),
		};

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testPerfDemoOrginalAsIntended()
	{
		printTestName();

		final long seed = System.nanoTime();
		final Random random = new Random(seed);
		final byte[] text = getText(500_000, 'a', 'b', random).getBytes();

		System.out.println("Seed = " + seed);

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 1000, 1220), 
			Arrays.copyOfRange(text, 2000, 2225), 
			Arrays.copyOfRange(text, 2005, 2225), 
			Arrays.copyOfRange(text, 20000, 22025), 
			Arrays.copyOfRange(text, 22000, 22025), 
			Arrays.copyOfRange(text, 22060, 22100),
		};

		matchAndPrintResult(text, patterns);
	}


	@Test
	public void testPerfDemoOrginalWiderNumberOfcharacters()
	{
		printTestName();

		final long seed = System.nanoTime();
		final Random random = new Random(seed);
		final byte[] text = getText(500_000, 'a', 'z', random).getBytes();

		System.out.println("Seed = " + seed);

		final byte[][] patterns = new byte[][]
		{
			Arrays.copyOfRange(text, 1000, 1220), 
			Arrays.copyOfRange(text, 2000, 2225), 
			Arrays.copyOfRange(text, 2005, 2225), 
			Arrays.copyOfRange(text, 20000, 22025), 
			Arrays.copyOfRange(text, 22000, 22025), 
			Arrays.copyOfRange(text, 22060, 22100),
		};

		matchAndPrintResult(text, patterns);
	}


	private void matchAndPrintResult(final byte[] text, final byte[][] patterns)
	{
		System.out.println("brute force matching");
		final long startB = System.currentTimeMillis();
		new BruteForceMatcher().match(text, bruteForceSet::add, patterns);
		final long endB = System.currentTimeMillis();
		System.out.println(endB - startB + "ms");

		System.out.println("aho corasick matching");
		final long startC = System.currentTimeMillis();
		new goryachev.findfiles.search.AhoCorasickMatcher(patterns).match(text, ahoCorasickSet::add);
		final long endC = System.currentTimeMillis();
		System.out.println(endC - startC + "ms");

		if(!bruteForceSet.equals(ahoCorasickSet))
		{
			System.out.println(new String(text));

			final Comparator<MatchingResult> matchingResultComparator = Comparator.<MatchingResult>comparingInt(m -> m.getStartIndex()).thenComparingInt(m -> m.getEndIndex());
			final Set<MatchingResult> bruteForceSetSorted = new TreeSet<>(matchingResultComparator);
			final Set<MatchingResult> ahoCorasickSetSorted = new TreeSet<>(matchingResultComparator);
			bruteForceSetSorted.addAll(bruteForceSet);
			ahoCorasickSetSorted.addAll(ahoCorasickSet);

			System.out.println(bruteForceSetSorted);
			System.out.println(ahoCorasickSetSorted);
		}
		
		Assert.assertEquals(bruteForceSet, ahoCorasickSet);
	}


	void printTestName()
	{
		System.out.println("----");
		System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
	}


	private static byte toUnsignedByte(final int value)
	{
		return (byte)(0xFF & value);
	}
	
	
	private static String getText(final int size, final char a, final char b, final Random random)
	{
		final StringBuilder sb = new StringBuilder(size);
		for(int i = 0; i < size; ++i)
		{
			sb.append(randomCharacter(a, b, random));
		}
		return sb.toString();
	}


	private static char randomCharacter(final char a, final char b, final Random random)
	{
		return (char)(a + (random.nextInt((b + 1) - a)));
	}
}