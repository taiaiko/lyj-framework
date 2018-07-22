package org.ly.ose.commons.model.messaging.payloads;

import org.lyj.ext.db.model.MapDocument;

public abstract class OSEPayload {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final MapDocument _map;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEPayload() {
        _map = new MapDocument();
    }

    public OSEPayload(final Object item) {
        if (item instanceof MapDocument) {
            _map = (MapDocument) item;
        } else {
            _map = new MapDocument(item);
        }
    }

    @Override
    public String toString() {
        return _map.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public MapDocument map(){
        return _map;
    }


}
