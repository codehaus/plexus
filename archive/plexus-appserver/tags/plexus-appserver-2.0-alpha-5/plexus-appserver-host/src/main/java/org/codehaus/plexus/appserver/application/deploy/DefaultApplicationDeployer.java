package org.codehaus.plexus.appserver.application.deploy;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.phase.AppDeploymentPhase;
import org.codehaus.plexus.appserver.application.event.ApplicationListener;
import org.codehaus.plexus.appserver.application.event.DefaultDeployEvent;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.deploy.AbstractDeployer;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.appserver.service.PlexusServiceException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author Jason van Zyl
 * @since Mar 19, 2004
 */
public class DefaultApplicationDeployer
    extends AbstractDeployer
    implements ApplicationDeployer, Contextualizable, Initializable, Disposable
{
    private Map deployments;

    private DefaultPlexusContainer appServerContainer;

    private List applicationListeners;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private File applicationsDirectory;

    private List phases;

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    public void deploy( String id, File location )
        throws ApplicationServerException
    {
        deployJar( location, true );
    }

    private void deployJar( File file, boolean expandPar )
        throws ApplicationServerException
    {
        AppDeploymentContext context = new AppDeploymentContext( file, applicationsDirectory, deployments,
                                                                 appServerContainer, getAppServer(), expandPar );

        for ( Iterator i = phases.iterator(); i.hasNext(); )
        {
            String id = (String) i.next();

            try
            {
                AppDeploymentPhase phase =
                    (AppDeploymentPhase) appServerContainer.lookup( AppDeploymentPhase.ROLE, id );

                phase.execute( context );
            }
            catch ( ComponentLookupException e )
            {
                throw new ApplicationServerException( "The requested app server lifecycle phase cannot be found: " + id,
                                                      e );
            }
            catch ( AppDeploymentException e )
            {
                throw new ApplicationServerException( "Error in the app server lifecycle " + id + " phase.", e );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Redeploy
    // ----------------------------------------------------------------------

    public void redeploy( String id )
        throws ApplicationServerException
    {
        AppRuntimeProfile profile = getApplicationRuntimeProfile( id );

        undeploy( id );

        File file = getAppServer().getAppDescriptor( id ).getPar();

        deployJar( file, false );

        DefaultDeployEvent event = createDeployEvent( profile );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener listener = (ApplicationListener) itr.next();

            listener.redeployedApplication( event );
        }
    }

    // ----------------------------------------------------------------------
    // Undeploy
    // ----------------------------------------------------------------------

    public void undeploy( String id )
        throws ApplicationServerException
    {
        getLogger().info( "Undeploying '" + id + "'." );

        AppRuntimeProfile profile = getApplicationRuntimeProfile( id );

        deployments.remove( id );

        DefaultPlexusContainer app = profile.getApplicationContainer();

        try
        {
            stopApplicationServices( profile );
        }
        catch ( PlexusServiceException e )
        {
            getLogger().info( "Can not stop services attached to application ", e );
        }

        app.dispose();

/* Don't dispose since the appserver container realm = the app container realm at present
        ClassRealm realm = app.getContainerRealm();

        try
        {
            realm.getWorld().disposeRealm( realm.getId() );
        }
        catch ( NoSuchRealmException e )
        {
            getLogger().warn( "Error while disposing appserver realm '" + realm.getId() + "'" );
        }
*/

        DefaultDeployEvent event = createDeployEvent( profile );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener listener = (ApplicationListener) itr.next();

            listener.undeployedApplication( event );
        }
    }

    public boolean isDeployed( String id )
    {
        return deployments.containsKey( id );
    }

    private void stopApplicationServices( AppRuntimeProfile runtimeProfile )
        throws PlexusServiceException
    {
        if ( runtimeProfile.getServices() != null )
        {
            for ( Iterator serviceIterator = runtimeProfile.getServices().iterator(); serviceIterator.hasNext(); )
            {
                PlexusService service = (PlexusService) serviceIterator.next();
                service.applicationStop( runtimeProfile );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Events
    // ----------------------------------------------------------------------

    private DefaultDeployEvent createDeployEvent( AppRuntimeProfile runtimeProfile )
    {
        return new DefaultDeployEvent( runtimeProfile );
    }

    public AppRuntimeProfile getApplicationRuntimeProfile( String applicationName )
        throws ApplicationServerException
    {
        AppRuntimeProfile profile = (AppRuntimeProfile) deployments.get( applicationName );

        if ( profile == null )
        {
            throw new ApplicationServerException( "No such application: '" + applicationName + "'." );
        }

        return profile;
    }

    public void deleteApplication( String applicationName )
        throws ApplicationServerException
    {
        undeploy( applicationName );

        File file = getAppServer().getAppDescriptor( applicationName ).getPar();

        File dir = new File( applicationsDirectory, applicationName );

        try
        {
            FileUtils.deleteDirectory( dir );
        }
        catch ( IOException e )
        {
            throw new ApplicationServerException( "Failed to delete application: '" + applicationName + "'.", e );
        }

        file.delete();

        if ( file.exists() || dir.exists() )
        {
            throw new ApplicationServerException( "Failed to delete application: '" + applicationName + "'." );
        }
    }

    public void addApplicationListener( ApplicationListener listener )
    {
        applicationListeners.add( listener );
    }

    public void removeApplicationListener( ApplicationListener listener )
    {
        applicationListeners.remove( listener );
    }

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        appServerContainer = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws InitializationException
    {
        deployments = new HashMap();

        applicationListeners = new ArrayList();

        getLogger().info( "Applications will be deployed in: '" + applicationsDirectory + "'." );
    }

    public void dispose()
    {
        List names = new ArrayList( deployments.keySet() );

        for ( Iterator it = names.iterator(); it.hasNext(); )
        {
            String name = (String) it.next();

            try
            {
                undeploy( name );
            }
            catch ( ApplicationServerException e )
            {
                getLogger().warn( "Error while undeploying appserver '" + name + "'.", e );
            }
        }
    }

    private ApplicationServer getAppServer()
        throws ApplicationServerException
    {
        try
        {
            return (ApplicationServer) appServerContainer.getContext().get( "plexus.appserver" );
        }
        catch ( ContextException e )
        {
            throw new ApplicationServerException( "Cannot retrieve app server from context.", e );
        }
    }
}
