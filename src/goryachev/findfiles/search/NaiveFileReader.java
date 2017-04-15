// Copyright © 2017 Andy Goryachev <andy@goryachev.com>
package goryachev.findfiles.search;
import goryachev.common.util.CList;
import goryachev.common.util.text.ZQuery;
import goryachev.findfiles.StyledTextModel;
import java.io.Closeable;
import java.io.File;
import java.util.function.Consumer;


/**
 * Naive File Reader for Search.
 */
public class NaiveFileReader
	implements Closeable
{
	private final File file;
	
	
	public NaiveFileReader(File f)
	{
		this.file = f;
	}
	
	
	public boolean contains(ZQuery q)
	{
		return true;
	}


	public void close()
	{
		// TODO
	}


	public void readFile(Consumer<CList<StyledTextModel.Line>> p)
	{
		// TODO read file in a cancellable bg thread;
		// try to determine if a known binary file
		// create a bunch of lines
		// if binary is detected in a text file, default to binary
//		CList<StyledTextModel.Line> lines = readFile(f.file);
	}
}
