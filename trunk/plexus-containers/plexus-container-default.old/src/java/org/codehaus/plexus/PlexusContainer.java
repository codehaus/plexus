package org.codehaus.plexus;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;

import java.io.Reader;
import java.util.Map;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String role, String id )
        throws ComponentLookupException;

    Map lookupAll( String role )
        throws ComponentLookupException;

    void releaseAll( Map components );

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String id );

    void release( Object component );

    void suspend( Object component );

    void resume( Object component );

    void addContextValue( Object key, Object value );

    void setClassWorld( ClassWorld classWorld );

    void setClassLoader( ClassLoader classLoader );

    void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException;

    ClassLoader getClassLoader();

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void dispose()
        throws Exception;
}
