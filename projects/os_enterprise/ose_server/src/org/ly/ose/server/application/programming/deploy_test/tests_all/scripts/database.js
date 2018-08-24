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

    var FILE = 'database.js';// used only for logs

    var COLLECTION = 'coll_sample';

    // ------------------------------------------------------------------------
    //              f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //              i n s t a n c e
    // ------------------------------------------------------------------------

    var instance = {};

    /**
     * Test upsert of an item
     */
    instance.upsert = function () {
        try {
            return addItems(1);
        } catch (err) {
            console.error(FILE + '#upsert', err);
            return err;
        }
    };

    /**
     * https://docs.arangodb.com/3.1/AQL/Fundamentals/BindParameters.html
     */
    instance.find = function () {
        try {
            var items = addItems(20);
            var query = "FOR t IN " + COLLECTION + "\n" +
                "  FILTER t.index > @index\n" +
                "  RETURN t";
            var args = {
                index:3
            };
            var response = $db.collection(COLLECTION).find(query, args);

            return response;
        } catch (err) {
            console.error(FILE + '#find', err);
            return err;
        }
    };

    instance.findEqual = function () {
        try {
            var items = addItems(20);

            var args = {
                _key:'sample_item_10'
            };

            return $db.collection(COLLECTION).findEqual(args);
        } catch (err) {
            console.error(FILE + '#findEqual', err);
            return err;
        }
    };

    instance.findEqualAsc = function (param) {
        try {
            var items = addItems(20);

            var args = {
                rnd:1
            };

            // log a parameter, just for testing pompous
            console.log(FILE + '#findEqualAsc', "PARAM: '" + param + "'");

            return $db.collection(COLLECTION).findEqualAsc(args, ['timestamp'], 0, 3);
        } catch (err) {
            console.error(FILE + '#findEqualAsc', err);
            return err;
        }
    };

    instance.forEach = function () {
        try {
            var items = addItems(20);

            var args = {
                rnd:1
            };

            // log a parameter, just for testing pompous
            console.log(FILE + '#forEach', "START");

            var response = [];

            $db.collection(COLLECTION).forEachEqual(args, function(item){

                console.log(FILE + '#forEach', item);

                response.push(item);

                return false;// continue loop
            });

            return response;
        } catch (err) {
            console.error(FILE + '#forEach', err);
            return err;
        }
    };

    // ------------------------------------------------------------------------
    //              p r i v a t e
    // ------------------------------------------------------------------------

    function addItems(count) {

        var response = [];

        for (var i = 0; i < count; i++) {
            var model = {
                "_key": "sample_item_" + i,
                "name": "This is a sample entity to store into database",
                "timestamp": (new Date()).getTime(),
                "index":(i===17?"🤘":i),
                "rnd": 1
            };
            response.push($db.collection(COLLECTION).upsert(model));
        }


        return response;
    }

    function addIndexes(){
        $db.collection(COLLECTION).addIndex(['index'], false);
        $db.collection(COLLECTION).addIndex(['rnd'], false);
    }

    // ------------------------------------------------------------------------
    //              e x p o r t s
    // ------------------------------------------------------------------------

    addIndexes();
    
    return instance;


})();