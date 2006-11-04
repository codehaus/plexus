package org.codehaus.plexus.installer;

/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.installer.Installer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * Manager interface to look up an installer.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DefaultInstallerManager
    implements InstallerManager, Contextualizable
{
    // TODO: Get away from doing container lookups once we have active, container-backed maps.
    private PlexusContainer container;

    /**
     * Retrieve the container instance so we can lookup installers that are added after this component is looked up for
     * the first time.
     * 
     * @throws ContextException 
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void contextualize( Context context ) throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    /**
     * @see org.codehaus.plexus.installer.manager.InstallerManager#getInstaller(java.lang.String)
     */
    public Installer getInstaller( String installerName )
        throws NoSuchInstallerException
    {
        Installer installer;
        try
        {
            installer = (Installer) container.lookup( Installer.ROLE, installerName );
        }
        catch ( ComponentLookupException e )
        {
            throw new NoSuchInstallerException( installerName, e );
        }

        if ( installer == null )
        {
            throw new NoSuchInstallerException( installerName );
        }

        return installer;
    }
}
