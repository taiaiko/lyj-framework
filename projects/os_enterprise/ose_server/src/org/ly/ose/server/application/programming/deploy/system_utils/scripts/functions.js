/**
 * functions.js
 * ---------------
 * Sample functions
 */
module.exports = (function () {

    // ------------------------------------------------------------------------
    //              i m p o r t s
    // ------------------------------------------------------------------------

    var _CONST = require('/scripts/constants');

    // ------------------------------------------------------------------------
    //              c o n s t
    // ------------------------------------------------------------------------

    var FILE = 'functions.js';// used only for logs

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    var _counter = 0;

    // ------------------------------------------------------------------------
    //              i n s t a n c e
    // ------------------------------------------------------------------------

    var instance = {};

    /**
     * Return script version
     * @return {*}
     */
    instance.version = function () {
        try {
            return version();
        } catch (err) {
            console.error(FILE + '#version', err);
            return err;
        }
    };

    instance.echo = function (value) {
        try {
            return value;
        } catch (err) {
            console.error(FILE + '#echo', err);
            return err;
        }
    };

    instance.count = function () {
        try {
            _counter++;
            return _counter;
        } catch (err) {
            console.error(FILE + '#echo', err);
            return err;
        }
    };

    // ------------------------------------------------------------------------
    //              p r i v a t e
    // ------------------------------------------------------------------------

    function version() {
        return _CONST.version;
    }

    // ------------------------------------------------------------------------
    //              e x p o r t s
    // ------------------------------------------------------------------------

    return instance;


})();