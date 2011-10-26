package ch.x42.osgi.samples.osgi101.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

@SuppressWarnings("serial")
@Component
@Service(value=Servlet.class)
@Property(name=DispatcherServlet.SERVLET_METHOD_PROP, value="POST")
public class SamplePostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) 
    throws ServletException, IOException {
        response.getWriter().println("This is " + getClass().getName());
        response.getWriter().flush();
    }
}