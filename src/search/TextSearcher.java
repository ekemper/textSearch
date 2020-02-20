package search;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.sun.tools.corba.se.idl.StringGen;
import	search.TextTokenizer;

import javax.swing.*;

public class TextSearcher {
	private static TextTokenizer tokenizer;
	private String wordRegex = "-?\\d+|\\b([a-zA-Z]+'[a-zA-Z]+)\\b|\\w+";
	HashMap<String, HashValue> map = new HashMap<>();
	int contextWordsCount;
	String queryWord;
	String text;

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
		
		init(w.toString());
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	protected void init(String fileContents) {
		text = fileContents;
		tokenizer = new TextTokenizer(text, wordRegex);
		HashMapGenerator hashGenerator = new HashMapGenerator(tokenizer);
		map = hashGenerator.map;
//		System.out.println("map: "+ map);
	}

	/**
	 * 
	 * @param word The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String word,int contextWords) {
		contextWordsCount = contextWords; /// TODO this could be cleaner
		queryWord = word;
		System.out.println("queryWord: "+ queryWord);
		System.out.println("contextWords: "+ contextWords);


		HashValue matches = map.get(queryWord);
		if(matches != null){
			ArrayList<String> result = handleMatches(matches);
			return result.toArray(new String[result.size()]);
		}else {
			return new String[0];
		}
	}

	private ArrayList<String> handleMatches(HashValue matches){
		ArrayList<String> result = new ArrayList<>();
		String fullString = "";

		for (int i = 0; i < matches.value.size(); i++) {

			if(contextWordsCount > 0) {
				ValueElement match = matches.value.get(i);
				result.add( leftContext(match) + queryWord + rightContext(match) );

			} else {
				result.add(queryWord);
			}
		}

		return result;
	}

	private String leftContext(ValueElement match){
		String leftMostWord = match.previousWord;
		HashValue previousWordMatches;
		ValueElement nearestPreviousWordElem = null;

		for (int i = 0; i < contextWordsCount; i++) {
			previousWordMatches = map.get(leftMostWord);

			nearestPreviousWordElem = getNearestPreviousWordEle(previousWordMatches, match.wordStart);
			leftMostWord = nearestPreviousWordElem.previousWord;
			// after 'contextWordsCount' iterations here, nearestPreviousWordEle will
			// be the ValueElement that represents the leftmost word in the left context string
		}

		int leftMostWordStartIndex = nearestPreviousWordElem.wordStart;
		return text.substring(leftMostWordStartIndex, match.wordStart);
	}

	private ValueElement getNearestPreviousWordEle(HashValue previousWordMatches, int wordStart){
		// find the element in the hash value array who's
		// previousWordStart is the next lesser value compared
		// to the wordStart index for hashValue that is passed in.
		int greatestPreviousWordStart = 0;
		int greatestPreviousWordStartIndex = 0;

		for (int i = 0; i < previousWordMatches.value.size(); i++) {
			int ithPreviousStartIndex = previousWordMatches.value.get(i).previousStartIndex;

			if((greatestPreviousWordStart < ithPreviousStartIndex) && (ithPreviousStartIndex < wordStart)){
				greatestPreviousWordStart = ithPreviousStartIndex;
				greatestPreviousWordStartIndex = i;
			}
		}

		return previousWordMatches.value.get(greatestPreviousWordStartIndex);
	}

	private String rightContext(ValueElement initialQueryWordMatch) {
		String currentContextWord = initialQueryWordMatch.nextWord;
		int currentContextWordIndex = initialQueryWordMatch.nextStartIndex;
		HashValue nextMatches;

		for (int i = 0; i < contextWordsCount-1; i++) {
			nextMatches = map.get(currentContextWord);
			ValueElement nextWordMatch = findNextWordMatch(nextMatches, currentContextWord, currentContextWordIndex);
			currentContextWord = nextWordMatch.nextWord;
			currentContextWordIndex = nextWordMatch.nextStartIndex;
		}

		int rightContextStartIndex = initialQueryWordMatch.wordStart + initialQueryWordMatch.word.length();
		int rightContextEndIndex = currentContextWordIndex + currentContextWord.length();
		return text.substring(rightContextStartIndex , rightContextEndIndex);
	}


	private ValueElement findNextWordMatch(HashValue nextMatches, String currentContextWord, int currentContextWordIndex){
		// from these matches, we need to find the one that comes directly after
		// the currentContextWord in the text.
		ValueElement result;
		ValueElement ithMatch;
		boolean isNextMatch;
		for (int i = 0; i < nextMatches.value.size(); i++) {
			ithMatch = nextMatches.value.get(i);
			isNextMatch = ithMatch.wordStart == currentContextWordIndex;
			if(isNextMatch){
				return ithMatch;
			}
		}

		throw new Error("ERROR: failed to find a matching next word for right context string!");
	}
}

class HashMapGenerator {
	TextTokenizer tokenizer;
	String previousWord;
	int previousWordStart;
	HashMap<String, HashValue> map = new HashMap<>();

	HashMapGenerator(TextTokenizer tokenizerInstance){
		tokenizer = tokenizerInstance;
		populateHashMap();
	}

	private void populateHashMap(){

		while(tokenizer.hasNext()){
			String currentWord = tokenizer.next();

			int currentWordStart = 0;
			if(tokenizer.matcher != null){
				currentWordStart = tokenizer.matcher.start();
			}

			addNextWordToPreviousWordValue(currentWord);

//			if(tokenizer.matcher != null){
//				System.out.println("tokenizer.matcher.start(): "+ tokenizer.matcher.start());
//			}
//
//			System.out.println("tokenizer.matcher: "+ tokenizer.matcher);

			if(tokenizer.matcher != null && currentWordStart == 0){
				// handle first word...
			}

			if(tokenizer.isWord(currentWord)){
				//System.out.println("currentWord: "+ currentWord);

				addToMap(currentWord, currentWordStart);

				// get ready for the next iteration:
				previousWord = currentWord;
				previousWordStart = currentWordStart;
			}
		}
	}

	private void addNextWordToPreviousWordValue(String currentWord){
		//go back and add 'currentWord' as the next word relative to previousWord ?!?!?
		// ... i promise it will all make sense
		if(previousWord != null && tokenizer.matcher != null){
			// If the previous word has multiple ValueElements in it's HashValue array,
			// we need to add 'currentWord' to the ValueElement whose previousWordStart is the largest
			HashValue previousWordValues = map.get(previousWord);

			previousWordValues.updateMaxPrevWordStartElem(currentWord, tokenizer.matcher.start());

			map.replace(previousWord, previousWordValues);
		}

	}

	private void addToMap(String wordKey, int wordStart){
		ValueElement newElement = new ValueElement(
				wordKey,
				wordStart,
			previousWord,
			previousWordStart
		);

		HashValue existingValue = map.get(wordKey);

		if(existingValue != null){
			existingValue.add(newElement);
			map.replace(wordKey, existingValue);

		}else{
			HashValue newValue = new HashValue();
//			System.out.println("creating new value with new element : " + newElement.wordKey + ":"+ newElement.startIndex);
			newValue.add(newElement);
			map.put(wordKey, newValue);
		}
//		System.out.println("key: "+ newKey + ", " + "start: "+ start);
	}
}

class HashValue {
	public ArrayList<ValueElement> value;
	HashValue(){
		value = new ArrayList<ValueElement>();
	}

	public void add(ValueElement newElement){
		value.add(newElement);
	}

	public void updateMaxPrevWordStartElem(String nextWord, int nextStartIndex){
		int maxPrevWordStartElemIndex = 0;
		boolean newMax;
		for (int i = 0; i < value.size(); i++) {
			newMax = value.get(maxPrevWordStartElemIndex).previousStartIndex < value.get(i).previousStartIndex;
			if(newMax){
				maxPrevWordStartElemIndex = i;
			}
		}

		ValueElement maxPrevWordStartElem = value.get(maxPrevWordStartElemIndex);
		maxPrevWordStartElem.nextWord = nextWord;
		maxPrevWordStartElem.nextStartIndex = nextStartIndex;
		value.set(maxPrevWordStartElemIndex, maxPrevWordStartElem);
	}

	public void print(){
		System.out.println("--------------------------------------------");

		for (int i = 0; i < value.size(); i++) {

			System.out.println("\nvalue["+i+"]: "+ value.get(i));

			value.get(i).print();
		}
	}
}

class ValueElement {
	String word;
	int wordStart;
	String previousWord;
	int previousStartIndex;
	String nextWord;
	int nextStartIndex;

	public ValueElement(String word, int wordStart, String previousWord, int previousStartIndex){
		this.word = word;
		this.wordStart = wordStart;
		this.previousWord = previousWord;
		this.previousStartIndex = previousStartIndex;
		this.nextWord = null;
		this.nextStartIndex = 0;
	}

	public void print(){
		System.out.println(
				"\n-- this.wordStart: " + wordStart +
				",\n -- this.previousWord: " + previousWord +
				", this.previousStartIndex: " + previousStartIndex +
				",\n -- this.nextWord: " + nextWord +
				", this.nextStartIndex: " + nextStartIndex
		);

	}
}

