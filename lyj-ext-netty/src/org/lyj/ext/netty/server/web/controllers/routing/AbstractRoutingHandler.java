package org.lyj.ext.netty.server.web.controllers.routing;

import org.lyj.commons.Delegates;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerContext;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Superclass for all routing handlers.
 */
public class AbstractRoutingHandler
        extends AbstractRequestHandler {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Set<Delegates.Callback<HttpServerContext>> _handlers;
    private final HttpServerConfig _config;
    private final String _encoding;
    private HttpServerContext _context;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractRoutingHandler(final HttpServerConfig config) {
        super(config);
        _config = config;
        _encoding = config.encoding();
        _handlers = new LinkedHashSet<>();
    }

    @Override
    public void close(){

    }

    @Override
    public final void handle(final HttpServerRequest request,
                             final HttpServerResponse response) {
        _context = new HttpServerContext(_config, request, response);
        this.handle();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public IRouter handler(final Delegates.Callback<HttpServerContext> callback){
        _handlers.add(callback);
        return null;
    }



    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    private void handle(){
        if(!_context.handled()){
            for(Delegates.Callback<HttpServerContext> handler:_handlers){
                handler.handle(_context);
                if(_context.handled()){
                    return; // already handled - break chain
                }
            }
        }
    }

}
