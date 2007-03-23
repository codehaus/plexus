package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Collection;
import java.util.Set;

public class DefaultActiveMap
    extends AbstractActiveMap
    implements ActiveMap, Contextualizable, LogEnabled
{

    public DefaultActiveMap()
    {
        //used for plexus init.
    }

    public DefaultActiveMap( PlexusContainer container, Class role )
    {
        super( container, role.getName() );
    }

    public DefaultActiveMap( PlexusContainer container, String role )
    {
        super( container, role );
    }

    public boolean containsKey( Object key )
    {
        return getMap().containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return getMap().containsValue( value );
    }

    public Set entrySet()
    {
        return getMap().entrySet();
    }

    public Object get( Object key )
    {
        return getMap().get( key );
    }

    public boolean isEmpty()
    {
        return getMap().isEmpty();
    }

    public Set keySet()
    {
        return getMap().keySet();
    }

    public int size()
    {
        return getMap().size();
    }

    public Collection values()
    {
        return getMap().values();
    }

    public boolean checkedContainsKey( Object key )
        throws ComponentLookupException
    {
        return checkedGetMap().containsKey( key );
    }

    public boolean checkedContainsValue( Object value )
        throws ComponentLookupException
    {
        return checkedGetMap().containsValue( value );
    }

    public Set checkedEntrySet()
        throws ComponentLookupException
    {
        return checkedGetMap().entrySet();
    }

    public Object checkedGet( Object key )
        throws ComponentLookupException
    {
        return checkedGetMap().get( key );
    }

    public boolean checkedIsEmpty()
        throws ComponentLookupException
    {
        return checkedGetMap().isEmpty();
    }

    public Set checkedKeySet()
        throws ComponentLookupException
    {
        return checkedGetMap().keySet();
    }

    public int checkedSize()
        throws ComponentLookupException
    {
        return checkedGetMap().size();
    }

    public Collection checkedValues()
        throws ComponentLookupException
    {
        return checkedGetMap().values();
    }

}
