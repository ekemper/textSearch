package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;
import	search.TextTokenizer;

public class TextSearcher {

	private static TextTokenizer tokenizer;
	private String wordRegex = "-?\\d+|\\b([a-zA-Z]+'[a-zA-Z]+)\\b|\\w+";

	private int contextWordCount;
	private String wordAtIteratorHead = "";
	private String currentWord;
	private int iteratorIndex = 0;
	private int contextSize;

	/**
	 * The current context is an array of length 2 * contextWordCount + 1
	 * where the end of the array is the iterator head.
	 */
	private ArrayList<String> currentContext;
	private ArrayList<String> result;
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

		//init(w.toString());
		fileContents = w.toString();
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	protected void init(String fileContents) {
		result = new ArrayList<>();
		currentContext = new ArrayList<>();
		tokenizer = new TextTokenizer(fileContents, wordRegex);
	}
	
	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
		init(fileContents); // this is prob an antipattern :)
		contextWordCount = contextWords; // perhaps there is a cleaner way to set this on the class?
		contextSize = (2*contextWordCount) + 1;

		while (tokenizer.hasNext())	{
			wordAtIteratorHead = tokenizer.next();
			updateCurrentContext();

			boolean hasCurrentWord =
					currentWord != null &&
					!currentWord.isEmpty() &&
					tokenizer.isWord(currentWord);

			// it's kinda brittle but this next bit avoids putting duplicate elements in the results array.
			// when the query word is matched in the current context, the next time we call tokenizer.next()
			// the value of 'wordAtIteratorHead' will likely be a space, comma or period but the current word
			// will still match with the same context words.
			// TODO find a cleaner way to avoid duplication of the entries
 			boolean iteratorHeadIsWordMatch = tokenizer.isWord(wordAtIteratorHead);

 			boolean foundMatch =
					hasCurrentWord &&
					iteratorHeadIsWordMatch &&
					currentWord.toLowerCase().equals(queryWord.toLowerCase());

			if(foundMatch){
				addContextStringToResult(true);
			}

			iteratorIndex++;
		}

		handleMatchesNearEnd(queryWord);
		return result.toArray(new String[result.size()]);
	}

	public void addContextStringToResult(boolean trimEnd){
		while(!tokenizer.isWord(currentContext.get(0))){
			currentContext.remove(0);
		}

		if(trimEnd){
			while(!tokenizer.isWord(currentContext.get(currentContext.size()-1))){
				currentContext.remove(currentContext.size()-1);
			}
		}

		result.add(String.join("", currentContext));
	}

	public void handleMatchesNearEnd(String queryWord){
		// the current context array will contain the match if it exists
		if(currentContextWords().indexOf(queryWord) == -1) return;

		while(currentContextWords().indexOf(queryWord) > contextWordCount) {
			// while the index of the matched word element in the context array is strictly
			// greater than 'contextWordCounts', remove elements from the
			// beginning of the context array...
			currentContext.remove(0);
		}

		addContextStringToResult(false);
	}

	public void updateCurrentContext(){
		currentContext.add(wordAtIteratorHead);

		if(currentContextWords().size() < contextWordCount + 1) {
			// just append next word to context and continue
			return;
		}

		setCurrentWord();

		//remove the word at the beginning of the array so that the context has the correct number of words.
		while(currentContextWords().size() > contextSize){
			currentContext.remove(0);
		}
	}

	public void setCurrentWord(){
		ArrayList<String> currentWords = currentContextWords();
		int currentWordIndex = currentWords.size() - contextWordCount - 1;
		currentWord = currentWords.get(currentWordIndex);
	}

	public ArrayList<String> currentContextWords(){
		ArrayList<String> justWords = new ArrayList<>();

		for (int i = 0; i < currentContext.size(); i++) {
			String element = currentContext.get(i);
			if(tokenizer.isWord(element)){
				justWords.add(element);
			}
		}

		return justWords;
	}
}

// Any needed utility classes can just go in this file

