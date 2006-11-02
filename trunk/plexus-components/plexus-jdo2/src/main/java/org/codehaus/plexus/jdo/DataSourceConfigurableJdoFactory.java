package org.codehaus.plexus.jdo;

/*
 * Copyright 2001-2006 The Codehaus.
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
