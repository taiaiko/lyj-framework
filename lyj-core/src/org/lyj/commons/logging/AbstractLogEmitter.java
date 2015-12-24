package org.lyj.commons.logging;

import org.lyj.commons.logging.util.LoggingUtils;

/**
 * Utility abstract class for Log Emitters (all classes needing a logger)
 *
 */
public class AbstractLogEmitter
        implements ILogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Logger _logger;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AbstractLogEmitter(){
        _logger = LoggingUtils.getLogger(this);
    }

    public AbstractLogEmitter(final Logger logger){
        _logger = logger;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    public Logger getLogger() {
        return _logger;
    }

    public void log(final Level level, final String methodName, final String message) {
        this.log(level, methodName, message, null);
    }

    public void log(final Level level, final String methodName, final String message, final Throwable t) {
        final Logger logger = this.getLogger();
        final String name = logger.getShortName().concat(".").concat(methodName);
        final String pattern = null == t ? "[%s] %s" : "[%s] %s: %s";
        final Object[] params = null == t ? new Object[]{name, message} : new Object[]{name, message, t};

        logger.log(level, pattern, params);
    }

    public void debug(final String methodName, final String message) {
        this.log(Level.FINE, methodName, message);
    }

    public void info(final String methodName, final String message) {
        this.log(Level.INFO, methodName, message);
    }

    public void error(final String methodName, final String message) {
        this.log(Level.SEVERE, methodName, message);
    }

}