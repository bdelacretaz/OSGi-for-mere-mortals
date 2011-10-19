package ch.x42.osgi.samples.osgi101.bootstrap;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartServer {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    void testBootstrap() throws Exception {
        
        final OsgiBootstrap osgi = new OsgiBootstrap();
        final Framework framework = osgi.getFramework();
        
        log.info("Framework bundle: {} ({})", framework.getSymbolicName(), framework.getState());
        osgi.installBundles(new File("target/bundles"));
        for(Bundle b : framework.getBundleContext().getBundles()) {
            log.info("Installed bundle: {} ({})", b.getSymbolicName(), b.getState());
        }
        
        osgi.waitForFrameworkAndQuit();
    }
    
    public static void main(String [] args) throws Exception {
        new StartServer().testBootstrap();
    }
}
