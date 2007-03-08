package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultActiveSet
    implements ActiveSet, Contextualizable, LogEnabled
{

    // configuration.
    private String role;

    private PlexusContainer container;

    private Logger logger;

    public DefaultActiveSet( PlexusContainer container, Class role )
    {
        this( container, role.getName() );
    }

    public DefaultActiveSet( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveCollectionManager.ROLE );
    }

    public boolean contains( Object value )
    {
        return getSet().contains( value );
    }

    public boolean containsAll( Collection collection )
    {
        return getSet().containsAll( collection );
    }

    public boolean isEmpty()
    {
        return getSet().isEmpty();
    }

    public Iterator iterator()
    {
        return getSet().iterator();
    }

    public int size()
    {
        return getSet().size();
    }

    public Object[] toArray()
    {
        return getSet().toArray();
    }

    public Object[] toArray( Object[] array )
    {
        return getSet().toArray( array );
    }

    public boolean checkedContains( Object value )
    throws ComponentLookupException
    {
        return checkedGetSet().contains( value );
    }

    public boolean checkedContainsAll( Collection collection )
    throws ComponentLookupException
    {
        return checkedGetSet().containsAll( collection );
    }

    public boolean checkedIsEmpty()
    throws ComponentLookupException
    {
        return checkedGetSet().isEmpty();
    }

    public Iterator checkedIterator()
    throws ComponentLookupException
    {
        return checkedGetSet().iterator();
    }

    public int checkedSize()
    throws ComponentLookupException
    {
        return checkedGetSet().size();
    }

    public Object[] checkedToArray()
    throws ComponentLookupException
    {
        return checkedGetSet().toArray();
    }

    public Object[] checkedToArray( Object[] array )
    throws ComponentLookupException
    {
        return checkedGetSet().toArray( array );
    }

    private Set getSet()
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

    private Set checkedGetSet()
        throws ComponentLookupException
    {
        return new LinkedHashSet( container.lookupList( role ) );
    }

    public String getRole()
    {
        return role;
    }
    
    protected void setRole( String role )
    {
        this.role = role;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }
    
}
