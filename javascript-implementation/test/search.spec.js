const searchText = require('../Search')

const assert = require('assert');

describe('searchText', function () {

    it('should search', function () {
        const result = searchText('pre', 4)

        assert.equal(result, 4);
    });
});