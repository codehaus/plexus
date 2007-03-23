package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractActiveList
    implements ActiveList
{

    private String role;

    private PlexusContainer container;

    private Logger logger;

    protected AbstractActiveList()
    {
        // used for plexus init.
    }

    protected AbstractActiveList( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveList.ROLE );
    }

    public boolean add( Object arg0 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public void add( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public boolean addAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public boolean addAll( int arg0, Collection arg1 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public void clear()
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public boolean remove( Object o )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public Object remove( int index )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public boolean removeAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public boolean retainAll( Collection arg0 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    public Object set( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    }

    protected final List checkedGetList()
        throws ComponentLookupException
    {
        return container.lookupList( role );
    }

    protected final List getList()
    {
        try
        {
            return checkedGetList();
        }
        catch ( ComponentLookupException e )
        {
            logger.debug( "Failed to lookup map for role: " + role, e );
        }

        return Collections.EMPTY_LIST;
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
    
    protected final Logger getLogger()
    {
        return logger;
    }

    public final void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

}
