package org.codehaus.plexus.embed;

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

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class EmbedderTest
    extends TestCase
{
    public void testConfigurationByURL()
        throws Exception
    {
        PlexusEmbedder embed = new Embedder();

        embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

        embed.addContextValue( "foo", "bar" );

        Properties contextProperties = new Properties();

        contextProperties.setProperty( "property1", "value1" );

        contextProperties.setProperty( "property2", "value2" );

        embed.start();

        try
        {
            embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        Object o = embed.lookup( MockComponent.ROLE );

        assertEquals( "I AM MOCKCOMPONENT", o.toString() );

        assertNotNull( getClass().getResource( "/test.txt" ) );

        embed.stop();
    }
}
