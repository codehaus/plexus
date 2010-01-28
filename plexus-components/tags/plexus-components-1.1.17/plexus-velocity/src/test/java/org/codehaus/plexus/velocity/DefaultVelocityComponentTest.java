package org.codehaus.plexus.velocity;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.PlexusTestCase;

public class DefaultVelocityComponentTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        DefaultVelocityComponent velocity;

        VelocityContext context;

        String value;

        velocity = (DefaultVelocityComponent) lookup( VelocityComponent.ROLE );

        // test the properties
        value = (String) velocity.getEngine().getProperty( "hello" );

        assertNotNull( value );

        assertEquals( "world", value );

        // test the rendering
        context = new VelocityContext();

        context.put( "variable", "Value from context" );

        Template template = velocity.getEngine().getTemplate("org/codehaus/plexus/velocity/DefaultVelocityComponentTest.vm" );

        StringWriter writer = new StringWriter();

        template.merge( context, writer );

        assertEquals( "Static text -- Value from context -- More static text", writer.toString() );
    }
}
