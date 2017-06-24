package goryachev.findfiles.search;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


/**
 * This class implements Aho-Corasick algorithm for multiple exact string matching problem.
 * https://codereview.stackexchange.com/questions/115624/aho-corasick-for-multiple-exact-string-matching-in-java
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 2, 2016)
 * @author Wanja Gayk (byte[] instead of String + micro-optimizations)
 * @version 2.0 (Oct 10, 2016)
 * @author <andy@goryachev.com>
 * @version 2017-06-12
 */
public class AhoCorasickMatcher
{
	protected final byte[][] searchPatterns;
	protected final TrieNode root;
	protected final Map<TrieNode,TrieNode> fail = new HashMap<>();
	protected final Map<TrieNode,int[]> patterns = new HashMap<>();

	
	public AhoCorasickMatcher(byte[] ... searchPatterns)
	{
		this.searchPatterns = searchPatterns;
		this.root = new TrieNode();
		this.patterns.put(root, new int[0]);
		
		constructTrie();
		computeFailureFunction();
	}
	

	private void constructTrie()
	{
		int sz = searchPatterns.length;

		for(int ip=0; ip<sz; ++ip)
		{
			TrieNode node = root;
			int charIndex = 0;
			int patternLength = searchPatterns[ip].length;

			while((charIndex < patternLength) && node.getChild(searchPatterns[ip][charIndex]) != null)
			{
				node = node.getChild(searchPatterns[ip][charIndex]);
				++charIndex;
			}

			while(charIndex < patternLength)
			{
				TrieNode nd = new TrieNode();
				node.setChild(searchPatterns[ip][charIndex], nd);
				node = nd;
				++charIndex;
			}

			patterns.put(node, new int[] { ip });
		}
	}


	private void computeFailureFunction()
	{
		TrieNode fallbackNode = new TrieNode();

		for(int c=0; c<0x100; c++)
		{
			byte b = toUnsignedByte(c);
			fallbackNode.setChild(b, root);
		}

		fail.put(root, fallbackNode);
		Deque<TrieNode> queue = new ArrayDeque<>();
		queue.addLast(root);

		while(!queue.isEmpty())
		{
			TrieNode head = queue.removeFirst();

			for(int c=0; c<0x100; c++)
			{
				byte character = toUnsignedByte(c);

				if(head.getChild(character) != null)
				{
					TrieNode child = head.getChild(character);
					TrieNode w = fail.get(head);

					while(w.getChild(character) == null)
					{
						w = fail.get(w);
					}

					fail.put(child, w.getChild(character));

					int[] failTargets = patterns.get(fail.get(child));
					int[] existingList = patterns.get(child);
					if(existingList == null)
					{
						patterns.put(child, failTargets);
					}
					else
					{
						int[] extendedList = Arrays.copyOf(existingList, existingList.length + failTargets.length);
						System.arraycopy(failTargets, 0, extendedList, existingList.length, failTargets.length);
						patterns.put(child, extendedList);
					}
					queue.addLast(child);
				}
			}
		}
	}
	
	
	private static byte toUnsignedByte(int value)
	{
		return (byte)(0xFF & value);
	}
	
	
	public void match(byte[] text, Consumer<MatchingResult> target)
	{
		match(text, 0, text.length, target);
	}

	
	public void match(byte[] text, int start, int end, Consumer<MatchingResult> target)
	{
		TrieNode state = root;
		
		for(int i=start; i<end; i++)
		{
			while(state.getChild(text[i]) == null)
			{
				state = fail.get(state);
			}

			state = state.getChild(text[i]);
			
			for(int ip: patterns.get(state))
			{
				target.accept(new MatchingResult(i + 1, searchPatterns[ip]));
			}
		}
	}

	
	//
	

	protected static class TrieNode
	{
		private final TrieNode[] children = new TrieNode[0x100];


		public void setChild(byte character, TrieNode child)
		{
			children[Byte.toUnsignedInt(character)] = child;
		}


		public TrieNode getChild(byte character)
		{
			return children[Byte.toUnsignedInt(character)];
		}
	}
}