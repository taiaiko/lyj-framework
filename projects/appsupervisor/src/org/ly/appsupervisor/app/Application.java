package org.ly.appsupervisor.app;

import org.ly.appsupervisor.app.loop.MainLoop;
import org.ly.appsupervisor.app.loop.installer.controllers.FileController;
import org.ly.appsupervisor.app.model.ModelLauncher;
import org.ly.appsupervisor.deploy.config.ConfigHelper;
import org.lyj.Lyj;
import org.lyj.commons.io.jsonrepository.JsonRepository;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.LocaleUtils;

import java.util.Locale;
import java.util.Map;

/**
 * Application Server
 */
public class Application {


    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    public static final String VERSION = IConstants.APP_VERSION;


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private boolean _test_mode;
    private final JsonRepository _config;

    private MainLoop _main_loop;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Application(final boolean test_mode) {

        LocaleUtils.setCurrent(Locale.ENGLISH);

        _test_mode = test_mode;
        _config = Lyj.getConfiguration();

        this.getLogger().info("STARTING APP VERSION ".concat(VERSION));
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.stop();
        } finally {
            super.finalize();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public void start() throws Exception {
        this.init();
        this.logConfiguration();
    }

    public void stop() {
        if (null != _main_loop) {
            _main_loop.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        try {

            if (!_test_mode) {
                // SCHEDULED JOBS
                if (ConfigHelper.instance().taskEnabled()) {
                    this.runMainLoop();
                }
            }

        } catch (Throwable t) {
            this.getLogger().error("Error initializing server", t);
        }
    }

    private void runMainLoop() {
        _main_loop = new MainLoop(ConfigHelper.instance().taskInterval());
        _main_loop.open();
    }

    private void logConfiguration() {
        final ConfigHelper config = ConfigHelper.instance();

        final StringBuilder sb = new StringBuilder();
        sb.append("CONFIGURATION:\n");
        sb.append("**************************************").append("\n");
        sb.append("\t").append("VERSION: ").append(IConstants.APP_VERSION).append("\n");
        sb.append("\t").append("MAIN LOOP ENABLED: ").append(config.taskEnabled()).append("\n");
        sb.append("\t").append("MAIN LOOP INTERVAL: ").append(config.taskInterval()).append(" sec.").append("\n");

        // launchers
        final Map<String, ModelLauncher> launchers = config.launchers();
        sb.append("\t").append("EXEC: ").append("\n");
        launchers.forEach((uid, launcher)->{
            sb.append("\t").append("\t").append(launcher.exec()).append("\n");
        });

        sb.append("\t").append("INSTALL PATH: ").append(FileController.ROOT).append("\n");
        sb.append("**************************************");

        this.getLogger().info(sb.toString());
    }

}
