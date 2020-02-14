const searchText = require('../Search')

const assert = require('assert');

describe('Basic Mocha String Test', function () {

    it('should search', function () {
        const result = searchText('pre')

        assert.equal(result, 4);
    });

    // it('should return first charachter of the string', function () {
    //     assert.equal("Hello".charAt(0), 'H');
    // });

});