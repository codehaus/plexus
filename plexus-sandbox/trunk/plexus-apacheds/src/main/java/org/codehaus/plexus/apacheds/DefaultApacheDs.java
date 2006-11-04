package org.codehaus.plexus.apacheds;

import org.apache.directory.server.configuration.MutableServerStartupConfiguration;
import org.apache.directory.server.core.configuration.MutableDirectoryPartitionConfiguration;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.core.schema.bootstrap.ApacheSchema;
import org.apache.directory.server.core.schema.bootstrap.ApachednsSchema;
import org.apache.directory.server.core.schema.bootstrap.AutofsSchema;
import org.apache.directory.server.core.schema.bootstrap.CollectiveSchema;
import org.apache.directory.server.core.schema.bootstrap.CorbaSchema;
import org.apache.directory.server.core.schema.bootstrap.CoreSchema;
import org.apache.directory.server.core.schema.bootstrap.CosineSchema;
import org.apache.directory.server.core.schema.bootstrap.InetorgpersonSchema;
import org.apache.directory.server.core.schema.bootstrap.JavaSchema;
import org.apache.directory.server.core.schema.bootstrap.Krb5kdcSchema;
import org.apache.directory.server.core.schema.bootstrap.NisSchema;
import org.apache.directory.server.core.schema.bootstrap.SystemSchema;
import org.apache.directory.server.jndi.ServerContextFactory;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultApacheDs
    extends AbstractLogEnabled
    implements ApacheDs, Startable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private boolean enableNetworking;

    private File basedir;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private boolean stopped;

    private MutableServerStartupConfiguration configuration;

    private Set contextPartitionConfigurations = new HashSet();

    // ----------------------------------------------------------------------
    // ApacheDs Implementation
    // ----------------------------------------------------------------------

    public void setBasedir( File basedir )
    {
        this.basedir = basedir;
    }

    public void setEnableNetworking( boolean enableNetworking )
    {
        this.enableNetworking = enableNetworking;
    }

    public InitialDirContext getAdminContext()
        throws NamingException
    {
        assertIsStarted();

        Hashtable environment = new Hashtable( configuration.toJndiEnvironment() );
        environment.put( Context.INITIAL_CONTEXT_FACTORY, ServerContextFactory.class.getName() );
        environment.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        environment.put( Context.SECURITY_CREDENTIALS, "secret" );
        environment.put( Context.SECURITY_AUTHENTICATION, "simple" );
//        environment.put( Context.PROVIDER_URL, "dc=hauskeeper,dc=codehaus,dc=org" );
        return new InitialDirContext( environment );
    }

    public InitialDirContext getSystemContext()
        throws NamingException
    {
        assertIsStarted();

        Hashtable environment = new Hashtable( configuration.toJndiEnvironment() );
        environment.put( Context.INITIAL_CONTEXT_FACTORY, ServerContextFactory.class.getName() );
        environment.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        environment.put( Context.SECURITY_CREDENTIALS, "secret" );
        environment.put( Context.SECURITY_AUTHENTICATION, "simple" );
        environment.put( Context.PROVIDER_URL, "ou=system" );
        return new InitialDirContext( environment );
    }

    public void addPartition( String name, String root, Set indexedAttributes, Attributes partitionAttributes )
        throws NamingException
    {
        MutableDirectoryPartitionConfiguration directoryPartitionConfiguration = new MutableDirectoryPartitionConfiguration();
        directoryPartitionConfiguration.setName( name );
        directoryPartitionConfiguration.setSuffix( root );
        directoryPartitionConfiguration.setIndexedAttributes( indexedAttributes );
        directoryPartitionConfiguration.setContextEntry( partitionAttributes );
        contextPartitionConfigurations.add( directoryPartitionConfiguration );
    }

    public void addPartition( Partition partition )
        throws NamingException
    {
        MutableDirectoryPartitionConfiguration directoryPartitionConfiguration = new MutableDirectoryPartitionConfiguration();
        directoryPartitionConfiguration.setName( partition.getName() );
        directoryPartitionConfiguration.setSuffix( partition.getSuffix() );
        directoryPartitionConfiguration.setIndexedAttributes( partition.getIndexedAttributes() );
        directoryPartitionConfiguration.setContextEntry( partition.getContextAttributes() );
        contextPartitionConfigurations.add( directoryPartitionConfiguration );
    }

    public Partition addSimplePartition( String ... domainComponents )
        throws NamingException
    {
        if ( domainComponents.length == 0 )
        {
            throw new NamingException( "Illegal argument, there has to be at least one domain component." );
        }

        String suffix = "";

        for ( int i = 0; i < domainComponents.length; i++ )
        {
            String dc = domainComponents[i];

            suffix += "dc=" + dc;

            if ( i != domainComponents.length - 1 )
            {
                suffix += ",";
            }
        }

        Partition partition = new Partition();
        partition.setName( "Partition for " + suffix );
        partition.setSuffix( suffix );
        Attributes attributes = new BasicAttributes();
        attributes.put( "dc", domainComponents[0] );
        attributes.put( "objectClass", "top" );
        Attribute objectClass = new BasicAttribute( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "domain" );
        objectClass.add( "extensibleObject" );
        attributes.put( objectClass );
        partition.setContextAttributes( attributes );

        addPartition( partition );

        return partition;
    }

    public void startServer()
        throws Exception
    {
        getLogger().info( "Starting LDAP server." );

        getLogger().info( "ApacheDS basedir: " + basedir.getAbsolutePath() );

        File logs = new File( basedir, "logs" );

        if ( !logs.exists() && !logs.mkdirs() )
        {
            throw new Exception( "Could not create logs directory: " + logs.getAbsolutePath() );
        }

        Properties environment = new Properties();
        environment.setProperty( "java.naming.security.authentication", "simple" );
        environment.setProperty( "java.naming.security.principal", "uid=admin,ou=system" );
        environment.setProperty( "java.naming.security.credentials", "secret" );

        MutableServerStartupConfiguration configuration = new MutableServerStartupConfiguration();
        configuration.setWorkingDirectory( basedir );
        configuration.setAllowAnonymousAccess( true );
        configuration.setEnableNtp( false );
        configuration.setEnableKerberos( false );
        configuration.setEnableChangePassword( false );
        configuration.setLdapPort( 10389 );
        configuration.setEnableNetworking( enableNetworking );

        configuration.setContextPartitionConfigurations( contextPartitionConfigurations );

        contextPartitionConfigurations = new HashSet();
        contextPartitionConfigurations.add( new AutofsSchema() );
        contextPartitionConfigurations.add( new CorbaSchema() );
        contextPartitionConfigurations.add( new CoreSchema() );
        contextPartitionConfigurations.add( new CosineSchema() );
        contextPartitionConfigurations.add( new ApacheSchema() );
        contextPartitionConfigurations.add( new CollectiveSchema() );
        contextPartitionConfigurations.add( new InetorgpersonSchema() );
        contextPartitionConfigurations.add( new JavaSchema() );
        contextPartitionConfigurations.add( new Krb5kdcSchema() );
        contextPartitionConfigurations.add( new NisSchema() );
        contextPartitionConfigurations.add( new SystemSchema() );
        contextPartitionConfigurations.add( new ApachednsSchema() );
        configuration.setBootstrapSchemas( contextPartitionConfigurations );

        Properties env = new Properties();
        env.setProperty( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.setProperty( Context.SECURITY_CREDENTIALS, "secret" );
        env.setProperty( Context.SECURITY_AUTHENTICATION, "simple" );
        env.setProperty( Context.PROVIDER_URL, "ou=system" );
        env.setProperty( Context.INITIAL_CONTEXT_FACTORY, ServerContextFactory.class.getName() );
        env.putAll( configuration.toJndiEnvironment() );
        new InitialDirContext( env );

        this.configuration = configuration;

        getLogger().info( "Started LDAP server." );
    }

    public void stopServer()
        throws Exception
    {
        if ( stopped )
        {
            throw new Exception( "Already stopped." );
        }

        stopped = true;

        getLogger().info( "Stopping LDAP server." );

        Hashtable env = new Hashtable();
        ShutdownConfiguration configuration = new ShutdownConfiguration();
        env.putAll( configuration.toJndiEnvironment() );
        new InitialDirContext( env );

        getLogger().info( "LDAP server stopped." );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
    }

    public void stop()
        throws StoppingException
    {
        try
        {
            if ( ! stopped )
            {
                stopServer();
            }
        }
        catch ( Exception e )
        {
            throw new StoppingException( "Error while stopping LDAP server.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void assertIsStarted()
        throws NamingException
    {
        if ( configuration == null )
        {
            throw new NamingException( "The server has to be started before used." );
        }
    }
}
