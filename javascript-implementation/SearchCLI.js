const searchText = require('./Search')

class SearchCLI {
    constructor() {
        this.searchText = searchText

        this.readline = require('readline').createInterface({
            input: process.stdin,
            output: process.stdout
        })
        this.gatherInput()
    }

    gatherInput() {
        this.readline.question(`\n\nEnter a search term: \n\n -->  `, word => {
            const result = this.searchText(word)
            const message = result
                ? result
                : `\n\nSorry, we could not find "${word}" in the text...\n\n`

            console.log(message)

            this.gatherInput()
        })
    }
}

const searchCLI = new SearchCLI()