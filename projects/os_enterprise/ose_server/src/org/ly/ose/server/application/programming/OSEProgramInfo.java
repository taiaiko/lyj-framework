package org.ly.ose.server.application.programming;

import org.json.JSONObject;
import org.ly.ose.server.IConstants;
import org.lyj.commons.logging.Level;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Program descriptor
 */
public class OSEProgramInfo
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // custom data fields
    public static final String FLD_SESSION_ID = "session_id";
    public static final String FLD_CLIENT_ID = "client_id";

    public enum FieldName {
        type,
        namespace,
        name,
        description,
        version,
        author,
        session_timeout,
        loop_interval,
        singleton,
        autostart,
        microservice,
        logging,

        // added from deployer
        files,
        api_host
    }

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _namespace;
    private String _name;
    private String _description;
    private String _version;
    private String _author;
    private int _session_timeout;
    private int _loop_interval;
    private boolean _singleton;
    private boolean _autostart;
    private boolean _microservice;
    private String _log_level; // SEVERE, INFO, ERROR,...
    private String _api_host;

    private String _installation_root;

    private final Map<File, String> _files;

    private final Map<String, Object> _custom_data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public OSEProgramInfo() {
        _files = new HashMap<>();
        _custom_data = new HashMap<>();
        this.init();
    }

    public OSEProgramInfo(final OSEProgramInfo info) {
        this();

        // clone data
        _files.putAll(info._files);
        _custom_data.putAll(info._custom_data);

        _namespace = info._namespace;
        _name = info._name;
        _description = info._description;
        _version = info._version;
        _author = info._author;
        _session_timeout = info._session_timeout;
        _loop_interval = info._loop_interval;
        _singleton = info._singleton;
        _autostart = info._autostart;
        _microservice = info._microservice;
        _log_level = info._log_level;
        _api_host = info._api_host;

        _installation_root = info._installation_root;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject toJson() {
        final JSONObject sb = new JSONObject();
        sb.put(FieldName.namespace.toString(), _namespace);
        sb.put(FieldName.name.toString(), _name);
        sb.put(FieldName.version.toString(), _version);
        sb.put(FieldName.description.toString(), _description);
        sb.put(FieldName.author.toString(), _author);
        sb.put(FieldName.session_timeout.toString(), _session_timeout);
        sb.put(FieldName.singleton.toString(), _singleton);
        sb.put(FieldName.autostart.toString(), _autostart);
        sb.put(FieldName.microservice.toString(), _microservice);
        sb.put(FieldName.logging.toString(), _log_level);
        sb.put(FieldName.loop_interval.toString(), _loop_interval);
        sb.put(FieldName.files.toString(), _files.size());
        sb.put(FieldName.api_host.toString(), _api_host);
        return sb;
    }

    public Map<String, Object> toMap() {
        return JsonWrapper.toMap(this.toJson());
    }

    public String uid() {
        return _namespace.concat(".").concat(_name);
    }

    public Map<File, String> files() {
        return _files;
    }

    public Map<String, Object> data() {
        return _custom_data;
    }

    public String fullName() {
        return StringUtils.replace(this.namespace() + "." + this.name(), ".", "_");
    }

    public String className() {
        return StringUtils.replace(this.namespace() + "_" + this.name(), "_", ".");
    }

    public String namespace() {
        return _namespace;
    }

    public OSEProgramInfo namespace(final String value) {
        _namespace = value;
        return this;
    }

    public String name() {
        return _name;
    }

    public OSEProgramInfo name(final String value) {
        _name = value;
        return this;
    }

    public String description() {
        return _description;
    }

    public OSEProgramInfo description(final String value) {
        _description = value;
        return this;
    }

    public String version() {
        return _version;
    }

    public OSEProgramInfo version(final String value) {
        _version = value;
        return this;
    }

    public String author() {
        return _author;
    }

    public OSEProgramInfo author(final String value) {
        _author = value;
        return this;
    }

    public String installationRoot() {
        return _installation_root;
    }

    public OSEProgramInfo installationRoot(final String value) {
        _installation_root = value;
        return this;
    }

    public int loopInterval() {
        return _loop_interval;
    }

    public OSEProgramInfo loopInterval(final int value) {
        _loop_interval = value;
        return this;
    }

    public int sessionTimeout() {
        return _session_timeout;
    }

    public OSEProgramInfo sessionTimeout(final int value) {
        _session_timeout = value;
        return this;
    }

    public boolean singleton() {
        return _singleton;
    }

    public OSEProgramInfo singleton(final boolean value) {
        _singleton = value;
        return this;
    }

    public boolean autostart() {
        return _autostart;
    }

    public OSEProgramInfo autostart(final boolean value) {
        _autostart = value;
        return this;
    }

    public boolean microservice() {
        return _microservice;
    }

    public OSEProgramInfo microservice(final boolean value) {
        _microservice = value;
        return this;
    }

    public String logLevel() {
        return _log_level;
    }

    public OSEProgramInfo logLevel(final String value) {
        _log_level = value;
        return this;
    }

    public String apiHost() {
        return _api_host;
    }

    public OSEProgramInfo apiHost(final String value) {
        _api_host = value;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _singleton = false;
        _log_level = Level.SEVERE.name();
        this.loopInterval(IConstants.LOOP_INTERVAL_MS);
        this.sessionTimeout(IConstants.SESSION_TIMEOUT_MS);
    }

}
