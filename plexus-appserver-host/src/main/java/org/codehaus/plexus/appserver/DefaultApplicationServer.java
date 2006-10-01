package org.codehaus.plexus.appserver;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.appserver.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.lifecycle.AppServerContext;
import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.appserver.lifecycle.phase.AppServerPhase;
import org.codehaus.plexus.appserver.service.deploy.ServiceDeployer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */

//- the containers aren't quite right. the appserver container is in the service which might not be correct
//- the container is not initialized

public class DefaultApplicationServer
    extends AbstractLogEnabled
    implements ApplicationServer, Initializable, Contextualizable, Startable
{
    private PlexusContainer container;

    private ApplicationDeployer applicationDeployer;

    private ServiceDeployer serviceDeployer;

    //todo: cdc doing configurations
    private List phases;

    private Map appDescriptors;

    // ----------------------------------------------------------------------
    // Application Facade
    // ----------------------------------------------------------------------

    public AppRuntimeProfile getApplicationRuntimeProfile( String applicationId )
        throws ApplicationServerException
    {
        return applicationDeployer.getApplicationRuntimeProfile( applicationId );
    }

    // ----------------------------------------------------------------------------
    // Delegation to the appserver deploy
    // ----------------------------------------------------------------------------

    public void deploy( String id, File location )
        throws ApplicationServerException
    {
        applicationDeployer.deploy( id, location );
    }

    public void redeploy( String id )
        throws ApplicationServerException
    {
        applicationDeployer.redeploy( id );
    }

    public void undeploy( String id )
        throws ApplicationServerException
    {
        applicationDeployer.undeploy( id );
    }

    public void addAppDescriptor( AppDescriptor appDescriptor )
    {
        appDescriptors.put( appDescriptor.getId(), appDescriptor );
    }

    public AppDescriptor getAppDescriptor( String appId )
    {
        return (AppDescriptor) appDescriptors.get( appId );
    }

    public Collection getAppDescriptors()
    {
        return appDescriptors.values();
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        container.addContextValue( "plexus.appserver", this );
    }

    public void initialize()
        throws InitializationException
    {
        appDescriptors = new LinkedHashMap();
    }

    public void start()
        throws StartingException
    {
        // ----------------------------------------------------------------------
        // Register the deployers inside the directory supervisor so applications
        // and services will be deployed.
        // ----------------------------------------------------------------------

        File appServerHome = FileUtils.resolveFile( new File( "." ), System.getProperty( "plexus.home" ) );

        AppServerContext appServerContext = new AppServerContext( this, appServerHome );

        for ( Iterator i = phases.iterator(); i.hasNext(); )
        {
            String appServerPhaseId = (String) i.next();

            try
            {
                AppServerPhase appServerPhase =
                    (AppServerPhase) container.lookup( AppServerPhase.ROLE, appServerPhaseId );

                appServerPhase.execute( appServerContext );
            }
            catch ( ComponentLookupException e )
            {
                throw new StartingException(
                    "The requested app server lifecycle phase cannot be found: " + appServerPhaseId, e );
            }
            catch ( AppServerLifecycleException e )
            {
                throw new StartingException( "Error in the app server lifecycle " + appServerPhaseId + " phase.", e );
            }
        }

        getLogger().info( "The appserver server has been initialized." );
    }

    public void stop()
    {
        // 1. should shut down all the apps and services properly
        // 2. serialize any configurations
    }
}
