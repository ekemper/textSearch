const generator = require('./WordHashGenerator')
const hash = generator.hash
// const text = generator.text

const searchText = word => {

    const result = hash[word]

    return result
        ? result
        : `\n\nSorry, we could not find "${word}" in the text...\n\n`
}


module.exports = searchText
