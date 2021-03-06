package org.lyj.ext.netty.server.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.commons.util.converters.JsonConverter;

import java.io.File;
import java.util.Map;

/**
 * The routing context
 */
public class HttpServerContext
        implements IHttpConstants {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String FLD_RESPONSE = "response";
    public static final String FLD_ERROR = "error";

    private static final String ERR_PREFIX = "err_"; // coded errors

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final HttpServerConfig _config;
    private final String _encoding;
    private final HttpServerRequest _request;
    private final HttpServerResponse _response;
    private final String _uri;
    private final HttpParams _params;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public HttpServerContext(final HttpServerConfig config,
                             final HttpServerRequest request,
                             final HttpServerResponse response) {
        _config = config;
        _encoding = config.encoding();
        _request = request;
        _response = response;
        _uri = _request.uri();
        _params = new HttpParams(_request);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uri() {
        return _uri;
    }

    public String protocol() {
        final String protocol_version = _request.protocolVersion().protocolName().toLowerCase();
        return _config.useSsl() ? protocol_version.concat("s://") : protocol_version.concat("://");
    }

    public String rootUrl() {
        final String protocol = this.protocol();
        final String host = this._request.host();

        return protocol.concat(host);
    }

    public String fullUrl() {
        final String root = this.rootUrl();
        final String uri = this.uri();

        return root.concat(uri);
    }

    public String method() {
        return _request.method();
    }

    public HttpServerContext handled(final boolean value) {
        _response.handled(value);
        return this;
    }

    public boolean handled() {
        return _response.handled();
    }

    public HttpParams params() {
        return _params;
    }

    public Map<String, String> responseHeaders() {
        return _response.headers();
    }

    public Map<String, String> requestHeaders() {
        return _request.headers();
    }


    public String getLang() {
        String langCode = "";
        final String accept_language = this.requestHeaders().get(ACCEPT_LANGUAGE);
        if (StringUtils.hasText(accept_language)) {
            final String[] tokens = StringUtils.split(accept_language, ",");
            if (tokens.length > 0) {
                langCode = CollectionUtils.get(StringUtils.split(tokens[0], ";"), 0);
            }
        }

        if (!StringUtils.hasText(langCode)) {
            langCode = this.getParam("lang");
        }
        if (!StringUtils.hasText(langCode)) {
            langCode = this.getParam("locale");
        }

        return StringUtils.hasText(langCode) ? LocaleUtils.getLanguage(langCode) : LocaleUtils.getCurrent().getLanguage();
    }

    public HttpServerResponse response() {
        return _response;
    }

    public HttpServerRequest request() {
        return _request;
    }

    // ------------------------------------------------------------------------
    //                      p a r a m s   h e l p e r s
    // ------------------------------------------------------------------------

    public void addParams(final Map<String, String> params) {
        if (null != params && params.size() > 0) {
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                _params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getParam(final String paramName) {
        return this.getParam(paramName, "");
    }

    public String getParam(final String paramName, final String defVal) {
        String result = defVal;
        try {
            result = this.params().getString(paramName);
        } catch (Throwable ignored) {
            // ignored error
        }
        return StringUtils.hasText(result) ? result : defVal;
    }

    public boolean getParamBoolean(final String paramName) {
        return this.getParamBoolean(paramName, false);
    }

    public boolean getParamBoolean(final String paramName, final boolean defVal) {
        return ConversionUtils.toBoolean(this.getParam(paramName), defVal);
    }

    public int getParamInteger(final String paramName) {
        return this.getParamInteger(paramName, 0);
    }

    public int getParamInteger(final String paramName, final int defVal) {
        return ConversionUtils.toInteger(this.getParam(paramName), defVal);
    }

    public long getParamLong(final String paramName) {
        return this.getParamLong(paramName, 0);
    }

    public long getParamLong(final String paramName, final long defVal) {
        return ConversionUtils.toLong(this.getParam(paramName), defVal);
    }

    public double getParamDouble(final String paramName) {
        return this.getParamDouble(paramName, 0);
    }

    public double getParamDouble(final String paramName, final double defVal) {
        return ConversionUtils.toDouble(this.getParam(paramName), defVal);
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
        _response.removeHeader(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public void addHeaderAccessControlAllowOriginAll() {
        _response.addHeaderAccessControlAllowOrigin("*");
    }

    public void addHeaderAccessControlAllowOrigin(final String value) {
        _response.addHeaderAccessControlAllowOrigin(value);
    }

    // ------------------------------------------------------------------------
    //                      w r i t e    r e s p o n s e   c o n t e n t
    // ------------------------------------------------------------------------

    public void write(final String content) {
        this.write(content, "");
    }

    public void writeStatus(final HttpResponseStatus status) {
        this.addCORSHeaders();
        _response.write(status, null);
    }

    public void writeStatus(final HttpResponseStatus status,
                            final String content) {
        this.addCORSHeaders();
        _response.write(status, content);
    }

    public void writeInternalServerError(final Throwable t) {
        this.addCORSHeaders();
        _response.writeErrorINTERNAL_SERVER_ERROR(t);
    }

    public void writeInternalServerError() {
        this.addCORSHeaders();
        _response.writeErrorINTERNAL_SERVER_ERROR();
    }

    public void writeJsonError(final String error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeJsonError(final Throwable error) {
        this.writeJson(validateJsonError(error));
    }

    public void writeJsonError(final Throwable error, final String methodName) {
        this.writeJson(validateJsonError(error, methodName));
    }

    public void writeErroMissingParams(final String... names) {
        this.writeJson(validateJsonError(new Exception("Bad Request, missing some parameters: " + CollectionUtils.toCommaDelimitedString(names))));
    }

    public void write(final String content,
                      final String content_type) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE,
                StringUtils.hasText(content_type) ? content_type : MimeTypeUtils.getMimePlaintext(_encoding));
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeJson(final Object content) {
        this.addCORSHeaders();

        final String json = validateJson(content);
        final int len = json.length();
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.getMimeJson(_encoding));
        _response.headers().put(CONTENT_LENGTH, len + "");
        _response.write(json);
        _response.flush();
    }

    public void writeHtml(final String content) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.getMimeHtml(_encoding));
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeImage(final byte[] content) {
        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.getMimeImageJpg());
        _response.headers().put(CONTENT_LENGTH, content.length + "");
        _response.write(content);
        _response.flush();
    }

    public void writeFile(final File file) {
        _response.writeFile(file);
        _response.flush();
    }


    public void writeXml(final String content) {
        this.addCORSHeaders();

        _response.headers().put(CONTENT_TYPE, MimeTypeUtils.getMimeXml(_encoding));
        _response.headers().put(CONTENT_LENGTH, content.length() + "");
        _response.write(content);
        _response.flush();
    }

    public void writeRawValue(final Object value) {
        this.write(null != value ? value.toString() : "");
    }

    public void writeRedirect(final String newUri) {
        _response.writeRedirect(newUri);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String validateJson(final Object obj) {
        /*
        String response = "";
        if (obj instanceof Map) {
            final JSONObject json = new JSONObject((Map) obj);
            response = json.toString();
        } else if (StringUtils.isJSON(obj)) {
            response = obj.toString();
        } else if (obj instanceof Collection) {
            response = JsonWrapper.toJSONArray(obj).toString();
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt(FLD_RESPONSE, null != obj ? obj.toString() : "");
            response = json.toString();
        }
        */

        // convert to json compatible value
        String response = JsonConverter.toJson(obj).toString();

        // wrap  a simple value
        if (!StringUtils.isJSON(response)) {
            final JSONObject json = new JSONObject();
            json.putOpt(FLD_RESPONSE, null != response ? response : "");
            response = json.toString();
        }

        // check eur symbol that can cause problems (2 more bytes are needed)
        // TODO: REMOVE THIS UGLY CODE
        if (StringUtils.isJSON(response) && response.contains("€")) {
            response = response + "  ";
        }
        return response;
    }

    private static String validateJsonError(final Throwable t) {
        final String error = ExceptionUtils.getRealMessage(t);
        return validateJsonError(StringUtils.hasText(error) ? error : t.toString());
    }

    private static String validateJsonError(final Throwable t,
                                            final String methodName) {
        final String message = ExceptionUtils.getRealMessage(t);
        // check if error is a coded error
        if (message.startsWith(ERR_PREFIX)) {
            return validateJsonError(message);
        } else {
            if (StringUtils.hasText(methodName)) {
                final String error = FormatUtils.format("[%s] ERROR: '%s'", methodName, message);
                return validateJsonError(error);
            } else {
                return validateJsonError(t);
            }
        }
    }

    private static String validateJsonError(final String text) {
        if (StringUtils.isJSON(text)) {
            return text;
        } else {
            final JSONObject json = new JSONObject();
            json.putOpt(FLD_ERROR, text);
            return json.toString();
        }
    }

    private void addCORSHeaders() {
        final String allowed_origins = _config.corsAllowOrigin();
        if (StringUtils.hasText(allowed_origins)) {
            this.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowed_origins);
            if (allowed_origins.equals("*")) {
                if (StringUtils.hasText(_config.corsAllowMethods())) {
                    this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _config.corsAllowMethods());
                } else {
                    final String method = _request.headerValue(ACCESS_CONTROL_REQUEST_METHOD);
                    if (null != method) {
                        this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _request.headerValue(ACCESS_CONTROL_REQUEST_METHOD));
                    } else {
                        this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, DELETE, PUT");
                    }
                }
                if (StringUtils.hasText(_config.corsAllowHeaders())) {
                    this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _config.corsAllowHeaders());
                } else {
                    final String headers = _request.headerValue(ACCESS_CONTROL_REQUEST_HEADERS);
                    if (null != headers) {
                        this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _request.headerValue(ACCESS_CONTROL_REQUEST_HEADERS));
                    } else {
                        this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, StringUtils.toString(_request.headerNames()));
                    }
                }
            } else {
                this.addHeader(ACCESS_CONTROL_ALLOW_METHODS, _config.corsAllowMethods());
                this.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, _config.corsAllowHeaders());
            }
        }
    }


}
