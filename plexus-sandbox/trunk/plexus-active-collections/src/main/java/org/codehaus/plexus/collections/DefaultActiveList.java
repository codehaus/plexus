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
import java.util.List;
import java.util.ListIterator;

public class DefaultActiveList
    implements ActiveList, Contextualizable, LogEnabled
{
    
    private String role;

    private PlexusContainer container;

    private Logger logger;

    public DefaultActiveList( PlexusContainer container, Class role )
    {
        this( container, role.getName() );
    }

    public DefaultActiveList( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveCollectionManager.ROLE );
    }

    public boolean checkedContains( Object value )
    throws ComponentLookupException
    {
        return checkedGetList().contains( value );
    }

    public boolean checkedContainsAll( Collection collection )
    throws ComponentLookupException
    {
        return checkedGetList().containsAll( collection );
    }

    public Object checkedGet( int index )
    throws ComponentLookupException
    {
        return checkedGetList().get( index );
    }

    public int checkedIndexOf( Object value )
    throws ComponentLookupException
    {
        return checkedGetList().indexOf( value );
    }

    public boolean checkedIsEmpty()
    throws ComponentLookupException
    {
        return checkedGetList().isEmpty();
    }

    public Iterator checkedIterator()
    throws ComponentLookupException
    {
        return checkedGetList().iterator();
    }

    public int checkedLastIndexOf( Object value )
    throws ComponentLookupException
    {
        return checkedGetList().lastIndexOf( value );
    }

    public ListIterator checkedListIterator()
    throws ComponentLookupException
    {
        return checkedGetList().listIterator();
    }

    public ListIterator checkedListIterator( int index )
    throws ComponentLookupException
    {
        return checkedGetList().listIterator( index );
    }

    public int checkedSize()
    throws ComponentLookupException
    {
        return checkedGetList().size();
    }

    public List checkedSubList( int fromIndex, int toIndex )
    throws ComponentLookupException
    {
        return checkedGetList().subList( fromIndex, toIndex );
    }

    public Object[] checkedToArray()
    throws ComponentLookupException
    {
        return checkedGetList().toArray();
    }

    public Object[] checkedToArray( Object[] array )
    throws ComponentLookupException
    {
        return checkedGetList().toArray( array );
    }

    public boolean contains( Object value )
    {
        return getList().contains( value );
    }

    public boolean containsAll( Collection collection )
    {
        return getList().containsAll( collection );
    }

    public Object get( int index )
    {
        return getList().get( index );
    }

    public int indexOf( Object value )
    {
        return getList().indexOf( value );
    }

    public boolean isEmpty()
    {
        return getList().isEmpty();
    }

    public Iterator iterator()
    {
        return getList().iterator();
    }

    public int lastIndexOf( Object value )
    {
        return getList().lastIndexOf( value );
    }

    public ListIterator listIterator()
    {
        return getList().listIterator();
    }

    public ListIterator listIterator( int index )
    {
        return getList().listIterator( index );
    }

    public int size()
    {
        return getList().size();
    }

    public List subList( int fromIndex, int toIndex )
    {
        return getList().subList( fromIndex, toIndex );
    }

    public Object[] toArray()
    {
        return getList().toArray();
    }

    public Object[] toArray( Object[] array )
    {
        return getList().toArray( array );
    }

    List checkedGetList()
        throws ComponentLookupException
    {
        return container.lookupList( role );
    }

    List getList()
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
