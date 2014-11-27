package ch.x42.osgi.samples.osgi101.bootstrap;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bootstrap the OSGi framework, based on Neil Bartlett's 
 *  http://njbartlett.name/2011/03/07/embedding-osgi.html
 *  tutorial.
 *  Installs and starts all bundles found in a folder specified by 
 *  the "additional.bundles.path" system property.
 */
class OsgiBootstrap {
    private static final Logger log = LoggerFactory.getLogger(OsgiBootstrap.class);
    private final Framework framework;
    private static final AtomicLong idCounter = new AtomicLong(System.currentTimeMillis()); 
    
    private static final String ADD_BUNDLES_FOLDER = System.getProperty("additional.bundles.path","target/bundles");
    
    public OsgiBootstrap() throws BundleException {
        this(new HashMap<String, String>());
    }
    
    OsgiBootstrap(Map<String, String> config) throws BundleException {
        FrameworkFactory frameworkFactory = java.util.ServiceLoader.load(FrameworkFactory.class).iterator().next();
        framework = frameworkFactory.newFramework(config);
        framework.start();
        log.info("OSGi framework started");
    }
    
    void installBundles(File fromFolder) throws BundleException {
        final String[] files = fromFolder.list();
        if(files == null) {
            log.warn("No bundles found in {}", fromFolder.getAbsolutePath());
            return;
        }
        
        log.info("Installing bundles from {}", fromFolder.getAbsolutePath());
        final List<Bundle> installed = new LinkedList<Bundle>();
        final BundleContext ctx = framework.getBundleContext();
        for(String filename : files) {
            if(filename.endsWith(".jar")) {
                final File f = new File(fromFolder, filename);
                final String ref = "file:" + f.getAbsolutePath();
                log.info("Installing bundle {}", ref);
                installed.add(ctx.installBundle(ref));
            }
        }
        
        for (Bundle bundle : installed) {
            log.info("Starting bundle {}", bundle.getSymbolicName());
            bundle.start();
        }
        
        log.info("{} bundles installed from {}", installed.size(), fromFolder.getAbsolutePath());
    }
    
    void waitForFrameworkAndQuit() throws Exception {
        try {
            framework.waitForStop(0);
        } finally {
            log.info("OSGi framework stopped, exiting");
            System.exit(0);
        }        
    }
    
    Framework getFramework() {
        return framework;
    }
    
    public static void main(String [] args) throws Exception {
        
        int nInstances = 1;
        if(args.length > 0) {
            nInstances = Integer.valueOf(args[0]);
        }
        final int firstPort = 8080;
        final OsgiBootstrap [] ob = new OsgiBootstrap[nInstances]; 
        
        log.info("Starting {} instances of the OSGi framework", nInstances);
        
        for(int i=0; i < nInstances; i++) {
            final int port = firstPort + i;
            final Map<String, String> config = new HashMap<String, String>();
            config.put("org.osgi.framework.storage", "felix-cache/instance-" + idCounter.incrementAndGet());
            config.put("org.osgi.service.http.port", String.valueOf(port));
            ob[i] = new OsgiBootstrap(config);
            final Framework framework = ob[i].getFramework();
            
            log.info("Framework started on port {}, bundle: {} ({})", 
                    new Object[]{ port, framework.getSymbolicName(), framework.getState()});
            log.info("Looking for additional bundles under {}", ADD_BUNDLES_FOLDER);
            ob[i].installBundles(new File(ADD_BUNDLES_FOLDER));
            for(Bundle b : framework.getBundleContext().getBundles()) {
                log.info("Installed bundle: {} ({})", b.getSymbolicName(), b.getState());
            }
        }
        
        for(OsgiBootstrap b : ob) {
            log.info("Waiting for {}", b.getFramework());
            b.waitForFrameworkAndQuit();
        }
    }
}