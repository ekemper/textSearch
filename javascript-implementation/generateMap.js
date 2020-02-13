const fs = require('fs');

class generateMap {
    constructor() {
        this.map = {}
        this.text = fs.readFileSync('../files/short_excerpt.txt', 'utf8')

        this.generate()
    }


    generate() {

        let currentChar = ''
        let nextChar = ''
        let lastNonAlphaPosition = null

        let previousWord = null
        let previousWordStart = 0
        let previousWordLength = null
        let currentWord = null
        let currentWordStart = null

        for (var i = 0; i < this.text.length; i++) {

            currentChar = this.text[i]
            nextChar = this.text[i + 1]
            console.log("----------------------", { currentChar })

            const nextIsEndOfText = (i === this.text.length - 1)
            if (nextIsEndOfText) continue

            const foundWordBoundary = !this.isLetter(currentChar) && this.isLetter(nextChar)
            if (foundWordBoundary) {

                currentWordStart = this.determineCurrentWordStart(i, lastNonAlphaPosition)

                // need to remove non letter chars here cause we are slicing backward 
                // from the next words start... could be a better way ?
                currentWord = this.text
                    .slice(currentWordStart, i)
                    .replace(/[^A-Za-z]/g, "")
                    .toLocaleLowerCase()

                // TODO rename ? 
                this.setNextOnPrevious(previousWord, previousWordStart, currentWord, currentWordStart)

                const newMapEntry = {
                    word: currentWord,
                    startIndex: currentWordStart,
                    length: i - 1 - lastNonAlphaPosition,
                    previousWord,
                    previousWordStart,
                    // previousWordLength,
                    //nextWordStart,
                    // nextWordLength
                }

                this.addWordToMap(currentWord, newMapEntry)

                // set up for the next iteration
                previousWord = currentWord
                previousWordStart = currentWordStart
                lastNonAlphaPosition = i
            }
        }

        //console.log(this.map)
    }

    determineCurrentWordStart(i, lastNonAlphaPosition) {
        // get the beginning index of the word that was just traversed
        return lastNonAlphaPosition
            ? lastNonAlphaPosition + 1
            : 0
    }

    setNextOnPrevious(previousWord, previousWordStart, currentWord, currentWordStart) {

        if (previousWord) {// TODO : need to patch this logic for the first word

            // set the current word as the 'next' word for the previous word ( ?!?! )
            // i promise it will make sense

            //console.log({ previousWord, "this.map[previousWord]": this.map[previousWord] })

            this.map[previousWord] = this.map[previousWord].map(previousWordObj => {
                //since there might be more than one...
                //console.log({ previousWordObj, previousWordStart })
                if (previousWordObj.startIndex === previousWordStart) {

                    return {
                        ...previousWordObj,
                        nextWord: currentWord,
                        nextWordStart: currentWordStart,
                        nextWordLength: 'todo'
                    }
                }
            })
        }
    }

    addWordToMap(currentWord, newMapEntry) {

        console.log('inaddword to map', { currentWord })

        if (!currentWord || !currentWord.length) {
            throw new Error("cannot put current word in map", { currentWord, newMapEntry })
        }

        const currentWordExistsInHash = !!this.map[currentWord]

        // console.log(currentWord, this.map[currentWord])
        if (!currentWordExistsInHash) {

            // add the word that was just traversed to the hash
            // as an array so that we can handle duplecates 
            this.map[currentWord] = [newMapEntry]

        } else {
            // append the details for the duplicate instance of the word
            this.map[currentWord].push(newMapEntry)
        }

    }


    isLetter(character) {
        return !!character.match(/^[a-z]+$/i) && character.length
    }
}
const generateMapInstance = new generateMap()