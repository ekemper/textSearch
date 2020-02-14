const fs = require('fs');

class WordHashGenerator {
    constructor() {
        this.hash = {}

        this.text = fs.readFileSync('../files/short_excerpt.txt', 'utf8')

        // we will pop the words off of this string as we match them
        this.textTemp = this.text

        this.wordList = this.text
            .split(/[^A-Za-z]/) // split on non letter chars
            // TODO why did the regex leave some empty strings? not matching punctuation ?
            .map(word => word.toLowerCase())
            .filter(word => word.length)

        this.generate()
    }


    generate() {

        this.wordList.map(word => {

            const newEntry = { startIndex: this.textTemp.toLowerCase().indexOf(word) }

            // remove the 
            this.textTemp = this.textTemp.substr(0, newEntry.startIndex)
                + this.textTemp.substr(newEntry.startIndex + word.length);

            const wordExistsInHash = !!this.hash[word]

            if (wordExistsInHash) {
                this.hash[word].push(newEntry)
            } else {
                this.hash[word] = [newEntry]
            }

        })



        // console.log({ wordList: this.wordList })

        // console.log(this.hash)
    }

}

module.exports = new WordHashGenerator()