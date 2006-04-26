/* Created on Oct 7, 2004 */
package org.codehaus.plexus.embed;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author jdcasey
 */
public interface PlexusEmbedder
{

    PlexusContainer getContainer();

    Object lookup( String role ) throws ComponentLookupException;

    Object lookup( String role, String id ) throws ComponentLookupException;

    boolean hasComponent( String role );

    boolean hasComponent( String role, String id );

    void release( Object service )
        throws ComponentLifecycleException;

    void setClassLoader( ClassLoader classLoader );

    void setConfiguration( URL configuration ) throws IOException;

    void setConfiguration( Reader configuration ) throws IOException;

    void addContextValue( Object key, Object value );

    void setProperties( Properties properties );

    void setProperties( File file );

    void start( ClassLoader classLoader )
        throws PlexusContainerException, PlexusConfigurationResourceException;

    void start()
        throws PlexusContainerException, PlexusConfigurationResourceException;

    void stop();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );    
}
