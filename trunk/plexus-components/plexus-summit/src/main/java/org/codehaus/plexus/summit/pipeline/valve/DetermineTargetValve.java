package org.codehaus.plexus.summit.pipeline.valve;

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

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *  role-hint="org.codehaus.plexus.summit.pipeline.valve.DetermineTargetValve"
 */
public class DetermineTargetValve
    extends AbstractValve
{
    /**
     * @plexus.requirement
     *  role-hint="new"
     */
    private Resolver resolver;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        if ( !data.hasTarget() )
        {
            String target = data.getParameters().getString( "target" );

            if ( target == null )
            {
                target = data.getParameters().getString( "view" );
            }

            if ( target != null )
            {
                data.setTarget( target );
            }
            else
            {
                target = resolver.getInitialView();

                data.setTarget( target );
            }
        }
    }
}
