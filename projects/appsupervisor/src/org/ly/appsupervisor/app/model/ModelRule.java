package org.ly.appsupervisor.app.model;

import org.lyj.commons.util.json.JsonItem;

public class ModelRule
        extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    private static final String FLD_ENABLED = "enabled";
    private static final String FLD_TYPE = "type";
    private static final String FLD_MU = "mu";
    private static final String FLD_LOWER_THAN = "lower_than";
    private static final String FLD_GREATER_THAN = "greater_than";
    private static final String FLD_EXPRESSION = "expression";
    private static final String FLD_HOST = "host";
    private static final String FLD_TIMEOUT_SEC = "timeout_sec";
    private static final String FLD_ACTION = "action";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelRule() {
        super();
    }

    public ModelRule(final Object item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean enabled() {
        return super.getBoolean(FLD_ENABLED);
    }

    public ModelRule enabled(final boolean value) {
        this.put(FLD_ENABLED, value);
        return this;
    }

    public String type() {
        return super.getString(FLD_TYPE);
    }

    public String mu() {
        return super.getString(FLD_MU);
    }

    public String lowerThan() {
        return super.getString(FLD_LOWER_THAN);
    }

    public String greaterThan() {
        return super.getString(FLD_GREATER_THAN);
    }

    public String expression() {
        return super.getString(FLD_EXPRESSION);
    }

    public String host() {
        return super.getString(FLD_HOST);
    }

    public int timeout() {
        return super.getInt(FLD_TIMEOUT_SEC);
    }

    public String action() {
        return super.getString(FLD_ACTION);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
