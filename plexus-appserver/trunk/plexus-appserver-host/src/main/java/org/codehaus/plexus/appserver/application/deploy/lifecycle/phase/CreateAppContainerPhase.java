package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
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

/**
 * @author Jason van Zyl
 */
public class CreateAppContainerPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        DefaultPlexusContainer serverContainer = context.getAppServerContainer();

        String name = "plexus.application." + context.getApplicationId();

        getLogger().info( "Using appDir = " + context.getAppDir() );

        DefaultPlexusContainer applicationContainer;

        try
        {
            applicationContainer = new DefaultPlexusContainer( name, null, context.getAppConfigurationFile().getPath(),
                                                               serverContainer.getClassWorld()/*, serverContainer*/, false );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }

        Properties contextValues = context.getContext();

        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String contextName = (String) i.next();

                applicationContainer.addContextValue( contextName, contextValues.getProperty( contextName ) );
            }
        }

        // ----------------------------------------------------------------------
        // We want to set ${app.home} and we want to create a new realm for the
        // appserver. Need to think about how to really separate the apps
        // from the parent container.
        // ----------------------------------------------------------------------

        try
        {
            if ( !applicationContainer.getContext().contains( "appserver.home" ) )
            {
                applicationContainer.addContextValue( "appserver.home",
                                                      context.getAppServer().getAppServerHome().getAbsolutePath() );
            }

            if ( !applicationContainer.getContext().contains( "appserver.base" ) )
            {
                applicationContainer.addContextValue( "appserver.base",
                                                      context.getAppServer().getAppServerBase().getAbsolutePath() );
            }

            getLogger().debug( "appserver.home = " + applicationContainer.getContext().get( "appserver.home" ) );
            getLogger().debug( "appserver.base = " + applicationContainer.getContext().get( "appserver.base" ) );
        }
        catch ( ContextException e )
        {
            // Won't happen
        }

        // ----------------------------------------------------------------------------
        // Make the application's home directory available in the context
        // ----------------------------------------------------------------------------
        applicationContainer.addContextValue( "plexus.home", context.getAppDir().getAbsolutePath() );

        applicationContainer.addContextValue( "app.home", context.getAppDir().getAbsolutePath() );

        // ----------------------------------------------------------------------------
        // Make the user's home directory available in the context
        // ----------------------------------------------------------------------------
        //noinspection AccessOfSystemProperties
        applicationContainer.addContextValue( "user.home", System.getProperty( "user.home" ) );

        Object appserver = null;

        try
        {
            appserver = serverContainer.getContext().get( "plexus.appserver" );
        }
        catch ( ContextException e )
        {
            // won't happen.
        }

        applicationContainer.addContextValue( "plexus.appserver", appserver );

        // ----------------------------------------------------------------------
        // Create the realm for the application
        // ----------------------------------------------------------------------

        ClassRealm realm = new ClassRealm( applicationContainer.getClassWorld(),
                                           "plexus.application." + context.getApplicationId(),
                                           applicationContainer.getContainerRealm() );

        applicationContainer.setContainerRealm( realm );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Map ctx = new ContextMapAdapter( applicationContainer.getContext() );

        Xpp3Dom dom = null;

        try
        {
            //noinspection IOResourceOpenedButNotSafelyClosed
            Reader configurationReader =
                new InterpolationFilterReader( new FileReader( context.getAppConfigurationFile() ), ctx );

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

        context.setApplicationContainer( applicationContainer );
    }
}
