package ch.x42.osgi.samples.osgi101.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Component(immediate=true)
public class DispatcherServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    // TODO make this configurable
    public static final String MOUNT_PATH = "/";
    
    @Reference
    private HttpService httpService;

    @Activate
    protected void activate(ComponentContext ctx) throws ServletException, NamespaceException {
        httpService.registerServlet(MOUNT_PATH, this, null, null);
        log.info("{} registered at {}", getClass().getSimpleName(), MOUNT_PATH);
    }
    
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        httpService.unregister(MOUNT_PATH);
        log.info("{} unregistered from {}", getClass().getSimpleName(), MOUNT_PATH);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write("This is " + getClass().getName());
        resp.getWriter().flush();
    }
}
