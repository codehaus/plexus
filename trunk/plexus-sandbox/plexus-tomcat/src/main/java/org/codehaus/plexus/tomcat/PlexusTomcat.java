package org.codehaus.plexus.tomcat;

import org.apache.catalina.Connector;
import org.apache.catalina.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Host;
import org.apache.catalina.Engine;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
*/
public interface PlexusTomcat
{
    String ROLE = PlexusTomcat.class.getName();

    void setCatalinaHome( File catalinaHome );

    void setContextRoot( File contextRoot );

    void setLogger( Logger logger );

    void setDefaultHost( String host );

    Engine getEngine();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    Host addHost( String hostName, String webapps );

    Connector addConnector( String host, int port, boolean secure );

    Context addContext( String host, String contextName, String contextPath, File path );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void startTomcat()
        throws LifecycleException;

    void stopTomcat()
        throws LifecycleException;
}
