package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

/**
 * @author Jason van Zyl
 */
public class CreateAppConfigurationPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        DefaultPlexusContainer serverContainer = context.getAppServerContainer();

        getLogger().info( "Using appDir = " + context.getAppDir() );

        // ----------------------------------------------------------------------------
        // Create the containers context value mappings
        // ----------------------------------------------------------------------------

        Map contextMap = new HashMap();

        Properties contextValues = context.getContext();

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

        if ( !contextMap.containsKey( "appserver.home" ) )
        {
            contextMap.put( "appserver.home", context.getAppServer().getAppServerHome().getAbsolutePath() );
        }

        if ( !contextMap.containsKey( "appserver.base" ) )
        {
            contextMap.put( "appserver.base", context.getAppServer().getAppServerBase().getAbsolutePath() );
        }

        getLogger().debug( "appserver.home = " + contextMap.get( "appserver.home" ) );
        getLogger().debug( "appserver.base = " + contextMap.get( "appserver.base" ) );

        // ----------------------------------------------------------------------------
        // Make the application's home directory available in the context
        // ----------------------------------------------------------------------------
        contextMap.put( "plexus.home", context.getAppDir().getAbsolutePath() );

        contextMap.put( "app.home", context.getAppDir().getAbsolutePath() );

        // ----------------------------------------------------------------------------
        // Make the user's home directory available in the context
        // ----------------------------------------------------------------------------
        //noinspection AccessOfSystemProperties
        contextMap.put( "user.home", System.getProperty( "user.home" ) );

        Object appserver = null;

        try
        {
            appserver = serverContainer.getContext().get( "plexus.appserver" );
        }
        catch ( ContextException e )
        {
            // won't happen.
        }

        contextMap.put( "plexus.appserver", appserver );

        context.setContextValues( contextMap );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Xpp3Dom dom = null;

        try
        {
            //noinspection IOResourceOpenedButNotSafelyClosed
            Reader configurationReader =
                new InterpolationFilterReader( new FileReader( context.getAppConfigurationFile() ), contextMap );

            dom = Xpp3DomBuilder.build( configurationReader );
        }
        catch ( FileNotFoundException e )
        {
            // we skipped this once already, ignore it this time.
            // Would be better to preload it instead.
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( "Error processing application configurator.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new AppDeploymentException( "Error processing application configurator.", e );
        }

        if ( dom != null )
        {
            PlexusConfiguration applicationConfiguration = new XmlPlexusConfiguration( dom );

            context.setAppConfiguration( applicationConfiguration );
        }
    }
}
