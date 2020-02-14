const util = require('./Utilities')
const generator = require('./WordHashGenerator')
const hash = generator.hash
const text = generator.text

const searchText = (word, contextWordCount) => {
    console.log({ wordstart: hash[word] })

    const wordStartIndices = hash[word]

    console.log({ wordStartIndices })

    return wordStartIndices.map(start => {
        return getContextString(word, start.startIndex, contextWordCount)
    })
}

const getContextString = (word, startIndex, contextWordCount) => {
    let leftContextString = ''
    let leftCharCount = 0
    let rightContextString = ''
    let rightCharCount = 0

    let currentWordCount = 0

    console.log({ contextWordCount })

    let whileArg = 0 < contextWordCount

    console.log({ whileArg })

    while (whileArg) {
        leftContextString = text.substring(startIndex - leftCharCount, startIndex)
        currentWordCount = wordCount(leftContextString)

        console.log({ currentWordCount })
        console.log({ leftContextString })
        console.log({ leftCharCount })
        leftCharCount++
        whileArg = currentWordCount < contextWordCount
    }

    // while (wordCount(rightContextString) > contextWordCount) {

    // }

    return leftContextString + word + rightContextString
}

const wordCount = str => {
    console.log({ str })
    console.log({ wordArray: util.wordArrayFromText(str) })
    console.log({ wordcount: util.wordArrayFromText(str).length })
    return util.wordArrayFromText(str).lenth
}

module.exports = searchText
