package org.codehaus.plexus.summit.pull;

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

import java.io.IOException;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * PullToolValve uses the PullService to populate the ViewContext.  After
 * populating the context it invokes the next Valve.  After the valve returns
 * the releases the tools from the context.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 7, 2003
 */
public class PullToolReleaseValve
    extends AbstractValve
{
    public void invoke( RunData data )
        throws IOException, ValveInvocationException

    {
        PullService pull;

        try
        {
            pull = (PullService) data.lookup( PullService.ROLE );
        }
        catch ( Exception e )
        {
            throw new ValveInvocationException( "Could not find the PullService!", e );
        }

        ViewContext viewContext = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        pull.releaseTools( viewContext );
    }
}
