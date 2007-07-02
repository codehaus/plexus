package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.ApplicationServerConstants;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

/**
 * @author Jason van Zyl
 * @author Andrew Williams
 */
public class CreateAppContextPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {

        DefaultPlexusContainer serverContainer = context.getAppServerContainer();

        // ----------------------------------------------------------------------------
        // Create the containers context value mappings
        // ----------------------------------------------------------------------------

        Map contextMap = new HashMap();

        // need of values from container context to be interpolated
        Context containerContext = serverContainer.getContext();
        contextMap.putAll( containerContext.getContextData() );

        // some will be override with the following code

        Properties contextValues = context.getContext();

        // XXX This code never executes since the context is always empty.
        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String contextName = (String) i.next();

                contextMap.put( contextName, contextValues.getProperty( contextName ) );
            }
        }

        // ----------------------------------------------------------------------
        // We want to set ${app.home} and we want to create a new realm for the
        // appserver. Need to think about how to really separate the apps
        // from the parent container.
        // ----------------------------------------------------------------------

        if ( !contextMap.containsKey( ApplicationServerConstants.APP_SERVER_HOME_KEY ) )
        {
            contextMap.put( ApplicationServerConstants.APP_SERVER_HOME_KEY, context.getAppServer().getAppServerHome()
                .getAbsolutePath() );
        }

        if ( !contextMap.containsKey( ApplicationServerConstants.APP_SERVER_BASE_KEY ) )
        {
            contextMap.put( ApplicationServerConstants.APP_SERVER_BASE_KEY, context.getAppServer().getAppServerBase()
                .getAbsolutePath() );
        }

        getLogger().debug( "appserver.home = " + contextMap.get( ApplicationServerConstants.APP_SERVER_HOME_KEY ) );
        getLogger().debug( "appserver.base = " + contextMap.get( ApplicationServerConstants.APP_SERVER_BASE_KEY ) );

        // ----------------------------------------------------------------------------
        // Make the user's home directory available in the context
        // ----------------------------------------------------------------------------
        //noinspection AccessOfSystemProperties
        contextMap.put( "user.home", System.getProperty( "user.home" ) );

        Object appserver = null;

        try
        {
            appserver = serverContainer.getContext().get( ApplicationServerConstants.APP_SERVER_CONTEXT_KEY );
        }
        catch ( ContextException e )
        {
            // won't happen.
        }

        contextMap.put( ApplicationServerConstants.APP_SERVER_CONTEXT_KEY, appserver );

        context.setContextValues( contextMap );
    }
}
