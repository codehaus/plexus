package org.codehaus.plexus.component.manager;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.test.DefaultServiceD;
import org.codehaus.plexus.test.ServiceD;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PoolableComponentManagerTest
    extends PlexusTestCase
{
    /**
     * Test poolable instantiation strategy.
     *
     * @throws Exception
     */
    public void testPoolableInstantiationStrategy()
        throws Exception
    {
        PlexusContainer container = getContainer();

        // ----------------------------------------------------------------------
        //  ServiceD
        // ----------------------------------------------------------------------

        // Retrieve an manager of component c.
        ServiceD serviceD1 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD1 );

        ServiceD serviceD2 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD2 );

        ServiceD serviceD3 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD3 );

        assertNotSame( serviceD1, serviceD2 );

        assertNotSame( serviceD2, serviceD3 );

        assertNotSame( serviceD1, serviceD3 );

        // Now let's release all the components.

        container.release( serviceD1 );

        container.release( serviceD2 );

        container.release( serviceD3 );

        ServiceD[] ds = new DefaultServiceD[ 30 ];

        for ( int h = 0; h < 5; h++ )
        {
            // Consume all available components in the pool.

            for ( int i = 0; i < 30; i++ )
            {
                ds[ i ] = (ServiceD) container.lookup( ServiceD.ROLE );
            }

            // Release them all.

            for ( int i = 0; i < 30; i++ )
            {
                container.release( ds[ i ] );
            }
        }
    }
}
