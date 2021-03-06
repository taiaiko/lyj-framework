package org.lyj.ext.script;

import org.lyj.Lyj;
import org.lyj.ext.script.deploy.TestDeployer;
import org.lyj.launcher.LyjLauncher;

/**
 * TEST UNIT INITIALIZER
 */
public class TestInitializer {

    private static boolean _init = false;

    public static void init() {
        if (!_init) {
            _init = true;


            // launcher
            Launcher.main(new String[]{"-w", "USERHOME/lyj-ext-script", "-t", "true"});

        }
    }


    private static class Launcher extends LyjLauncher {

        public Launcher(final String[] args){
            super(args);
        }

        @Override
        public void ready() {

        }

        @Override
        public void shutdown() {

        }

        public static void main(final String[] args) {
            final Launcher main = new Launcher(args);

            Lyj.registerDeployer(new TestDeployer(Lyj.isSilent()));

            main.run();
        }
    }
}
