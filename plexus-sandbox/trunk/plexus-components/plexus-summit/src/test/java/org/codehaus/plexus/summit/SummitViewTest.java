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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.summit.parameters.RequestParameterParser;

/**
 * SummitView unit tests.
 * 
 * @author Pete Kazmier
 */
public class SummitViewTest
    extends PlexusTestCase
{
    /**
     * Verify that the SummitView can in fact act as a ServiceBroker
     * and lookup() components.
     *
     * @throws Exception If an error occurs.
     */
    public void testServiceBroker() throws Exception
    {
        // Now we lookup() a component with the 'view'
        RequestParameterParser parser = 
            ( RequestParameterParser ) lookup( RequestParameterParser.ROLE );

        assertTrue( parser instanceof RequestParameterParser );
    }
}
