const fs = require('fs');

class generateMap {
    constructor() {
        this.hash = {}

        this.text = fs.readFileSync('../files/short_excerpt.txt', 'utf8')

        this.wordList = this.text
            .split(/[^A-Za-z]/) // split on non letter chars
            // TODO why did the regex leave some empty strings? not matching punctuation ?
            .map(word => word.toLowerCase())
            .filter(word => word.length)

        this.generate()
    }


    generate() {

        this.wordList.map(word => {

            const newEntry = { startIndex: this.text.toLowerCase().indexOf(word) }

            const wordExistsInHash = !!this.hash[word]

            if (wordExistsInHash) {
                this.hash[word].push(newEntry)
            } else {
                this.hash[word] = [newEntry]
            }

        })



        console.log({ wordList: this.wordList })

        console.log(this.hash)
    }

}
const generateMapInstance = new generateMap()