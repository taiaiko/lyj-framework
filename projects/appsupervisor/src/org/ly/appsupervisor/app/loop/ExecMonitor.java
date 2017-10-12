package org.ly.appsupervisor.app.loop;

import org.ly.appsupervisor.app.loop.operations.ActionController;
import org.ly.appsupervisor.app.loop.operations.RuleController;
import org.lyj.commons.Delegates;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.Set;

public class ExecMonitor
        extends AbstractLogEmitter {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Delegates.Callback<String> _callback_out;
    private Delegates.Callback<String> _callback_error;

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    private ExecMonitor() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void outputHandler(final Delegates.Callback<String> callback) {
        _callback_out = callback;
    }

    public Delegates.Callback<String> outputHandler() {
        return _callback_out;
    }

    public void errorHandler(final Delegates.Callback<String> callback) {
        _callback_error = callback;
    }

    public Delegates.Callback<String> errorHandler() {
        return _callback_error;
    }

    public void monitor() {
        this.monitor(null, null);
    }

    public void monitor(final Delegates.Callback<String> output,
                        final Delegates.Callback<String> error) {
        try {
            this.outputHandler(output);
            this.errorHandler(error);

            this.checkRules();
        } catch (final Exception exec_error) {
            super.error("monitor", exec_error);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void checkRules() throws Exception {
        final Set<String> actions = RuleController.instance().check();

        // run actions
        for (final String action_name : actions) {
            ActionController.instance().run(action_name);
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static ExecMonitor __instance;

    public static ExecMonitor instance() {
        if (null == __instance) {
            __instance = new ExecMonitor();
        }
        return __instance;
    }

}