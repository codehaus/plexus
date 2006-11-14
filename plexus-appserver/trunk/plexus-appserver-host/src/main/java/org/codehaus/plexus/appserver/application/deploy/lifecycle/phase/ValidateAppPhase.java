package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ValidateAppPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        // ----------------------------------------------------------------------
        // We need to make sure that we have the basic requirements covered
        // when deploying an appserver. The PAR may be incomplete or corrupt
        // or a directory copied over just may not be intact.
        //
        // -> ${app}/conf/plexus.conf
        // -> ${app}/lib
        // ----------------------------------------------------------------------

        File appDir = context.getAppDir();

        String appId = context.getApplicationId();

        // first, look in ${appserver.base}/conf/APP_ID/application.xml for an override
        File serverAppConfigDir =
            new File( context.getAppServer().getAppServerBase(), PlexusRuntimeConstants.CONF_DIRECTORY );
        File appConfigDir = new File( serverAppConfigDir, appId );
        File applicationConfigurationFile = new File( appConfigDir, PlexusApplicationConstants.CONFIGURATION_FILE );

        if ( !applicationConfigurationFile.exists() )
        {
            // otherwise, fallback to the deployed application's conf/application.xml
            appConfigDir = new File( appDir, PlexusApplicationConstants.CONF_DIRECTORY );
            applicationConfigurationFile = new File( appConfigDir, PlexusApplicationConstants.CONFIGURATION_FILE );
        }

        if ( !applicationConfigurationFile.exists() )
        {
            throw new AppDeploymentException( "The application '" + appId + "' does not have a valid " +
                "configurator: " + applicationConfigurationFile + " does not exist!" );
        }

        context.setAppConfigurationFile( applicationConfigurationFile );

        getLogger().info( "Using application configurator file " + applicationConfigurationFile + "." );

        File applicationLibrary = new File( appDir, PlexusApplicationConstants.LIB_DIRECTORY );

        if ( !applicationLibrary.exists() || !applicationLibrary.isDirectory() )
        {
            throw new AppDeploymentException( "The application '" + appId + "' does not have a valid library: " +
                applicationLibrary + " does not exist!" );
        }

        context.setAppLibDirectory( applicationLibrary );
    }
}
