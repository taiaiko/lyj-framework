package org.lyj.ext.netty.server.web.controllers.routing;

import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.MimeTypeUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.netty.server.web.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The routing context
 */
public class RoutingContext
        implements IHttpConstants {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;
    private final HttpServerRequest _request;
    private final HttpServerResponse _response;
    private final String _uri;
    private final HttpParams _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public RoutingContext(final HttpServerConfig config,
                          final HttpServerRequest request,
                          final HttpServerResponse response) {
        _config = config;
        _request = request;
        _response = response;
        _uri = _request.uri();
        _params = new HttpParams(_request.params());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uri() {
        return _uri;
    }

    public String method() {
        return _request.method();
    }

    public RoutingContext handled(final boolean value) {
        _response.handled(value);
        return this;
    }

    public boolean handled() {
        return _response.handled();
    }

    public HttpParams params() {
        return _params;
    }

    public Map<String, String> headers() {
        return _response.headers();
    }

    // ------------------------------------------------------------------------
    //                      r e s p o n s e   h e a d e r s
    // ------------------------------------------------------------------------

    public void addHeader(final String name, final String value) {
        _response.headers().put(name, value);
    }

    public void removeHeader(final String name) {
        _response.headers().remove(name);
    }

    public boolean hasHeader(final String name) {
        return _response.headers().containsKey(name);
    }

    public String getHeader(final String name) {
        return _response.headers().get(name);
    }

    public void removeHeaderAccessControlAllowOrigin() {
        this.removeHeader(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public void addHeaderAccessControlAllowOriginAll() {
        this.addHeaderAccessControlAllowOrigin("*");
    }

    public void addHeaderAccessControlAllowOrigin(final String value) {
        this.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, value);
    }

    // ------------------------------------------------------------------------
    //                      w r i t e    r e s p o n s e   c o n t e n t
    // ------------------------------------------------------------------------

    public void write(final String content) {
        this.write(content, "");
    }

    public void writeInternalServerError(final Throwable t) {
        _response.writeErrorINTERNAL_SERVER_ERROR(t);
    }

    public void writeInternalServerError() {
        _response.writeErrorINTERNAL_SERVER_ERROR();
    }

    public void writeJsonError(final String error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeJsonError(final Throwable error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeErroMissingParams(final String... names) {
        this.writeJson(validateJsonError(new Exception("Bad Request, missing some parameters: " + CollectionUtils.toCommaDelimitedString(names))));
    }

    public void write(final String content,
                      final String content_type) {
        if(StringUtils.hasText(_config.headerAccessControlAllowOrigin())){
            _response.headers().put(ACCESS_CONTROL_ALLOW_ORIGIN, _config.headerAccessControlAllowOrigin());
        }
        _response.headers().put(CONTENT_TYPE,
                StringUtils.hasText(content_type) ? content_type : MimeTypeUtils.MIME_PLAINTEXT);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeJson(final Object content) {
        if(StringUtils.hasText(_config.headerAccessControlAllowOrigin())){
            _response.headers().put(ACCESS_CONTROL_ALLOW_ORIGIN, _config.headerAccessControlAllowOrigin());
        }
        final String json = validateJson(content);
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_JSON);
        _response.headers().put(CONTENT_LENGTH, json.length() + "");
        _response.write(json);
        _response.flush();
    }

    public void writeHtml(final String content) {
        if(StringUtils.hasText(_config.headerAccessControlAllowOrigin())){
            _response.headers().put(ACCESS_CONTROL_ALLOW_ORIGIN, _config.headerAccessControlAllowOrigin());
        }
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_HTML);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeXml(final String content) {
        if(StringUtils.hasText(_config.headerAccessControlAllowOrigin())){
            _response.headers().put(ACCESS_CONTROL_ALLOW_ORIGIN, _config.headerAccessControlAllowOrigin());
        }
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.MIME_XML);
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------

    void addParams(final Map<String, String> params) {
        if (null != params && params.size() > 0) {
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                _params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String validateJson(final Object text) {
        if (StringUtils.isJSON(text)) {
            return text.toString();
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt("response", null != text ? text.toString() : "");
            return json.toString();
        }
    }

    private static String validateJsonError(final Throwable t) {
        return validateJsonError(t.toString());
    }

    private static String validateJsonError(final String text) {
        if (StringUtils.isJSON(text)) {
            return text;
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt("error", text);
            return json.toString();
        }
    }

}