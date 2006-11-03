package org.codehaus.plexus.security.common.jdo;

import org.codehaus.plexus.jdo.DefaultConfigurableJdoFactory;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.configuration.UserConfiguration;
import org.codehaus.plexus.util.StringUtils;

/**
 * UserConfigurableJdoFactory 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.jdo.JdoFactory"
 *                   role-hint="users"
 */
public class UserConfigurableJdoFactory
    extends DefaultConfigurableJdoFactory
    implements Initializable
{
    /**
     * @plexus.requirement
     */
    private UserConfiguration config;
    
    private String getConfigString( String key, String currentValue, String defaultValue )
    {
        if ( StringUtils.isNotEmpty( currentValue ) )
        {
            return config.getString( key, currentValue );
        }
        else
        {
            return config.getString( key, defaultValue );
        }
    }

    public void initialize()
        throws InitializationException
    {
        String jdbcDriverName = getConfigString( "jdbc.driver.name", super.getDriverName(),
                                                 "org.apache.derby.jdbc.EmbeddedDriver" );
        String jdbcUrl = getConfigString( "jdbc.url", super.getUrl(), 
                                          "jdbc:derby:${plexus.home}/database;create=true" );
        String jdbcUsername = getConfigString( "jdbc.username", super.getUserName(), "sa" );
        String jdbcPassword = getConfigString( "jdbc.password", super.getPassword(), "" );

        super.setDriverName( jdbcDriverName );
        super.setUrl( jdbcUrl );
        super.setUserName( jdbcUsername );
        super.setPassword( jdbcPassword );

        if ( StringUtils.isEmpty( super.persistenceManagerFactoryClass ) )
        {
            super.setPersistenceManagerFactoryClass( "org.jpox.PersistenceManagerFactoryImpl" );
        }

        if ( ( super.otherProperties == null ) || super.otherProperties.isEmpty() )
        {
            super.setProperty( "org.jpox.autoCreateSchema", "true" );
            super.setProperty( "org.jpox.autoStartMechanism", "SchemaTable" );
            super.setProperty( "org.jpox.autoStartMechanismMode", "Ignored" );
            super.setProperty( "org.jpox.validateTables", "false" );
            super.setProperty( "org.jpox.validateConstraints", "false" );
            super.setProperty( "org.jpox.transactionIsolation", "READ_UNCOMMITTED" );
            super.setProperty( "org.jpox.rdbms.dateTimezone", "JDK_DEFAULT_TIMEZONE" );
        }

        super.initialize();
    }

}
