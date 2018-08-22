package org.ly.ose.server.application.endpoints.socket;


import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.server.IConstants;
import org.ly.ose.server.application.controllers.messaging.MessageManager;
import org.lyj.commons.logging.AbstractLogEmitter;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;
import org.lyj.ext.netty.server.websocket.impl.sessions.SessionClientController;

/**
 * Dispatch socket messages
 */
public class SocketController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String REQ_TYPE_TEXT = "text";

    private final SocketServer _server;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketController(final SocketServer server) {
        _server = server;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void notifyRequest(final String session_id,
                              final Object data) {
        try {
            final OSERequest request = new OSERequest(JsonConverter.toObject(data));

            // only messages witha client uid are valid messages
            if (StringUtils.hasText(request.uid())) {
                // set source channel
                request.source(IConstants.CHANNEL_SOCKET);
                // set response channel id (session_id)
                request.clientId(session_id);
                // and address
                request.address(SessionClientController.instance().addressOf(session_id));

                // ready to process message
                // if message has a response, it's immediately dispatched
                // note: socket messages can have async later responses
                final Object response = MessageManager.instance().handle(request);
                if(response instanceof OSEResponse){
                    ((OSEResponse)response).uid(_server.config().uri());
                    // send only if response has a payload
                    if (((OSEResponse)response).hasPayload() || ((OSEResponse)response).hasError()) {
                        sendResponse(request.clientId(), ((OSEResponse)response));
                    }
                }

            }

        } catch (Throwable t) {
            super.error("notifyRequest", t);
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void sendResponse(final String session_id,
                                    final OSEResponse message) {
        // is client still connected?
        if (SessionClientController.instance().exists(session_id)) {
            SessionClientController.instance().open(session_id).writeRaw(message);
        }
    }


}
