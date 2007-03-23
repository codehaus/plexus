package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractActiveMap
    implements ActiveMap, Contextualizable, LogEnabled
{

    private String role;

    private PlexusContainer container;

    private Logger logger;
    
    protected AbstractActiveMap()
    {
        // used for plexus component init.
    }
    
    protected AbstractActiveMap( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveMap.ROLE );
    }

    public void clear()
    {
        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
    }

    public Object put( Object arg0, Object arg1 )
    {
        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
    }

    public void putAll( Map arg0 )
    {
        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
    }

    public Object remove( Object key )
    {
        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
    }

    protected final Logger getLogger()
    {
        return logger;
    }
    
    protected final Map getMap()
    {
        try
        {
            return checkedGetMap();
        }
        catch ( ComponentLookupException e )
        {
            logger.debug( "Failed to lookup map for role: " + role, e );
        }

        return Collections.EMPTY_MAP;
    }

    protected final Map checkedGetMap()
        throws ComponentLookupException
    {
        return container.lookupMap( role );
    }
    
    public final String getRole()
    {
        return role;
    }
    
    protected final void setRole( String role )
    {
        this.role = role;
    }

    public final void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public final void enableLogging( Logger logger )
    {
        this.logger = logger;
    }
}
