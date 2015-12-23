package org.lyj.ext.mongo.schema;

import org.bson.Document;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Index wrapper
 */
public class LyjMongoIndex {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _name; // optional index name (assigned from Mongo if leaved empty)
    private boolean _unique;
    private final Map<String, Integer> _fields;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public LyjMongoIndex() {
        _fields = new LinkedHashMap<>();
    }

    public LyjMongoIndex(final String fieldName, final int sortOrder) {
        this();
        this.addField(fieldName, sortOrder);
    }

    public LyjMongoIndex(final Document fields) {
        this();
        final Set<String> keys = fields.keySet();
        for (final String key : keys) {
            _fields.put(key, fields.getInteger(key));
        }
    }

    public LyjMongoIndex(final Map<String, Integer> fields) {
        _fields = new LinkedHashMap<>(fields);
    }

    @Override
    public String toString() {
        final Document result = new Document();
        result.put("name", this.getName());
        result.put("index", this.toDocument());
        result.put("options", new Document("unique", this.isUnique()));

        return result.toJson();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getName() {
        return _name;
    }

    public void setName(final String value) {
        if (StringUtils.hasText(value)) {
            _name = value;
        }
    }

    public boolean isUnique() {
        return _unique;
    }

    public void setUnique(final boolean value) {
        _unique = value;
    }

    public Set<String> getFieldNames() {
        return _fields.keySet();
    }

    public boolean hasFields() {
        return _fields.keySet().size() > 0;
    }

    public String getSignature() {
        final StringBuilder result = new StringBuilder();
        final Set<String> keys = _fields.keySet();
        for (final String key : keys) {
            result.append(key);
        }
        return MD5.encode(result.toString()).toUpperCase();
    }

    /**
     * Add a field to index
     *
     * @param name      Field name
     * @param sortOrder Sort Order: 1=ascending, -1=descending
     */
    public void addField(final String name, final int sortOrder) {
        _fields.put(name, sortOrder == 1 || sortOrder == -1 ? sortOrder : 1);
    }

    /**
     * Return an instance of Document to use with method MongoCollection.createIndex
     *
     * @return Document. ex: "{email:1}"
     */
    public Document toDocument() {
        final Document result = new Document();
        final Set<String> names = this.getFieldNames();
        for (final String name : names) {
            result.put(name, _fields.get(name));
        }
        return result;
    }


}
