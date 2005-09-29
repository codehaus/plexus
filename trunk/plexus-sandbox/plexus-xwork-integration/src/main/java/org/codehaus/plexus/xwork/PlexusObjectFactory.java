package org.codehaus.plexus.xwork;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusObjectFactory
    extends ObjectFactory
    implements Initializable, Contextualizable
{
    private PlexusContainer container;

    private boolean chainObjectFactory = true;

    private ObjectFactory objectFactory;

    // ----------------------------------------------------------------------
    // ObjectFactory overrides
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        objectFactory = ObjectFactory.getObjectFactory();
    }

    public Action buildAction( ActionConfig config )
        throws Exception
    {
        String roleHint = config.getClassName();

        try
        {
            return (Action) container.lookup( Action.class.getName(), roleHint );
        }
        catch ( ComponentLookupException e )
        {
            if ( chainObjectFactory && objectFactory != null )
            {
                return objectFactory.buildAction( config );
            }

            throw e;
        }
    }

    // ----------------------------------------------------------------------
    // Plexus Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
