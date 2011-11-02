package ch.x42.osgi.samples.osgi101.core.impl;

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

/** Default POST servlet mounted without a path,
 *  will catch requests for which there's 
 *  no POST servlet with a specific path.  
 */
@SuppressWarnings("serial")
@Component
@Service(value=Servlet.class)
@Property(name=CoreConstants.SERVLET_METHOD_PROP, value="POST")
public class DefaultPostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) 
    throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                "No specific POST servlet found to process this request");
    }
}