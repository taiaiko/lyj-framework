package org.lyj.ext.script.program.engines.javascript.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;

public class JavascriptUtils {

    public static boolean isError(final Object item) {
        if (item instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror) item).getClassName().equalsIgnoreCase("Error");
        }
        return false;
    }

    public static boolean isUndefined(final Object item) {
        return item instanceof Undefined;
    }

}
