package org.lyj.ext.netty.server.web.handlers.impl;

import org.lyj.commons.util.FormatUtils;
import org.lyj.ext.netty.server.web.HttpServerConfig;
import org.lyj.ext.netty.server.web.HttpServerRequest;
import org.lyj.ext.netty.server.web.HttpServerResponse;
import org.lyj.ext.netty.server.web.controllers.routing.IRoute;
import org.lyj.ext.netty.server.web.controllers.routing.IRouter;
import org.lyj.ext.netty.server.web.controllers.routing.Router;
import org.lyj.ext.netty.server.web.HttpServerContext;
import org.lyj.ext.netty.server.web.handlers.AbstractRequestHandler;

/**
 *
 */
public class RoutingHandler
        extends AbstractRequestHandler
        implements IRouter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Router _router;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    protected RoutingHandler(final HttpServerConfig config) {
        super(config);
        _router = new Router(config.encoding());
    }

    @Override
    public void close(){

    }

    @Override
    public void handle(final HttpServerRequest request,
                       final HttpServerResponse response) {
        _router.handle(new HttpServerContext(super.config(), request, response));
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public IRoute all(final String path) {
        this.log("ALL", path);
        return _router.all(path);
    }

    @Override
    public IRoute get(final String path) {
        this.log("GET", path);
        return _router.get(path);
    }

    @Override
    public IRoute post(final String path) {
        this.log("POST", path);
        return _router.post(path);
    }

    @Override
    public IRoute delete(String path) {
        this.log("DELETE", path);
        return _router.delete(path);
    }

    @Override
    public IRoute put(String path) {
        this.log("PUT", path);
        return _router.put(path);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void log(final String method, final String path){
        super.logger().info(FormatUtils.format("Added Route Handler for method '%s': %s", method, path));
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static RoutingHandler create(final HttpServerConfig config) {
        return new RoutingHandler(config);
    }


}
