package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;

/**
 * @author Jason van Zyl
 */
public class AppInitializationPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        String name = "plexus.application." + context.getApplicationId();

        // ----------------------------------------------------------------------------
        // Create the container and start
        // ----------------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer;

        try
        {
            addLibJars( context, name );

            // This call will initialise and start the container

            applicationContainer = new DefaultPlexusContainer( name, context.getContextValues(), context
                .getAppConfigurationFile().getAbsoluteFile(), context.getAppRuntimeProfile().getApplicationWorld() );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }

        context.getAppRuntimeProfile().setApplicationContainer( applicationContainer );
    }

    private void addLibJars( AppDeploymentContext context, String realmName )
        throws AppDeploymentException
    {
        // not using fileutils here since that can throw an IOException
        File[] jars = context.getAppLibDirectory().listFiles( new FileFilter()
        {
            public boolean accept( File pathname )
            {
                return pathname.isFile() && pathname.toString().endsWith( ".jar" );
            }
        } );

        // should not happen
        if ( jars == null )
        {
            getLogger().warn( "Not a directory: " + context.getAppLibDirectory() );
        }
        else
        {
            ClassWorld appWorld = context.getAppRuntimeProfile().getApplicationWorld();
            ClassRealm realm;

            try
            {
                realm = appWorld.getRealm( realmName );
            }
            catch ( NoSuchRealmException e1 )
            {
                throw new AppDeploymentException( "Realm not found: " + realmName, e1 );
            }

            for ( int i = 0; i < jars.length; i++ )
            {
                try
                {
                    realm.addURL( jars[i].toURI().toURL() );
                }
                catch ( MalformedURLException e )
                {
                    getLogger().warn( "Error converting file " + jars[i] + " to URL", e );
                }
            }
        }
    }
}
