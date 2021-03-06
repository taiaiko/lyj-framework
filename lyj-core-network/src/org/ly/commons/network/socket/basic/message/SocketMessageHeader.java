package org.ly.commons.network.socket.basic.message;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.util.RandomUtils;
import org.lyj.commons.util.json.JsonItem;

import java.io.Serializable;
import java.util.Map;

public class SocketMessageHeader
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_TYPE = "_type";
    private static final String FLD_FILE_NAME = "_file_name";
    private static final String FLD_FILE_SIZE = "_file_size";
    private static final String FLD_CHARSET = "_charset";
    private static final String FLD_HEADERS = "_headers";
    private static final String FLD_CHUNK_UID = "_chunk_uid";
    private static final String FLD_CHUNK_INDEX = "_chunk_index";
    private static final String FLD_CHUNK_COUNT = "_chunk_count";
    private static final String FLD_CHUNK_OFFSET = "_chunk_offset";
    private static final String FLD_CHUNK_LENGTH = "_chunk_length";


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonItem _item;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageHeader() {
        _item = new JsonItem();
        this.init();
    }

    public SocketMessageHeader(final Object item) {
        _item = new JsonItem(item);
        this.init();
    }

    @Override
    public String toString() {
        return _item.toString();
    }

    public JSONObject toJson() {
        return _item.json();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public byte type() {
        return _item.getByte(FLD_TYPE, SocketMessage.MessageType.Undefined.getValue());
    }

    public SocketMessageHeader type(final byte value) {
        _item.put(FLD_TYPE, value);
        return this;
    }


    public String charset() {
        return _item.getString(FLD_CHARSET);
    }

    public SocketMessageHeader charset(final String value) {
        _item.put(FLD_CHARSET, value);
        return this;
    }

    public String fileName() {
        return _item.getString(FLD_FILE_NAME);
    }

    public SocketMessageHeader fileName(final String value) {
        _item.put(FLD_FILE_NAME, value);
        return this;
    }

    public long fileSize() {
        return _item.getLong(FLD_FILE_SIZE);
    }

    public SocketMessageHeader fileSize(final long value) {
        _item.put(FLD_FILE_SIZE, value);
        return this;
    }
    
    public String chunkUid() {
        return _item.getString(FLD_CHUNK_UID);
    }

    public SocketMessageHeader chunkUid(final String value) {
        _item.put(FLD_CHUNK_UID, value);
        return this;
    }

    public int chunkIndex() {
        return _item.getInt(FLD_CHUNK_INDEX);
    }

    public SocketMessageHeader chunkIndex(final int value) {
        _item.put(FLD_CHUNK_INDEX, value);
        return this;
    }

    public int chunkCount() {
        return _item.getInt(FLD_CHUNK_COUNT);
    }

    public SocketMessageHeader chunkCount(final int value) {
        _item.put(FLD_CHUNK_COUNT, value);
        return this;
    }

    public long chunkOffset() {
        return _item.getLong(FLD_CHUNK_OFFSET);
    }

    public SocketMessageHeader chunkOffset(final long value) {
        _item.put(FLD_CHUNK_OFFSET, value);
        return this;
    }

    public int chunkLength() {
        return _item.getInt(FLD_CHUNK_LENGTH);
    }

    public SocketMessageHeader chunkLength(final int value) {
        _item.put(FLD_CHUNK_LENGTH, value);
        return this;
    }

    public JsonItem headers() {
        if (!_item.has(FLD_HEADERS)) {
            _item.put(FLD_HEADERS, new JSONObject());
        }
        return new JsonItem(_item.getJSONObject(FLD_HEADERS));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public int length() {
        return this.getBytes().length;
    }

    public byte[] getBytes() {
        return this.getBytes(this.charset());
    }

    public byte[] getBytes(final String charset) {
        try {
            return _item.toString().getBytes(charset);
        } catch (Throwable ignored) {
            return this.getBytes();
        }
    }

    public void putAll(final SocketMessageHeader values) {
        _item.putAll(values.toJson());
    }

    public void putAll(final Map<String, Object> values) {
        _item.putAll(values);
    }

    public void putAll(final JSONObject values) {
        _item.putAll(values);
    }

    public void putAll(final JsonItem values) {
        _item.putAll(values);
    }

    public void put(final String key, final Object value) {
        _item.put(key, value);
    }

    public Object get(final String key) {
        return _item.get(key);
    }

    public String getString(final String key) {
        return _item.getString(key);
    }

    public int getInt(final String key) {
        return _item.getInt(key);
    }

    public boolean getBoolean(final String key) {
        return _item.getBoolean(key);
    }

    public long getLong(final String key) {
        return _item.getLong(key);
    }

    public double getDouble(final String key) {
        return _item.getDouble(key);
    }

    public JSONArray getJSONArray(final String key) {
        return _item.getJSONArray(key);
    }

    public JSONObject getJSONObject(final String key) {
        return _item.getJSONObject(key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!_item.has(FLD_CHARSET)) {
            _item.put(FLD_CHARSET, CharEncoding.UTF_8);
        }
        if (!_item.has(FLD_CHUNK_INDEX)) {
            _item.put(FLD_CHUNK_INDEX, 1);
        }
        if (!_item.has(FLD_CHUNK_COUNT)) {
            _item.put(FLD_CHUNK_COUNT, 1);
        }
        if (!_item.has(FLD_CHUNK_UID)) {
            _item.put(FLD_CHUNK_UID, RandomUtils.randomUUID(true));
        }
    }


}
