# OSGi for mere mortals

This is the code of my "OSGi for mere mortals" presentation at
ApacheCon NA 2011, http://na11.apachecon.com/talks/19416 and
ApacheCon EU 2012, http://www.apachecon.eu/schedule/presentation/98/ - a small
standalone RESTful server built from scratch using OSGi 
Declarative Services.

The slides at http://www.slideshare.net/bdelacretaz/osgi-for-mere-mortals 
should help you walk through the code.

The example demonstrates the complete lifecycle of an OSGi application,
from starting the framework and installing the required bundles to running
the app itself.

The Maven build is also a useful example of how to create OSGi bundles in a 
simple way.

To build the runnable jar, run `mvn clean install` at the top of the source
code tree (using <a href="http://maven.apache.org">Apache Maven</a>
3.0.3 or later).

You can then start the server from the *launcher* subfolder by running

    java -jar target/osgi-for-mere-mortals-launcher-0.0.1-SNAPSHOT.jar

(or whatever the name of that jar file is).

The OSGi console shown in the slides is at http://localhost:8080/system/console
