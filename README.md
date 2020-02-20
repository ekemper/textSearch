# textSearch
Coding challenge for StoryBlocks


1) Create a hash table that indexes the word matches from the text: 

``` 
{
  i:[
      {
        word,
        wordStart,
        previousWord,
        previousStartIndex,
        nextWord,
        nextStartIndex
      },
      {
        word,
        wordStart,       ( different matches for the same word )
        previousWord,
        previousStartIndex,
        nextWord,
        nextStartIndex
      }
    ],
  will:[
    ...
    ],
  here:[...],
  .
  .
  .
}
```
2) Use the previous word and next word in each match object to find the starting and ending indices of the context string for n context words.
3) slice the context string from the input text.
