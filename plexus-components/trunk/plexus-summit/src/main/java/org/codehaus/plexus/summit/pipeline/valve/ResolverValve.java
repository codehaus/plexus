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
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *  role-hint="org.codehaus.plexus.summit.pipeline.valve.ResolverValve"
 */
public class ResolverValve
    extends AbstractValve
{
    /**
     * @plexus.configuration
     *  default-value="new"
     */
    private String resolver;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        String target = data.getTarget();

        try
        {
            Resolver res = (Resolver) data.lookup( Resolver.ROLE, resolver );

            Resolution resolution = res.resolve( target );

            data.setResolution( resolution );
        }
        catch ( Exception e )
        {
            throw new ValveInvocationException( "Couldn't resolve target.", e );
        }
    }
}
