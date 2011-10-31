package ch.x42.osgi.samples.osgi101.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.x42.osgi.samples.osgi101.core.CoreConstants;

@SuppressWarnings("serial")
@Component(immediate=true)
public class DispatcherServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    // TODO make this configurable
    public static final String MOUNT_PATH = "/";
    
    @Reference
    private HttpService httpService;
    
    /** Keep track of Servlets registered as OSGi services,
     *  so that we can dispatch requests to them.
     */
    private ServiceTracker servletServicesTracker;
    
    private BundleContext bundleContext;

    @Activate
    protected void activate(ComponentContext ctx) throws ServletException, NamespaceException {
        bundleContext = ctx.getBundleContext();
        httpService.registerServlet(MOUNT_PATH, this, null, null);
        servletServicesTracker = new ServiceTracker(ctx.getBundleContext(), Servlet.class.getName(), null);
        servletServicesTracker.open();
        log.info("{} registered at {}", getClass().getSimpleName(), MOUNT_PATH);
    }
    
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        httpService.unregister(MOUNT_PATH);
        servletServicesTracker.close();
        servletServicesTracker = null;
        bundleContext = null;
        log.info("{} unregistered from {}", getClass().getSimpleName(), MOUNT_PATH);
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {
        // Select a servlet to dispatch to, based on its service properties and
        // keeping the one that has the longest path match if there are several
        // TODO might implement some caching, not needed for this simple example
        final String method = req.getMethod();
        final String path = req.getPathInfo();
        ServiceReference selectedService = null;
        for(ServiceReference  ref : servletServicesTracker.getServiceReferences()) {
            final List<String> serviceMethods = getServiceMethods(ref);
            final String servicePath = (String)ref.getProperty(CoreConstants.SERVLET_PATH_PROP);
            if(serviceMethods.contains(method) && (servicePath == null || path.startsWith(servicePath))) {
                if(selectedService == null) {
                    selectedService = ref;
                } else {
                    final String currentServicePath = (String)selectedService.getProperty(CoreConstants.SERVLET_PATH_PROP);
                    final int currentLength = (currentServicePath == null ? 0 : currentServicePath.length());
                    final int newLength = (servicePath == null ? 0 : servicePath.length());
                    if(newLength > currentLength) {
                        log.debug("Overriding service {} with {} as former has a longer path", selectedService, ref);
                        selectedService = ref;
                    }
                }
            }
        }
        
        if(selectedService == null) {
            throw new ServletException("No Servlet service found to handle method " + method + " and path " + path);
        }
        
        final Servlet servlet = (Servlet)bundleContext.getService(selectedService);
        log.debug("Dispatching to {}", servlet);
        servlet.service(req, resp);
    }
    
    private List<String> getServiceMethods(ServiceReference ref) {
        final List<String> result = new ArrayList<String>();
        final Object o = ref.getProperty(CoreConstants.SERVLET_METHOD_PROP);
        if(o instanceof String) {
            result.add((String)o);
        } else if (o instanceof String[]) {
            for(String str : (String[])o) {
                result.add(str);
            }
        } else {
            throw new IllegalStateException("Invalid type " + o.getClass().getName() 
                    + " for " + CoreConstants.SERVLET_METHOD_PROP);
        }
        return result;
    }
}
