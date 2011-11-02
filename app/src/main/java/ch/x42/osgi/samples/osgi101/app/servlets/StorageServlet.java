package ch.x42.osgi.samples.osgi101.app.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.x42.osgi.samples.osgi101.core.CoreConstants;
import ch.x42.osgi.samples.osgi101.core.Storage;

/** Servlet that implements GET and POST access
 *  to our Storage component, mounted on /store
 *  by default, path can be changed by configuration.
 */
@SuppressWarnings("serial")
@Component(metatype=true)
@Service(value=Servlet.class)
@org.apache.felix.scr.annotations.Properties({
    @Property(name=CoreConstants.SERVLET_METHOD_PROP, value={"POST", "GET"}, propertyPrivate=true),
    @Property(name=CoreConstants.SERVLET_PATH_PROP, value="/store")
})
public class StorageServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Reference
    Storage storage;
    
    @Property(boolValue=true)
    public static final String ADD_METADATA_PROP = "add.metadata";
    private boolean addMetadata;
    
    protected void activate(ComponentContext ctx) {
        addMetadata = (Boolean)ctx.getProperties().get(ADD_METADATA_PROP);
        final String mountPath = (String)ctx.getProperties().get(CoreConstants.SERVLET_PATH_PROP);
        log.info("Activated, path={}, addMetadata={}", mountPath, addMetadata);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) 
    throws ServletException, IOException {
        final String path = getStoragePath(req);
        final Properties props = storage.get(path);
        if(props == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "Not found in store: " + path);
            return;
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        propertiesAsText(response.getWriter(), props);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) 
    throws ServletException, IOException {
        final String path = getStoragePath(req);
        final Properties props = new Properties();
        
        // If so configiured, store some internal metadata
        if(addMetadata) {
            props.put("_stored.by", getClass().getName());
            props.put("_stored.at", new Date());
            props.put("_storage.path", path);
        }
        
        // Store request body
        final InputStream is = req.getInputStream();
        if(is != null) {
            props.put("body", toString(is));
        }
            
        // Store and dump result
        storage.put(path, props);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("Stored at " + path);
        propertiesAsText(response.getWriter(), props);
    }
    
    private String getStoragePath(HttpServletRequest req) {
        return req.getPathInfo();
    }
    
    private void propertiesAsText(PrintWriter pw, Properties props) {
        final Enumeration<?> e = props.propertyNames();
        while(e.hasMoreElements()) {
            final String key = e.nextElement().toString();
            pw.print(key);
            pw.print('=');
            pw.println(props.get(key));
        }
    }
    
    private String toString(InputStream is) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final byte [] buffer = new byte[16384];
        int n = 0;
        while( (n = is.read(buffer, 0, buffer.length)) > 0) {
            // TODO should handle encoding here
            sb.append(new String(buffer, 0, n));
        }
        return sb.toString();
    }
}