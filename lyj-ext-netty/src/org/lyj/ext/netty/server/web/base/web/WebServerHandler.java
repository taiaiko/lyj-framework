/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.lyj.ext.netty.server.web.base.web;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.ExceptionUtils;
import org.lyj.ext.netty.server.web.HttpServer;
import org.lyj.ext.netty.server.web.controllers.HttpServerRequestContext;
import org.lyj.ext.netty.server.web.controllers.HttpServerRequestHandlers;
import org.lyj.ext.netty.server.web.utils.CookieUtil;
import org.lyj.ext.netty.server.web.utils.ResponseUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebServerHandler
        extends SimpleChannelInboundHandler<HttpObject> {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerRequestHandlers _handlers;
    private final HttpServerRequestContext _context;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public WebServerHandler(final HttpServer server) {
        _handlers = server.handlers();
        _context = new HttpServerRequestContext(server.config());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        this.logger().error(cause.toString());
        if (ctx.channel().isActive()) {
            ResponseUtil.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx,
                                final HttpObject msg) {

        if (!msg.decoderResult().isSuccess()) {
            ResponseUtil.sendError(ctx, BAD_REQUEST);
            return;
        }

        _handlers.handle(_context.handle(ctx, msg));


    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger logger() {
        return LoggingUtils.getLogger(this);
    }


}