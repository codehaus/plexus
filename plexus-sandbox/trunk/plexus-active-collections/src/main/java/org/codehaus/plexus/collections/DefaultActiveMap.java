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
import java.util.Map;
import java.util.Set;

public class DefaultActiveMap
    implements ActiveMap, Contextualizable, LogEnabled
{

    private String role;

    private PlexusContainer container;

    private Logger logger;

    public DefaultActiveMap( PlexusContainer container, Class role )
    {
        this( container, role.getName() );
    }

    public DefaultActiveMap( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
        this.logger = container.getLoggerManager().getLoggerForComponent( ActiveCollectionManager.ROLE );
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

    private Map getMap()
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

    private Map checkedGetMap()
        throws ComponentLookupException
    {
        return container.lookupMap( role );
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
