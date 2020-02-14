const util = require('./Utilities')
const WordHashGenerator = require('./WordHashGenerator')

class TextSearcher {
    constructor(textFilePath) {

        this.textFilePath = textFilePath
        this.generator = new WordHashGenerator(textFilePath)

        this.hash = this.generator.hash
        this.text = this.generator.text
    }

    searchText(word, contextWordCount) {
        const wordStartIndices = this.hash[word]

        if (wordStartIndices) {

            return wordStartIndices.map(start => {
                return this.leftContextString(start.startIndex, contextWordCount)
                    + word
                    + this.rightContextString(word, start.startIndex, contextWordCount)
            })

        } else {
            return null
        }
    }

    rightContextString(word, startIndex, contextWordCount) {

        let currentRightWordCount = 0
        let whileArg = 0 < contextWordCount
        let rightContextString = ''
        let rightCharCount = 0
        let currentRightBound = 0

        const wordEndIndex = startIndex + word.length

        while (whileArg) {

            currentRightBound = wordEndIndex + rightCharCount

            if (currentRightBound <= this.text.length) {
                rightContextString = this.text.substring(wordEndIndex, currentRightBound)
                currentRightWordCount = this.wordCount(rightContextString)
                rightCharCount++
                whileArg = currentRightWordCount < contextWordCount

            } else {
                rightContextString = this.text.substring(wordEndIndex, this.text.length)
                whileArg = false
            }
        }

        return rightContextString
    }

    leftContextString(startIndex, contextWordCount) {

        let leftContextString = ''
        let leftCharCount = 0
        let currentLeftWordCount = 0
        let whileArg = 0 < contextWordCount
        let currentLeftBound = 0

        while (whileArg) {

            currentLeftBound = startIndex - leftCharCount

            if (currentLeftBound >= 0) {

                leftContextString = this.text.substring(currentLeftBound, startIndex)
                currentLeftWordCount = this.wordCount(leftContextString)
                leftCharCount++
                whileArg = currentLeftWordCount < contextWordCount

            } else {
                leftContextString = this.text.substring(0, startIndex)
                whileArg = false
            }
        }

        return leftContextString
    }

    wordCount(str) {
        return util.wordArrayFromText(str).filter(word => {
            //if (word.length < 2 && !!this.hash[word]) console.log(word)
            return this.hash[word]
        }).length
    }
}

module.exports = TextSearcher
