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
            console.log(this.searchText(word))
            this.gatherInput()
        })
    }
}

const searchCLI = new SearchCLI()