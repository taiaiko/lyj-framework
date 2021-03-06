package org.lyj.ext.script.program.engines.javascript;

import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.ext.script.program.Program;
import org.lyj.ext.script.program.engines.AbstractEngine;
import org.lyj.ext.script.program.exceptions.ScriptEvalException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * javascript wrapper
 * <p>
 * <p>
 * http://www.oracle.com/technetwork/articles/java/jf14-nashorn-2126515.html
 * <p>
 * http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial/
 */
public class EngineJavascript
        extends AbstractEngine {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String EXTENSION_FILE = "extend.js";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ScriptEngine _engine;
    private boolean _initialized;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EngineJavascript(final Program program) {
        super(program);
        _engine = engine();

        _engine.getContext().setErrorWriter(program.logger());
        _engine.getContext().setWriter(program.logger());
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void close() {

    }

    @Override
    public void addRuntimeAttribute(final String key, Object value) {
        _engine.getContext().setAttribute(key, value, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public Object getRuntimeAttribute(final String key) {
        return _engine.getContext().getAttribute(key);
    }

    @Override
    public Object eval(final String script) throws Exception {
        this.mergeContext(null);
        this.init();
        return _engine.eval(script);
    }

    @Override
    public Object eval(String script, final Map<String, Object> context) throws ScriptEvalException {
        try {
            this.mergeContext(context);
            this.init();
            return _engine.eval(script);
        } catch (Exception t) {
            throw new ScriptEvalException(t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        synchronized (this) {
            if (!_initialized) {
                try {
                    // eval extended scripts
                    final String extension = super.loadResource(EXTENSION_FILE);
                    if (StringUtils.hasText(extension)) {
                        _engine.eval(extension);
                    } else {
                        super.program().logger().warn("[ScriptEngine.init]", "EXTENSION 'ly' not loaded!");
                    }
                    _initialized = true;
                } catch (Throwable t) {
                    super.program().logger().error("[ScriptEngine.init]", t);
                }
            }
        }
    }

    private ScriptContext mergeContext(final Map<String, Object> context) {
        final ScriptContext result = _engine.getContext();

        // merge with global context
        CollectionUtils.forEach(super.context(), (value, index, key) -> {
            result.setAttribute((String) key, value, ScriptContext.ENGINE_SCOPE);
        });

        // merge with internal
        if (null != context) {
            CollectionUtils.forEach(context, (value, index, key) -> {
                result.setAttribute((String) key, value, ScriptContext.ENGINE_SCOPE);
            });
        }

        return result;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ScriptEngine __engine;

    public static Object evalScript(final String script) throws ScriptException {
        return engine().eval(script);
    }

    public static ScriptEngine engine() {
        return new ScriptEngineManager().getEngineByName("nashorn");
    }

    public static ScriptEngine engine(final boolean singleton) {
        if (!singleton) {
            return engine();
        }
        if (null == __engine) {
            __engine = engine();
        }
        return __engine;
    }

}
