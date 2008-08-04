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

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * @author David Wynter
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultJdoFactory
    extends AbstractLogEnabled
    implements JdoFactory, Initializable
{
    /** @configuration */
    private Properties properties;

    private PersistenceManagerFactory persistenceManagerFactory;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Initializing JDO." );

        persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory( properties );

        String driverClass = null;

        try
        {
            driverClass = (String) properties.get( "javax.jdo.option.ConnectionDriverName" );

            //TDDO: Class.forName is evil
            Class.forName( driverClass );
        }
        catch ( ClassNotFoundException e )
        {
            throw new InitializationException( "Cannot find driver class: " + driverClass, e );
        }

        // TODO: Move this to a special DBCP version of the JdoFactory
/*
        if ( properties.get( "usePool" ).equals( "true" ) )
        {
            // Create the actual pool of connections
            ObjectPool connectionPool = new GenericObjectPool( null );

            // Create the factory to be used by the pool to create the connections
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                (String) properties.get( "javax.jdo.option.ConnectionURL" ),
                (String) properties.get( "javax.jdo.option.ConnectionUserName" ),
                (String) properties.get( "javax.jdo.option.ConnectionPassword" ) );

            // Wrap the connections with pooled variants.
            PoolableConnectionFactory pcf = new PoolableConnectionFactory( connectionFactory, connectionPool, null,
                                                                           null, false, true );

            // Create the datasource
            DataSource ds = new PoolingDataSource( connectionPool );

            // Create our PMF for using JPOX.
            persistenceManagerFactory.setConnectionDriverName( (String) properties.get( "javax.jdo.option.ConnectionDriverName" ) );
            persistenceManagerFactory.setConnectionURL( (String) properties.get( "javax.jdo.option.ConnectionURL" ) );
            persistenceManagerFactory.setConnectionFactory( ds );
        }
        else
        {
            PersistenceManagerFactory persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory( properties );
        }
*/
    }

    // ----------------------------------------------------------------------
    // JdoFactory Implementation
    // ----------------------------------------------------------------------

    public PersistenceManagerFactory getPersistenceManagerFactory()
    {
        return persistenceManagerFactory;
    }
}
