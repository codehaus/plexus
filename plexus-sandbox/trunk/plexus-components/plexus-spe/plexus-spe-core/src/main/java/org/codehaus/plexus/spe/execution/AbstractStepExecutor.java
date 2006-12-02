package org.codehaus.plexus.spe.execution;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.List;
import java.util.Collections;
import java.util.Collection;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractStepExecutor
    extends AbstractLogEnabled
{
    /**
     * @plexus.requirement
     */
    private PlexusContainer container;

    protected static String getChild( Xpp3Dom configuration, String key )
        throws ProcessException
    {
        if ( configuration == null || configuration.getChild( key ) == null ||
            StringUtils.isEmpty( configuration.getChild( key ).getValue() ) )
        {
            throw new ProcessException( "Invalid configuration: Missing '" + key + "'." );
        }

        return configuration.getChild( key ).getValue();
    }

    protected static String getChild( Xpp3Dom configuration, String key, String defaultValue )
        throws ProcessException
    {
        if ( configuration == null || configuration.getChild( key ) == null ||
            StringUtils.isEmpty( configuration.getChild( key ).getValue() ) )
        {
            return defaultValue;
        }

        return configuration.getChild( key ).getValue();
    }

    protected ClassRealm getContainerRealm()
    {
        return container.getContainerRealm();
    }

    protected Object lookup( String role )
        throws ProcessException
    {
        try
        {
            return container.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ProcessException( "Could not look up component with role '" + role + "'.", e );
        }
    }

    protected Object lookup( String role, String roleHint )
        throws ProcessException
    {
        try
        {
            return container.lookup( role, roleHint );
        }
        catch ( ComponentLookupException e )
        {
            throw new ProcessException(
                "Could not look up component with role '" + role + "' and role hint '" + roleHint + "'.", e );
        }
    }

    protected void release( Object component )
    {
        try
        {
            container.release( component );
        }
        catch ( ComponentLifecycleException e )
        {
            getLogger().error( "Error while releasing ", e );
        }
    }

    protected void release( Collection<Object> components )
    {
        for ( Object component : components )
        {
            release( component );
        }
    }
}
