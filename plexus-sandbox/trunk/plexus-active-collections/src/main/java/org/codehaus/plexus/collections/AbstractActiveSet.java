package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractActiveSet
    implements ActiveSet
{

    // configuration.
    private String role;

    private PlexusContainer container;

    private Logger logger;

    protected AbstractActiveSet()
    {
        // used for plexus init.
    }

    protected AbstractActiveSet( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveSet.ROLE );
    }
    
    public boolean add( Object arg0 )
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    public boolean addAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    public void clear()
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    public boolean remove( Object o )
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    public boolean removeAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    public boolean retainAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveSet implementations are not mutable." );
    }

    protected final Logger getLogger()
    {
        return logger;
    }
    
    protected final Set getSet()
    {
        try
        {
            return checkedGetSet();
        }
        catch ( ComponentLookupException e )
        {
            logger.debug( "Failed to lookup map for role: " + role, e );
        }

        return Collections.EMPTY_SET;
    }

    protected final Set checkedGetSet()
        throws ComponentLookupException
    {
        return new LinkedHashSet( container.lookupList( role ) );
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
