package ch.x42.osgi.samples.osgi101.app.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import ch.x42.osgi.samples.osgi101.core.CoreConstants;

/** Sample GET servlet mounted on /, will catch
 *  requests for which there's no GET servlet
 *  with a more specific path.  
 */
@SuppressWarnings("serial")
@Component
@Service(value=Servlet.class)
@Property(name=CoreConstants.SERVLET_METHOD_PROP, value="GET")
public class SampleGetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) 
    throws ServletException, IOException {
        response.getWriter().println("This is " + getClass().getName());
        response.getWriter().flush();
    }
}