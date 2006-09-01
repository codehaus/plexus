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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultConfigurableJdoFactory
    extends DefaultJdoFactory
    implements ConfigurableJdoFactory, Initializable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @plexus.configuration
     */
    private String persistenceManagerFactoryClass;

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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Boolean configured = Boolean.FALSE;

    private Properties properties;

    private PersistenceManagerFactory pmf;

    private Properties otherProperties;

    /*
javax.jdo.PersistenceManagerFactoryClass	org.jpox.PersistenceManagerFactoryImpl	The name of the PersistenceManager implementation
javax.jdo.option.ConnectionDriverName		The name of the (JDBC) driver to use for the DB
javax.jdo.option.ConnectionURL		URL specifying the datastore to use for persistence
javax.jdo.option.ConnectionUserName		Username to use for connecting to the DB
javax.jdo.option.ConnectionPassword		Password to use for connecting to the DB
javax.jdo.option.IgnoreCache	true | false	Whether to ignore the cache for queries
javax.jdo.option.Multithreaded	true | false	Whether to run the PersistenceManager multithreaded
javax.jdo.option.NontransactionalRead	true | false	Whether to allow nontransactional reads
javax.jdo.option.NontransactionalWrite	true | false	Whether to allow nontransactional writes. Not supported by JPOX
javax.jdo.option.Optimistic	true | false	Whether to use Optimistic transactions
javax.jdo.option.RetainValues	true | false	Whether to suppress automatic eviction of persistent instances on transaction completion
javax.jdo.option.RestoreValues	true | false	Whether persistent object have transactional field values restored when transaction rollback occurs.
javax.jdo.option.Mapping		Name for the ORM MetaData mapping files to use with this PMF. For example if this is set to "mysql" then the implementation looks for MetaData mapping files called "'classname'-mysql.orm" or "package-mysql.orm". If this is not specified then the JDO implementation assumes that all is specified in the JDO MetaData file.
javax.jdo.mapping.Catalog		Name of the catalog to use by default for all classes persisted using this PMF. This can be overridden in the MetaData where required, and is optional. JPOX will prefix all table names with this catalog name if the RDBMS supports specification of catalog names in DDL.
javax.jdo.mapping.Schema		Name of the schema to use by default for all classes persisted using this PMF. This can be overridden in the MetaData where required, and is optional. JPOX will prefix all table names with this schema name if the RDBMS supports specification of schema names in DDL.
    */

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        if ( otherProperties == null )
        {
            otherProperties = new Properties();
        }
    }

    // ----------------------------------------------------------------------
    // JdoFactory Implementation
    // ----------------------------------------------------------------------

    public PersistenceManagerFactory getPersistenceManagerFactory()
    {
        if ( configured == Boolean.FALSE )
        {
            configure();
        }

        return pmf;
    }

    // ----------------------------------------------------------------------
    // ConfigurationJdoFactory Implementation
    // ----------------------------------------------------------------------

    public void setPersistenceManagerFactoryClass( String persistenceManagerFactoryClass )
    {
        this.persistenceManagerFactoryClass = persistenceManagerFactoryClass;
    }

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

    public void setProperty( String key, String value )
    {
        setPropertyInner( otherProperties, key, value );
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

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void configure()
    {
        synchronized( configured )
        {
            if ( configured == Boolean.TRUE )
            {
                return;
            }

            doConfigure();
        }
    }

    private void doConfigure()
    {
        Properties properties = getProperties();

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Configuring JDO Factory." );

            for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                getLogger().debug( entry.getKey() + "=" + entry.getValue() );
            }
        }

        /* TODO: implement these
        javax.jdo.option.IgnoreCache	true | false	Whether to ignore the cache for queries
        javax.jdo.option.Multithreaded	true | false	Whether to run the PersistenceManager multithreaded
        javax.jdo.option.NontransactionalRead	true | false	Whether to allow nontransactional reads
        javax.jdo.option.NontransactionalWrite	true | false	Whether to allow nontransactional writes. Not supported by JPOX
        javax.jdo.option.Optimistic	true | false	Whether to use Optimistic transactions
        javax.jdo.option.RetainValues	true | false	Whether to suppress automatic eviction of persistent instances on transaction completion
        javax.jdo.option.RestoreValues	true | false	Whether persistent object have transactional field values restored when transaction rollback occurs.
        javax.jdo.option.Mapping		Name for the ORM MetaData mapping files to use with this PMF. For example if this is set to "mysql" then the implementation looks for MetaData mapping files called "'classname'-mysql.orm" or "package-mysql.orm". If this is not specified then the JDO implementation assumes that all is specified in the JDO MetaData file.
        javax.jdo.mapping.Catalog		Name of the catalog to use by default for all classes persisted using this PMF. This can be overridden in the MetaData where required, and is optional. JPOX will prefix all table names with this catalog name if the RDBMS supports specification of catalog names in DDL.
        javax.jdo.mapping.Schema		Name of the schema to use by default for all classes persisted using this PMF. This can be overridden in the MetaData where required, and is optional. JPOX will prefix all table names with this schema name if the RDBMS supports specification of schema names in DDL.
        */

        pmf = JDOHelper.getPersistenceManagerFactory( properties );

        this.properties = properties;

        configured = Boolean.TRUE;
    }

    private void setPropertyInner( Properties properties, String key, String value )
    {
        if ( key == null )
        {
            throw new IllegalArgumentException( "The key cannot be null." );
        }

        if ( value == null )
        {
            return;
        }

        properties.setProperty( key, value );
    }
}
