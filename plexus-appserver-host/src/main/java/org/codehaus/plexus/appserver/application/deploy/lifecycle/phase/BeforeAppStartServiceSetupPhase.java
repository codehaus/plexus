package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Jason van Zyl
 */
public class BeforeAppStartServiceSetupPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        PlexusConfiguration[] services = context.getAppConfiguration().getChild( "services" ).getChildren( "service" );

        for ( int i = 0; i < services.length; i++ )
        {
            PlexusConfiguration serviceConfiguration = services[i];

            String id = serviceConfiguration.getChild( "id" ).getValue( "" );

            if ( StringUtils.isEmpty( id ) )
            {
                throw new AppDeploymentException( "Missing child element 'id' in 'service'." );
            }

            PlexusService service;

            try
            {
                service = (PlexusService) context.getAppServerContainer().lookup( PlexusService.ROLE, id );
            }
            catch ( ComponentLookupException e )
            {
                throw new AppDeploymentException( "Error looking up service for pre app init call.", e );
            }

            context.getAppRuntimeProfile().getServices().add( service );

            PlexusConfiguration conf = serviceConfiguration.getChild( "configuration" );

            context.getAppRuntimeProfile().getServiceConfigurations().add( conf );

            try
            {
                service.beforeApplicationStart( context.getAppRuntimeProfile(), conf );
            }
            catch ( Exception e )
            {
                throw new AppDeploymentException( "Error executing service.", e );
            }
        }

        context.getDeployments().put( context.getApplicationId(), context.getAppRuntimeProfile() );
    }
}
