package org.codehaus.plexus.appserver.service.deploy;

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
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.deploy.AbstractDeployer;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.phase.ServiceDeploymentPhase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author Jason van Zyl
 * @since Jul 17, 2004
 */
public class DefaultServiceDeployer
    extends AbstractDeployer
    implements ServiceDeployer, Initializable, Contextualizable
{
    private File servicesDirectory;

    private DefaultPlexusContainer container;

    private List phases;

    // ----------------------------------------------------------------------
    // ServiceDeployer Implementation
    // ----------------------------------------------------------------------

    public void deploy( String serviceId, File location )
        throws ApplicationServerException
    {
        deploy( serviceId, location, true );
    }

    private void deploy( String id, File sar, boolean expandSar )
        throws ApplicationServerException
    {
        ServiceDeploymentContext context = new ServiceDeploymentContext( id, sar, servicesDirectory, container );

        for ( Iterator i = phases.iterator(); i.hasNext(); )
        {
            String phaseId = (String) i.next();

            try
            {
                ServiceDeploymentPhase phase =
                    (ServiceDeploymentPhase) container.lookup( ServiceDeploymentPhase.ROLE, phaseId );

                phase.execute( context );
            }
            catch ( ComponentLookupException e )
            {
                throw new ApplicationServerException(
                    "The requested app server lifecycle phase cannot be found: " + phaseId, e );
            }
            catch ( ServiceDeploymentException e )
            {
                throw new ApplicationServerException( "Error executing service deployment id.", e );
            }
        }
    }

    public void redeploy( String id )
        throws ApplicationServerException
    {
    }

    public void undeploy( String id )
        throws ApplicationServerException
    {
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Services will be deployed in: '" + servicesDirectory + "'." );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
