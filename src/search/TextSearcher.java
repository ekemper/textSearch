package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import	search.TextTokenizer;

public class TextSearcher {
	private static TextTokenizer tokenizer;
	private String wordRegex = "-?\\d+|\\b([a-zA-Z]+'[a-zA-Z]+)\\b|\\w+";
	HashMap<String, HashValue> map = new HashMap<>();

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
		tokenizer = new TextTokenizer(fileContents, wordRegex);

		HashMapGenerator hashGenerator = new HashMapGenerator(tokenizer);
		map = hashGenerator.map;
//		System.out.println("map: "+ map);
	}

	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {

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

		for (int i = 0; i < matches.value.size(); i++) {
			result.add(formContextStringForMatch(matches.value.get(i)));
		}

		return result;
	}

	private String formContextStringForMatch(ValueElement match){
		String result = "";

		System.out.println("match: "+ match);

		return result;
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

//			if(tokenizer.matcher != null){
//				System.out.println("tokenizer.matcher.start(): "+ tokenizer.matcher.start());
//			}
//
//			System.out.println("tokenizer.matcher: "+ tokenizer.matcher);

			if(tokenizer.matcher != null && tokenizer.matcher.start() == 0){
				// handle first word...
			}

			if(tokenizer.isWord(currentWord)){
				addToMap(currentWord);

				// get ready for the next iteration:
				previousWord = currentWord;
				previousWordStart = tokenizer.matcher.start();
			}
		}
	}

	private void addToMap(String wordKey){
		ValueElement newElement = new ValueElement(previousWord, previousWordStart);
		HashValue existingValue = map.get(wordKey);

		if(existingValue != null){
			existingValue.add(newElement);
			map.replace(wordKey, existingValue);
		}else{
			HashValue newValue = new HashValue(newElement);
			newValue.add(newElement);
			map.put(wordKey, newValue);
		}
//		System.out.println("key: "+ newKey + ", " + "start: "+ start);
	}
}


class HashValue {
	public ArrayList<ValueElement> value;
	HashValue(ValueElement newElement){
		value = new ArrayList<ValueElement>();
		value.add(newElement);
	}

	public void add(ValueElement newElement){
		value.add(newElement);
	}
}

class ValueElement {
	String wordKey;
	int startIndex;

	public ValueElement(String wordKey, int startIndex){
		this.wordKey = wordKey;
		this.startIndex = startIndex;
	}
}

