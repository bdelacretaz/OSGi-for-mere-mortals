# OSGi for mere mortals

This is the code of my "OSGi for mere mortals" presentation at [ApacheCon](http://apachecon.com) NA 2011, EU 2012 and EU 2014.

<div style="float:right"><a href="http://events.linuxfoundation.org/events/apachecon-europe/program/schedule">
<img src="http://bit.ly/1ttg6CE"/></a></div>

It's a minimal standalone RESTful server built from scratch using OSGi Declarative Services, meant
to demonstrate that OSGi is not only for superhuman guru programmers.

This example demonstrates the complete lifecycle of an OSGi application,
from starting the framework and installing the required bundles to running
the app itself.

It also demonstrates the svelteness of the OSGi framework and core services: the total size of the build-time 
dependencies (jar files) is around 3 megabytes and additional runtime baggage amounts to ten OSGi bundles (also jar files) representing about 2 megabytes more, including all runtime features like the OSGi web console, interactive OSGi shell, the OSGi configuration mechanism and front-end and the servlet engine. Startup time is around 300 msec on my laptop.

The Maven build is also a useful example of how to create OSGi bundles in a simple way.

The slides at http://www.slideshare.net/bdelacretaz/osgi-for-mere-mortals should help you walk through the code.

## How to build and start
To build the runnable jar, run `mvn clean install` at the top of the source
code tree (using <a href="http://maven.apache.org">Apache Maven</a>
3.0.3 or later).

You can then start the server from the *launcher* subfolder by running

    java -jar target/osgi-for-mere-mortals-launcher-0.0.1-SNAPSHOT.jar

(or whatever the name of that jar file is).

The OSGi console shown in the slides is at http://localhost:8080/system/console - use admin/admin to log in.

## How to use the server
The server doesn't do much, that's not the point, the goal is just to demonstrate how it is assembled.

You can store data via HTTP POST requests:

    $ date | curl -X POST -D - http://localhost:8080/store/testing
    HTTP/1.1 201 Created
    Location: /store/testing
    Content-Type: text/plain; charset=utf-8
    Content-Length: 149
    Server: Jetty(6.1.x)
    
    Stored at /store/testing
    StoredBy:ch.x42.osgi.samples.osgi101.app.servlets.StorageServlet
    StoredAt:Fri Nov 14 17:03:36 CET 2014
    
And retrieve it via HTTP GET:

    curl http://localhost:8080/store/testing
    StoredBy:ch.x42.osgi.samples.osgi101.app.servlets.StorageServlet
    StoredAt:Fri Nov 14 17:03:36 CET 2014
    Path:/store/testing 


