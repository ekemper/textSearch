package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import	search.TextTokenizer;

public class TextSearcher {
	private TextTokenizer tokenizer;
//	private String wordRegex = "-?\\d+|\\b([a-zA-Z]+'[a-zA-Z]+)\\b|\\w+";
	private String wordRegex;
	private String fileContents;

	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;
		
		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}

//		init(w.toString());
		fileContents = w.toString();
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
//	protected void init(String fileContents) {
//	}
	
	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord, int contextWords) {
		String count = "" + contextWords;

		String chars = "[ ,\\.-]+";
		String leftContext = "(\\w+"+ chars +"){"+count+"}";
		String rightContext = "("+chars+"+\\w+){"+count+"}";
		System.out.println("---------------------------");

//		System.out.println("rightContext : " + rightContext);
//		System.out.println("rightContext : " + rightContext);

		wordRegex =
				leftContext +
				"\\b(" + queryWord + ")\\b" +
				rightContext;

		System.out.println("wordRegex : " + wordRegex);

		tokenizer = new TextTokenizer(fileContents, wordRegex);

		ArrayList<String> result = new ArrayList<>();

		String next;
		while(tokenizer.hasNext()){

			next = tokenizer.next();


			if(tokenizer.isWord(next)){
				System.out.println("match : " + next);
				result.add(next);
			}
		}

		System.out.println("result : " + result);

		return result.toArray(new String[result.size()]);
	}
}

// Any needed utility classes can just go in this file

