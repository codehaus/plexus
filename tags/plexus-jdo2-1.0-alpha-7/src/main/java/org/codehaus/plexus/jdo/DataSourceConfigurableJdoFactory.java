package org.codehaus.plexus.jdo;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.sql.SQLException;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id: DefaultConfigurableJdoFactory.java 3796 2006-09-01 00:42:18Z carlos $
 */
public class DataSourceConfigurableJdoFactory
    extends AbstractConfigurableJdoFactory
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @plexus.configuration
     */
    private String connectionFactoryName;

    /**
     * @plexus.configuration
     */
    private String shutdownConnectionFactoryName;

    public Properties getProperties()
    {
        synchronized ( configured )
        {
            if ( configured == Boolean.TRUE )
            {
                return properties;
            }
            else
            {
                Properties properties = new Properties();

                Iterator it = otherProperties.entrySet().iterator();
                while ( it.hasNext() )
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
                }

                setPropertyInner( properties, "javax.jdo.PersistenceManagerFactoryClass",
                                  persistenceManagerFactoryClass );
                setPropertyInner( properties, "javax.jdo.option.ConnectionFactoryName", connectionFactoryName );

                return properties;
            }
        }
    }


    public void shutdown()
        throws Exception
    {
        if ( shutdownConnectionFactoryName != null )
        {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup( shutdownConnectionFactoryName );
            try
            {
                ds.getConnection();
            }
            catch ( SQLException e )
            {
                /*
                 * In Derby, any request to the DriverManager with a shutdown=true attribute raises an exception.
                 * http://db.apache.org/derby/manuals/reference/sqlj251.html
                 */
            }
        }

        super.shutdown();
    }
}
