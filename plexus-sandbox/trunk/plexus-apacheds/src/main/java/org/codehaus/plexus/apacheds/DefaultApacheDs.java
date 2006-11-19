package org.codehaus.plexus.apacheds;

import org.apache.directory.server.configuration.MutableServerStartupConfiguration;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.core.configuration.SyncConfiguration;
import org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration;
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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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

    private Set partitionConfigurations = new HashSet();

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
        MutableBTreePartitionConfiguration configuration = new MutableBTreePartitionConfiguration();
        configuration.setName( name );
        configuration.setSuffix( root );
        configuration.setIndexedAttributes( indexedAttributes );
        configuration.setContextEntry( partitionAttributes );
        partitionConfigurations.add( configuration );
    }

    public void addPartition( Partition partition )
        throws NamingException
    {
        MutableBTreePartitionConfiguration configuration = new MutableBTreePartitionConfiguration();
        configuration.setName( partition.getName() );
        configuration.setSuffix( partition.getSuffix() );
        configuration.setIndexedAttributes( partition.getIndexedAttributes() );
        configuration.setContextEntry( partition.getContextAttributes() );
        configuration.setSynchOnWrite( true );
        configuration.setCacheSize( 1 );
        configuration.setOptimizerEnabled( false );
        partitionConfigurations.add( configuration );
    }

    public Partition addSimplePartition( String name, String[] domainComponents )
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

        // ----------------------------------------------------------------------
        // The root entry of the partition
        // ----------------------------------------------------------------------

        Attributes attributes = new BasicAttributes();
        attributes.put( "dc", domainComponents[0] );
        Attribute objectClass = new BasicAttribute( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "domain" );
        objectClass.add( "extensibleObject" );
        attributes.put( objectClass );

        Partition partition = new Partition();
        partition.setName( name );
        partition.setSuffix( suffix );
        partition.setContextAttributes( attributes );
        HashSet set = new HashSet();
        set.add( "uid" );
        set.add( "cn" );
        partition.setIndexedAttributes( set );

        addPartition( partition );

        return partition;
    }

    public void startServer()
        throws Exception
    {
        getLogger().info( "Starting Apache Directory Server server." );

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
        configuration.setSynchPeriodMillis( 100 );

        configuration.setContextPartitionConfigurations( partitionConfigurations );

        Set bootstrapSchemas = new HashSet();
        bootstrapSchemas.add( new AutofsSchema() );
        bootstrapSchemas.add( new CorbaSchema() );
        bootstrapSchemas.add( new CoreSchema() );
        bootstrapSchemas.add( new CosineSchema() );
        bootstrapSchemas.add( new ApacheSchema() );
        bootstrapSchemas.add( new CollectiveSchema() );
        bootstrapSchemas.add( new InetorgpersonSchema() );
        bootstrapSchemas.add( new JavaSchema() );
        bootstrapSchemas.add( new Krb5kdcSchema() );
        bootstrapSchemas.add( new NisSchema() );
        bootstrapSchemas.add( new SystemSchema() );
        bootstrapSchemas.add( new ApachednsSchema() );
        configuration.setBootstrapSchemas( bootstrapSchemas );

        Properties env = new Properties();
        env.setProperty( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.setProperty( Context.SECURITY_CREDENTIALS, "secret" );
        env.setProperty( Context.SECURITY_AUTHENTICATION, "simple" );
        env.setProperty( Context.PROVIDER_URL, "ou=system" );
        env.setProperty( Context.INITIAL_CONTEXT_FACTORY, ServerContextFactory.class.getName() );
        env.putAll( configuration.toJndiEnvironment() );
        new InitialDirContext( env );

        this.configuration = configuration;

        getLogger().info( "Started Apache Directory Server server." );
    }

    public void stopServer()
        throws Exception
    {
        if ( stopped )
        {
            throw new Exception( "Already stopped." );
        }

        getLogger().info( "Stopping Apache Directory Server server." );

        sync();

        stopped = true;

        Hashtable env = new Hashtable();
        env.putAll( new ShutdownConfiguration().toJndiEnvironment() );
        new InitialDirContext( env );

        getLogger().info( "Apache Directory Server server stopped." );
    }

    public void sync()
        throws Exception
    {
        getLogger().info( "Sync'ing Apache Directory Server server." );

        Hashtable env = new Hashtable();
        env.putAll( new SyncConfiguration().toJndiEnvironment() );
        new InitialDirContext( env );
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
            if ( !stopped )
            {
                stopServer();
            }
        }
        catch ( Exception e )
        {
            throw new StoppingException( "Error while stopping Apache Directory Server server.", e );
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
