package org.codehaus.plexus.jdo;

/*
 * Created on Jan 13, 2005
 *
 * Copyright STPenable Ltd. (c) 2005
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

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author David Wynter
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultJdoFactoryTest.java 1482 2005-02-25 17:08:26Z trygvis $
 */
public class DefaultJdoFactoryTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        JdoFactory jdoFactory = (JdoFactory) lookup( JdoFactory.ROLE );

        PersistenceManagerFactory pmf = jdoFactory.getPersistenceManagerFactory();

        PersistenceManager pm = pmf.getPersistenceManager();
/*
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Parent parent = new Parent( "Sony Discman", "A standard discman from Sony", 49.99 );

            pm.makePersistent( parent );

            tx.commit();
        }
        finally
        {
            if ( tx.isActive() )
            {
                tx.rollback();
            }

            pm.close();
        }
*/
    }
}
