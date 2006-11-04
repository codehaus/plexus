package org.codehaus.plexus.summit;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * <p>The base class for all the various components used in Summit:</p>
 * <p/>
 * <p/>
 * <ul>
 * <li>Displays</li>
 * <li>Modules</li>
 * <li>Pipelines</li>
 * <li>Valves (not yet)</li>
 * <li>Renderers</li>
 * <li>Resolvers</li>
 * <li>Views</li>
 * </ul>
 * </p>
 * <p/>
 * <p>The primary motivation for this is to share access to the
 * application view which acts a mediator between all the various
 * components so that each of the components are not directly
 * coupled to one another.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
public abstract class AbstractSummitComponent
    extends AbstractLogEnabled
    implements SummitComponent, Contextualizable
{
    private PlexusContainer container;

    public PlexusContainer getContainer()
    {
        return container;
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return container.lookup( role, id );
    }

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return container.lookup( role );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
