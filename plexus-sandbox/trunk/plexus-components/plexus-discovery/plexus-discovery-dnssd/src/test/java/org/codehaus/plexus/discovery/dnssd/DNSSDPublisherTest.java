package org.codehaus.plexus.discovery.dnssd;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.discovery.ResourcePublisher;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;

/**
 * Tests the DNSSDPublisherTest. Please check the Setup Notes for this Test
 *
 * @author Aldrin Leal 07/01/2007 11:18:06
 */
public class DNSSDPublisherTest
    extends PlexusTestCase
{
    /**
     * Resource Discoverer
     */
    private DNSSDResourcePublisher resourcePublisher;

    /**
     * NameServer
     */
    private Process nameserver;

    /**
     * Config Dir
     */
    private File configDir;

    /**
     * Inet Address
     */
    private InetAddress inetAddress;

    /**
     * Override
     *
     * @param context
     * @throws Exception
     */
    protected void customizeContext( Context context )
        throws Exception
    {
        context.put( "IF", this.inetAddress.getHostAddress() );

        super.customizeContext(
            context );    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        this.inetAddress = InetAddress.getLocalHost();

        this.configDir = new File( System.getProperty( "user.dir" ), "named" );

        if ( this.configDir.exists() )
        {
            File[] files = this.configDir.listFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                files[i].delete();
            }
        }
        else
        {
            this.configDir.mkdir();
        }

        File configFile = getFileFromTemplate( "named.conf", "named.conf.template" );
        File unsecureDb = getFileFromTemplate( "db.unsecure.dnssd.test", "db.template" );
        File secureDb = getFileFromTemplate( "db.dnssd.test", "db.template" );

        super.setUp();

        String name = this.getName().substring( "test".length() );

        this.resourcePublisher = (DNSSDResourcePublisher) this.container
            .lookup( ResourcePublisher.ROLE, name );

        /*
         * Named startup is disabled on Win32 platforms
         */
        if ( !System.getProperty( "os.name" ).startsWith( "Windows" ) )
        {
            this.nameserver = Runtime.getRuntime().exec( new String[]{"named", "-g", "-c",
                configFile.getAbsolutePath().replaceAll( "\\\\", "/" ), "-4", "-d", "3"} );
        }
        else
        {
            System.err.println( "Win32 has some issues with running named from the test. However, you can:" );
            System.err.println( "a) Grab a Win32 build from http://www.isc.org/sw/bind/" );
            System.err.println( "b) Unpack it and run named.exe with this command:" );
            System.err.println( "  > named -g -c " + configFile.getAbsolutePath() + " -4 -d 3" );
        }
    }

    /**
     * Creates a file off a Template File
     *
     * @param target   target file (will be created in configDir)
     * @param template file to copy from (relative to this class classpath)
     * @return Path to the Generated File
     * @throws IOException Someone set us up the bomb!
     */
    private File getFileFromTemplate( String target, String template )
        throws IOException
    {
        File configFile = new File( this.configDir, target );

        PrintWriter printWriter = new PrintWriter( new FileOutputStream( configFile ) );
        BufferedReader reader =
            new BufferedReader( new InputStreamReader( DNSSDPublisherTest.class.getResourceAsStream( template ) ) );

        String line;
        String path = this.configDir.getAbsolutePath().replaceAll( "\\\\", "/" );
        while ( null != ( line = reader.readLine() ) )
        {
            line = line.replaceAll( "\\$DIR", path );
            line = line.replaceAll( "\\$IF", this.inetAddress.getHostAddress() );
            printWriter.println( line );
        }

        printWriter.close();
        return configFile;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
        {
            return;
        }

        if ( null != this.nameserver )
        {
            this.nameserver.destroy();
        }

        if ( this.configDir.exists() )
        {
            File[] files = this.configDir.listFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                files[i].delete();
            }
        }

        this.configDir.delete();
    }


    /**
     * Tests on an unsecure, IP-based, but left open to any address
     *
     * @throws Exception Someone has set us up the bomb!
     */
    public void testUnsecure()
        throws Exception
    {
        DNSSDDiscoverableResource discoverableResource = new DNSSDDiscoverableResource();

        discoverableResource.setName( "Home" );
        discoverableResource.setType( "_http._tcp" );
        discoverableResource.setUrl( new URL( "http://www.dnssd.test.:8000/" ) );

        this.resourcePublisher.registerResource( discoverableResource );

        Lookup lookup = new Lookup( Name.fromString( "_http._tcp.unsecure.dnssd.test." ), Type.PTR, DClass.IN );
        lookup.setCache( null );

        Resolver resolver = this.resourcePublisher.getResolver();

        lookup.setResolver( resolver );

        Record[] records = lookup.run();

        assertTrue( null != records );
        assertTrue( 0 != records.length );

        this.resourcePublisher.deregisterResource( discoverableResource );

        /*
         * clears the cache
         */
        lookup = new Lookup( Name.fromString( "_http._tcp.unsecure.dnssd.test." ), Type.PTR, DClass.IN );
        lookup.setCache( null );

        records = lookup.run();

        assertTrue( null == records || 0 == records.length );
    }

    /**
     * Tests on an keyed, TSIG-based DNS Domain
     *
     * @throws Exception Someone has set us up the bomb!
     */
    public void testSimple()
        throws Exception
    {
        DNSSDDiscoverableResource discoverableResource = new DNSSDDiscoverableResource();

        discoverableResource.setName( "Home" );
        discoverableResource.setType( "_http._tcp" );
        discoverableResource.setUrl( new URL( "http://www.dnssd.test.:8000/" ) );

        this.resourcePublisher.registerResource( discoverableResource );

        Lookup lookup = new Lookup( Name.fromString( "_http._tcp.dnssd.test." ), Type.PTR, DClass.IN );
        lookup.setCache( null );

        Resolver resolver = this.resourcePublisher.getResolver();

        lookup.setResolver( resolver );

        Record[] records = lookup.run();

        assertTrue( null != records );
        assertTrue( 0 != records.length );

        this.resourcePublisher.deregisterResource( discoverableResource );

        /*
         * clears the cache
         */
        lookup = new Lookup( Name.fromString( "_http._tcp.dnssd.test." ), Type.PTR, DClass.IN );
        lookup.setCache( null );

        records = lookup.run();

        assertTrue( null == records || 0 == records.length );
    }
}
