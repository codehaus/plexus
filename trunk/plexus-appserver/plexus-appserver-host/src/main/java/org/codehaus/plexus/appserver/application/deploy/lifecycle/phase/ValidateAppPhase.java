package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.PlexusApplicationConstants;
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

        File location = context.getApplicationsDirectory();

        String appId = context.getApplicationId();

        File applicationConfigurationFile = new File( new File( location, PlexusApplicationConstants.CONF_DIRECTORY ),
                                                      PlexusApplicationConstants.CONFIGURATION_FILE );

        if ( !applicationConfigurationFile.exists() )
        {
            throw new AppDeploymentException( "The appserver '" + appId + "' does not have a valid " +
                "configuration: " + applicationConfigurationFile + " does not exist!" );
        }

        context.setAppConfigurationFile( applicationConfigurationFile );

        File applicationLibrary = new File( location, PlexusApplicationConstants.LIB_DIRECTORY );

        if ( !applicationLibrary.exists() )
        {
            throw new AppDeploymentException( "The appserver '" + appId + "' does not have a valid library: " +
                applicationLibrary + " does not exist!" );
        }

        context.setAppLibDirectory( applicationLibrary );
    }
}
