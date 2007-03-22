package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Collection;
import java.util.Iterator;

public class DefaultActiveSet
    extends AbstractActiveSet
    implements ActiveSet, Contextualizable, LogEnabled
{

    public DefaultActiveSet()
    {
        // used for plexus init.
    }

    public DefaultActiveSet( PlexusContainer container, Class role )
    {
        super( container, role.getName() );
    }

    public DefaultActiveSet( PlexusContainer container, String role )
    {
        super( container, role );
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

}
