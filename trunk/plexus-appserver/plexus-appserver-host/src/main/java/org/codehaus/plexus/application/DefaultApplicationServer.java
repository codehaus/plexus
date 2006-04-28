package org.codehaus.plexus.application;

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

import java.io.File;
import java.util.List;
import java.util.Iterator;

import org.codehaus.plexus.application.lifecycle.phase.deploy.application.ApplicationDeployer;
import org.codehaus.plexus.application.lifecycle.phase.deploy.service.ServiceDeployer;
import org.codehaus.plexus.application.lifecycle.phase.AppServerPhase;
import org.codehaus.plexus.application.lifecycle.AppServerContext;
import org.codehaus.plexus.application.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.application.supervisor.Supervisor;
import org.codehaus.plexus.application.supervisor.SupervisorListener;
import org.codehaus.plexus.application.supervisor.SupervisorException;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */

//- the containers aren't quite right. the application container is in the service which might not be correct
//- the container is not initialized

public class DefaultApplicationServer
    extends AbstractLogEnabled
    implements ApplicationServer,
    Contextualizable,
    Startable
{
    private PlexusContainer container;

    private ApplicationDeployer applicationDeployer;

    private ServiceDeployer serviceDeployer;

    private Supervisor supervisor;

    //todo: cdc doing configurations
    private List phases;

    // ----------------------------------------------------------------------
    // Application Facade
    // ----------------------------------------------------------------------

    public ApplicationRuntimeProfile getApplicationRuntimeProfile( String applicationId )
        throws ApplicationServerException
    {
        return applicationDeployer.getApplicationRuntimeProfile( applicationId );
    }

    // ----------------------------------------------------------------------------
    // Delegation to the application deployer
    // ----------------------------------------------------------------------------

    public void deploy( String name,
                        String location )
        throws ApplicationServerException
    {
        applicationDeployer.deploy( name, location );
    }

    public void redeploy( String name,
                          String location )
        throws ApplicationServerException
    {
        applicationDeployer.deploy( name, location );
    }

    public void undeploy( String name )
        throws ApplicationServerException
    {
        applicationDeployer.undeploy( name );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        // ----------------------------------------------------------------------
        // Register the deployers inside the directory supervisor so applications
        // and services will be deployed.
        // ----------------------------------------------------------------------

        File appServerHome = new File( System.getProperty( "plexus.home" ) );

        AppServerContext appServerContext = new AppServerContext( this, appServerHome );

        for ( Iterator i = phases.iterator(); i.hasNext(); )
        {
            String appServerPhaseId = (String) i.next();

            try
            {
                AppServerPhase appServerPhase = (AppServerPhase) container.lookup( AppServerPhase.ROLE, appServerPhaseId );

                appServerPhase.execute( appServerContext );
            }
            catch ( ComponentLookupException e )
            {
                throw new StartingException( "The requested app server lifecycle phase cannot be found: " + appServerPhaseId, e );
            }
            catch ( AppServerLifecycleException e )
            {
                throw new StartingException( "Error in the app server lifecycle " + appServerPhaseId + " phase.", e );
            }
        }

        getLogger().info( "The application server has been initialized." );
    }

    public void stop()
    {
        // 1. should shut down all the apps and services properly
        // 2. serialize any configurations
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        container.addContextValue( "plexus.appserver", this );
    }
}
