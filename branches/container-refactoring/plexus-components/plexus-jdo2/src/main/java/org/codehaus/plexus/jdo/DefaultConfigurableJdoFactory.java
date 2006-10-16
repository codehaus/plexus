package org.codehaus.plexus.jdo;

/*
* Copyright 2005-2006 The Apache Software Foundation.
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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultConfigurableJdoFactory
    extends AbstractConfigurableJdoFactory
    implements ConfigurableJdoFactory, Initializable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @plexus.configuration
     */
    private String driverName;

    /**
     * @plexus.configuration
     */
    private String url;

    /**
     * @plexus.configuration
     */
    private String userName;

    /**
     * @plexus.configuration
     */
    private String password;

    public void setDriverName( String driverName )
    {
        this.driverName = driverName;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public void shutdown()
        throws Exception
    {
        if ( url != null )
        {

            if ( url.indexOf( "jdbc:derby:" ) == 0 )
            {
                String databasePath = url.substring( "jdbc:derby:".length() );

                if ( databasePath.indexOf( ";" ) > 0 )
                {
                    databasePath = databasePath.substring( 0, databasePath.indexOf( ";" ) );
                }

                try
                {
                    /* shutdown the database */
                    DriverManager.getConnection( "jdbc:derby:" + databasePath + ";shutdown=true" );
                }
                catch ( SQLException e )
                {
                    /*
                     * In Derby, any request to the DriverManager with a shutdown=true attribute raises an exception.
                     * http://db.apache.org/derby/manuals/reference/sqlj251.html
                     */
                }

                System.gc();
            }
        }
        super.shutdown();
    }

    public Properties getProperties()
    {
        synchronized( configured )
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

                setPropertyInner( properties, "javax.jdo.PersistenceManagerFactoryClass", persistenceManagerFactoryClass );
                setPropertyInner( properties, "javax.jdo.option.ConnectionDriverName", driverName );
                setPropertyInner( properties, "javax.jdo.option.ConnectionURL", url );
                setPropertyInner( properties, "javax.jdo.option.ConnectionUserName", userName );
                setPropertyInner( properties, "javax.jdo.option.ConnectionPassword", password );

                return properties;
            }
        }
    }
}
