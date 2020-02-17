const fs = require('fs');

class WordHashGenerator {
    constructor(filePath) {
        this.hash = {}

        this.text = fs.readFileSync(filePath, 'utf8')//.replace(/\n|\r/g, ' ')

        this.wordList = this.text
            .split(/\W+/g) // TODO : need to refine regex to match words
        // TODO why did the regex leave some empty strings? not matching punctuation ?
        //.map(word => word.toLowerCase())

        //.filter(word => word.length)

        console.log({ 'wordlist': this.wordList })

        // this.generate()
    }

    generate() {
        this.wordList.map(word => {

            const wordExistsInHash = !!this.hash[word]

            if (wordExistsInHash) {

                const greatestExistingStartIndex = this.hash[word]
                    .sort(function (a, b) {
                        return b.startIndex - a.startIndex;
                    })[0].startIndex;

                this.hash[word].push({
                    startIndex: this.text.toLowerCase().indexOf(word, greatestExistingStartIndex + word.length)
                })

            } else {
                this.hash[word] = [{ startIndex: this.text.toLowerCase().indexOf(word) }]
            }
        })

        console.log(this.hash)
    }

}

module.exports = WordHashGenerator