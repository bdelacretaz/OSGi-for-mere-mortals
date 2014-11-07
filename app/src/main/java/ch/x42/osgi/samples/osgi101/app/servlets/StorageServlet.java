package ch.x42.osgi.samples.osgi101.app.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

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
@Component(
    metatype=true,
    label="OSGi 101 Storage Servlet",
    description="Servlet that handles POST and GET requests for the Storage service")
@Service(value=Servlet.class)
@org.apache.felix.scr.annotations.Properties({
    @Property(
        name=CoreConstants.SERVLET_METHOD_PROP, 
        value={"POST", "GET"}, propertyPrivate=true),
    @Property(
        name=CoreConstants.SERVLET_PATH_PROP,
        label="Servlet Path",
        description="Path on which the servlet is mounted",
        value="/store")
})
public class StorageServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicInteger pathCounter = new AtomicInteger();
    
    /** Property name for request body, that we handle in a special way */
    public static final String BODY = "_request_body";
    
    @Reference
    Storage storage;
    
    @Property(
        boolValue=true,
        label="Add metadata?",
        description="If true, some metadata is added before storing")
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
            props.put("StoredBy", getClass().getName());
            props.put("StoredAt", new Date());
            props.put("Path", path);
        }
        
        // Store request body
        final InputStream is = req.getInputStream();
        if(is != null) {
            props.put(BODY, toString(is));
        }
            
        // Store and dump result
        storage.put(path, props);
        response.setHeader("Location", req.getContextPath() + path);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("Stored at " + path);
        propertiesAsText(response.getWriter(), props);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
    
    private String getStoragePath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if(path.endsWith("/")) {
            path += pathCounter.incrementAndGet() + "_" + System.currentTimeMillis();
        }
        return path;
    }
    
    /** Output our properties as text, with request body last, separated
     *  by a blank line. Similar to RFC822 mail format. 
     */
    private void propertiesAsText(PrintWriter pw, Properties props) {
        final Enumeration<?> e = props.propertyNames();
        boolean hasMetadata = false;
        while(e.hasMoreElements()) {
            final String key = e.nextElement().toString();
            if(key.equals(BODY)) {
                continue;
            }
            hasMetadata = true;
            pw.print(key);
            pw.print(':');
            pw.println(props.get(key));
        }
        
        final Object body = props.get(BODY);
        if(body != null) {
            if(hasMetadata) {
                pw.println();
            }
            pw.print(body);
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