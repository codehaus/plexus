package org.codehaus.plexus.server.apacheds;

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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.apache.ldap.server.jndi.EnvKeys;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultApacheDS
    extends AbstractLogEnabled
    implements ApacheDS, Initializable, Startable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @configuration
     */
    private String providerUrl;

    /**
     * @configuration
     */
    private String principal;

    /**
     * @configuration
     */
    private String credentials;

    /**
     * @configuration
     */
    private String workingDirectory;

    /**
     * @configuration
     */
    private String disableAnonymous;

    /**
     * @configuration
     */
    private int port;

    /**
     * @configuration
     */
    private int sslPort;

    /**
     * @configuration
     */
    private List partitions;

    /**
     * @configuration
     */
    private List schemas;

    /**
     * @configuration
     */
    private Properties extraProperties;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Properties serverProperties;

    private InitialDirContext rootContext;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        serverProperties = new Properties( extraProperties );

        setProperty( Context.INITIAL_CONTEXT_FACTORY, "org.apache.ldap.server.jndi.ServerContextFactory" );

        setProperty( Context.PROVIDER_URL, providerUrl );

        setProperty( Context.SECURITY_PRINCIPAL, principal );

        setProperty( Context.SECURITY_CREDENTIALS, credentials );

        setProperty( EnvKeys.WKDIR, workingDirectory );

        setProperty( EnvKeys.DISABLE_ANONYMOUS, disableAnonymous );

        if ( port != 0 )
        {
            getLogger().info( "Apache DS will be listening on port " + port );

            setProperty( EnvKeys.LDAP_PORT, Integer.toString( port ) );
        }

        if ( sslPort != 0 )
        {
            getLogger().info( "Apache DS will be listening on SSL port " + sslPort );

            setProperty( EnvKeys.LDAPS_PORT, Integer.toString( sslPort ) );
        }

        // ----------------------------------------------------------------------
        // Partitions
        // ----------------------------------------------------------------------

        String partitionsString = "";

        for ( Iterator it = partitions.iterator(); it.hasNext(); )
        {
            Partition partition = (Partition) it.next();

            // ----------------------------------------------------------------------
            // Partition name
            // ----------------------------------------------------------------------

            String name = partition.getName();

            if ( StringUtils.isEmpty( name ) )
            {
                throw new Exception( "Error in configuration: Missing or empty element 'name' for partition." );
            }

            partitionsString += name + " ";

            // ----------------------------------------------------------------------
            // Suffix
            // ----------------------------------------------------------------------

            String suffix = partition.getSuffix();

            if ( StringUtils.isEmpty( suffix ) )
            {
                throw new Exception( "Error in configuration: Missing or empty element 'suffix' for partition '" + name + "'." );
            }

            setProperty( EnvKeys.SUFFIX + name, suffix );

            // ----------------------------------------------------------------------
            // Object classes
            // ----------------------------------------------------------------------

            List objectClasses = partition.getObjectClasses();

            if ( objectClasses.size() == 0 )
            {
                getLogger().warn( "No object classes configured for partition: '" + name + "'." );
            }

            String objectClassesString = "";

            for ( Iterator it2 = objectClasses.iterator(); it2.hasNext(); )
            {
                String objectClass = (String) it2.next();

                if ( StringUtils.isEmpty( objectClass ) )
                {
                    throw new Exception( "Error in configuration: Missing or empty element 'objectClass' for partition '" + name + "'." );
                }

                objectClassesString += objectClass + " ";
            }

            setProperty( EnvKeys.ATTRIBUTES + name + ".objectClass", objectClassesString );

            // ----------------------------------------------------------------------
            // Indices classes
            // ----------------------------------------------------------------------

            List indices = partition.getIndices();

            String indicesString = "";

            for ( Iterator it2 = indices.iterator(); it2.hasNext(); )
            {
                String index = (String) it2.next();

                if ( StringUtils.isEmpty( index ) )
                {
                    throw new Exception( "Error in configuration: Missing or empty element 'index' for partition '" + name + "'." );
                }

                indicesString += index + " ";
            }

            setProperty( EnvKeys.INDICES + name, indicesString );
        }

        setProperty( EnvKeys.PARTITIONS, partitionsString );

        // ----------------------------------------------------------------------
        // Schemas
        // ----------------------------------------------------------------------

        String schemasString = "";

        for ( Iterator it = schemas.iterator(); it.hasNext(); )
        {
            String schema = (String) it.next();

            schemasString += schema + " ";
        }

        setProperty( EnvKeys.SCHEMAS, schemasString );

        // Dump the configuration
//        for ( Iterator it = new TreeMap( serverProperties ).entrySet().iterator(); it.hasNext(); )
//        {
//            Map.Entry entry = (Map.Entry) it.next();
//
//            System.err.println( entry.getKey() + "=" + entry.getValue() );
//        }
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting the Apache Directory Server." );

        rootContext = new InitialDirContext( serverProperties );
    }

    public void stop()
        throws Exception
    {
        getLogger().info( "Stopping the Apache Directory Server." );

        Properties shutdownProperties = new Properties();

        shutdownProperties.setProperty( EnvKeys.SHUTDOWN, "" );

        new InitialDirContext( shutdownProperties );

        // Loose the reference to the main context to it can be GCed

        rootContext = null;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void setProperty( String key, String value )
    {
        String oldValue = serverProperties.getProperty( key );

        if ( !StringUtils.isEmpty( oldValue ) )
        {
            getLogger().warn( "Extra property will be overridden: '" + key + "'." );
        }

        serverProperties.setProperty( key, value );
    }
}
