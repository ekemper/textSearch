module.exports = {
    wordArrayFromText: text => {
        return text
            .split(/[^A-Za-z]/) // split on non letter chars
            // TODO why did the regex leave some empty strings? 
            // not matching punctuation ?
            .map(word => word.toLowerCase())
            .filter(word => word.length)
    }
}