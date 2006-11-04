package org.codehaus.plexus.tomcat;

import org.apache.catalina.Logger;
import org.apache.catalina.Container;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusCatalinaLogger
    implements Logger
{
    private org.codehaus.plexus.logging.Logger plexusLogger;

    private Container container;

    private List listeners = new ArrayList();

    public PlexusCatalinaLogger( org.codehaus.plexus.logging.Logger plexusLogger )
    {
        this.plexusLogger = plexusLogger;
    }

    public Container getContainer()
    {
        return container;
    }

    public void setContainer( Container container )
    {
        this.container = container;
    }

    public String getInfo()
    {
        return null;
    }

    public int getVerbosity()
    {
        return 0; // This _can_ be translated, but who cares?
    }

    public void setVerbosity( int i )
    {
        // ignored
    }

    public void addPropertyChangeListener( PropertyChangeListener propertyChangeListener )
    {
        listeners.add( propertyChangeListener );
    }

    public void log( String string )
    {
        plexusLogger.info( string );
    }

    public void log( Exception exception, String string )
    {
        plexusLogger.info( string, exception );
    }

    public void log( String string, Throwable throwable )
    {
        plexusLogger.info( string, throwable );
    }

    public void log( String string, int i )
    {
        plexusLogger.info( string );
    }

    public void log( String string, Throwable throwable, int i )
    {
        plexusLogger.info( string, throwable );
    }

    public void removePropertyChangeListener( PropertyChangeListener propertyChangeListener )
    {
        listeners.remove( propertyChangeListener );
    }
}
