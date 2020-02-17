const Search = require('../TextSearcher')
// the path to the text file is relative to the wordHashGenterator file, could make this cleaner
//const shortExcerptSearcher = new Search("../files/short_excerpt.txt")
const longExcerptSearcher = new Search("../files/long_excerpt.txt")
const assert = require('assert');

const assertArraysEqual = (expected, actual) => {
    // check size first, then contents:
    assert.equal(expected.length, actual.length);

    for (i = 0; i < expected.length; i++) {
        assert.equal(expected[i], actual[i]);
    }
}


describe('searchText', () => {

    /** Simplest possible case, no context and the word occurs exactly once. */
    it('one hit no context', function () {
        const result = shortExcerptSearcher.searchText('sketch', 0)
        const expected = "sketch";

        assert.equal(result, expected);
    });

    /** Next simplest case, no context and multiple hits. */
    it('multiple hits no context', () => {
        const result = shortExcerptSearcher.searchText('naturalists', 0)
        const expected = ["naturalists", "naturalists"];

        assertArraysEqual(result, expected);
    });

    /** This is the example from the document. */
    it('test basic search', () => {
        const result = shortExcerptSearcher.searchText('naturalists', 3)
        const expected = [
            "great majority of naturalists believed that species",
            "authors. Some few naturalists, on the other"
        ];

        assertArraysEqual(result, expected);
    });

    // /** Same as basic search but a little more context. */
    // it('test basic search, more context', () => {
    //     const result = shortExcerptSearcher.searchText('naturalists', 6)
    //     const expected = [
    //         "Until recently the great majority of naturalists believed that species were immutable productions",
    //         "maintained by many authors. Some few naturalists, on the other hand, have believed"
    //     ];

    //     assertArraysEqual(result, expected);
    // });

    // /** Tests query word with apostrophe. */
    // it('apostrophe', () => {
    //     const result = longExcerptSearcher.searchText("animal's", 4)
    //     const expected = [
    //         "not indeed to the animal's or plant's own good",
    //         "habitually speak of an animal's organisation as\r\nsomething plastic"
    //     ];

    //     assertArraysEqual(result, expected);
    // });

    // /** Tests numeric query word. */
    // it('numerical query', () => {
    //     const result = longExcerptSearcher.searchText("1844", 2)
    //     const expected = [
    //         "enlarged in 1844 into a",
    //         "sketch of 1844--honoured me"
    //     ];

    //     assertArraysEqual(result, expected);
    // });

    // /** Tests mixed alphanumeric query word. */
    // it('mixed alphanumerical query', () => {
    //     const result = longExcerptSearcher.searchText("xxxxx10x", 3)
    //     const expected = ["date first edition [xxxxx10x.xxx] please check"]
    //         ;

    //     assertArraysEqual(result, expected);
    // });



});