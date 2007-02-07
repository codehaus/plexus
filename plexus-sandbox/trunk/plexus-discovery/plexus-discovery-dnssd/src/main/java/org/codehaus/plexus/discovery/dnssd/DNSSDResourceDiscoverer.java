/**
 * Copyright 2006 Aldrin Leal, aldrin at leal dot eng dot bee ar
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codehaus.plexus.discovery.dnssd;

import org.codehaus.plexus.discovery.ResourceDiscoverer;
import org.codehaus.plexus.discovery.URLCodec;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents an Resource found via a DNS Record, via SRV records, using the Style Proposed from the DNS-SD Group
 * <p/>
 * For more info, please check the article
 * <a href="http://www.dns-sd.org/ServerStaticSetup.html">&quot;Manually Adding DNS-SD Service Discovery Records to an Existing Name Server&quot;</a>, as well as the spec, defined in <a href="http://files.dns-sd.org/draft-cheshire-dnsext-dns-sd.txt">draft-cheshire-dnsext-dns-sd-04.txt</a>
 *
 * @author Aldrin Leal
 * @plexus.component role="org.codehaus.plexus.discovery.ResourceDiscoverer"
 * lifecycle-handler="basic"
 */
public class DNSSDResourceDiscoverer
    extends TimerTask
    implements ResourceDiscoverer, LogEnabled, Startable
{
    /**
     * Default Refresh Time
     */
    private static final long REFRESHTIME_DEFAULT = 600L;

    /**
     * Empty Arg
     */
    private static final String NO_VALUE = "";

    /**
     * Logger
     */
    private Logger logger;

    /**
     * DNS Servers being Used
     */
    private Set dnsServers;

    /**
     * Domains being Searched
     */
    private Set dnsDomains;

    /**
     * Services being Searched for
     */
    private Set services;

    /**
     * Resolver being used
     */
    private Resolver resolver;

    /**
     * Refresh Time, in seconds
     *
     * @plexus.configuration default-value="300"
     */
    private long refreshTime;

    /**
     * Timer being used
     */
    private Timer timer;

    /**
     * Found Resources
     */
    private List foundResources;

    /**
     * A HashMap of NS Records for each Zone, which will be populated under the following conditions:
     * <p/>
     * <ul>
     * <li>The Zone has an NS record (which will be the source of a child resolver)
     * <li>The zone also has one of the browse records {l,}b._dns-sd._udp
     * </ul>
     * <p/>
     * Unless otherwise specified, disabled zones will keep an entry, but pointing to null.
     */
    private Map resolverMap;

    /**
     * Constructor
     */
    public DNSSDResourceDiscoverer()
    {
        super();
        setRefreshTime( REFRESHTIME_DEFAULT );
    }

    /**
     * Gets the DNS Servers being Used
     *
     * @return DNS Servers
     * @see DNSSDResourceDiscoverer#setDnsServers(java.util.Set)
     */
    public Set getDnsServers()
    {
        return dnsServers;
    }

    /**
     * Sets the DNS Servers being Used, like "64.142.82.154"
     *
     * @param dnsServers set of dns nameservers addresses, Set of type java.lang.String
     */
    public void setDnsServers( Set dnsServers )
    {
        this.dnsServers = dnsServers;
    }

    /**
     * Returns the list of DNS Domains to get Watched for, like "dns-sd.org"
     *
     * @return dns domains to get watched
     * @see DNSSDResourceDiscoverer#setDnsDomains(java.util.Set)
     */
    public Set getDnsDomains()
    {
        return dnsDomains;
    }

    /**
     * Sets the list of DNS Domains to get Watched for
     *
     * @param dnsDomains dns domains to watch, Set of type java.lang.String
     */
    public void setDnsDomains( Set dnsDomains )
    {
        this.dnsDomains = dnsDomains;
    }

    /**
     * Gets the list of Services to get Watched for
     *
     * @return Services to get Watched for
     * @see DNSSDResourceDiscoverer#setServices(java.util.Set)
     */
    public Set getServices()
    {
        return services;
    }

    /**
     * Sets the list of Services to get Watched for, like "_http._tcp"
     *
     * @param services services to watch, Set of type java.lang.String
     */
    public void setServices( Set services )
    {
        this.services = services;
    }

    /**
     * Gets the Refresh Time, in Seconds
     *
     * @return refresh time, in seconds
     */
    public long getRefreshTime()
    {
        return refreshTime;
    }

    /**
     * Sets the Refresh Time, in Seconds
     *
     * @param refreshTime the refresh time to set
     */
    public void setRefreshTime( long refreshTime )
    {
        this.refreshTime = refreshTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.discovery.ResourceDiscoverer#findResources()
     */
    public Iterator findResources()
    {
        List resourcesToReturn = new ArrayList();

        if ( null != this.foundResources )
        {
            resourcesToReturn.addAll( this.foundResources );
        }

        return resourcesToReturn.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return super.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return this.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    /**
     * Starts the Service
     *
     * @throws StartingException Someone has set up us the bomb
     */
    public void start()
        throws StartingException
    {
        this.timer = new Timer( this.getName() );

        this.timer.scheduleAtFixedRate( this, 0, 1000 * this.refreshTime );
    }

    /**
     * Deactivates the service
     *
     * @throws StoppingException Someone has set us up the bomb
     */
    public void stop()
        throws StoppingException
    {
        this.timer.cancel();
    }

    /**
     * Main Discovery Entry-Point
     */
    public void run()
    {
        try
        {
            Set resourcesFound = new TreeSet();

            /**
             * Sets up the DNS Resolver
             */
            if ( null == this.resolver )
            {
                /**
                 * Have we been supplied a list of DNS Servers to watch for? If true, load'em up!
                 */
                if ( ( null != this.dnsServers ) && !this.dnsServers.isEmpty() )
                {
                    this.resolver = new ExtendedResolver(
                        (String[]) this.dnsServers.toArray( new String[this.dnsServers.size()] ) );
                }
                else
                {
                    /**
                     * Perform Auto-Configuration, dnsjava-style
                     */
                    this.resolver = new SimpleResolver();
                }
            }

            /**
             * resolver map setup
             */
            if ( null == this.resolverMap )
            {
                this.resolverMap = new TreeMap();
            }

            /**
             * for each domain, try to look up resources...
             */
            for ( Iterator iterDnsDomains = this.dnsDomains.iterator(); iterDnsDomains
                .hasNext(); )
            {
                String dnsDomain = (String) iterDnsDomains.next();

                /**
                 * Validate / Skip this domain
                 */
                if ( !this.resolverMap.containsKey( dnsDomain ) )
                {
                    this.resolverMap.put( dnsDomain, setupDnsDomain( dnsDomain ) );
                }

                if ( null == this.resolverMap.get( dnsDomain ) )
                {
                    continue;
                }

                Resolver domainResolver = (Resolver) this.resolverMap.get( dnsDomain );

                // for each service-type defined...
                for ( Iterator iterServices = this.services.iterator(); iterServices
                    .hasNext(); )
                {
                    String serviceType = (String) iterServices.next();
                    Name serviceTypeName =
                        Name.concatenate( Name.fromString( serviceType ), Name.fromString( dnsDomain, Name.root ) );

                    // From a Given Service, Construct a DNS Query to be sent, as well as a message to wrap it up.
                    Record baseRecord = Record.newRecord( serviceTypeName, Type.PTR, DClass.ANY );

                    Message baseMessage = Message.newQuery( baseRecord );
                    // Then, Retrieve the resulting answers from the resolver
                    Message baseReturnMessage = domainResolver.send( baseMessage );

                    if ( 0 != baseReturnMessage.getRcode() )
                    {
                        continue;
                    }

                    // Good. A Positive response!
                    if ( logger.isInfoEnabled() )
                    {
                        logger
                            .info( "baseReturnMessage(domain=" + dnsDomain + "; serviceType=" + serviceType + "): " +
                                baseReturnMessage
                                    .getSectionArray( 1 ).length + " pointers found." );
                    }

                    // For each Answer found, Check if we've got PTR Records
                    for ( int i = 0; i < baseReturnMessage
                        .getSectionArray( Section.ANSWER ).length; i++ )
                    {
                        Record curRecord = baseReturnMessage
                            .getSectionArray( Section.ANSWER )[i];

                        // Ignore any non-PTR resources found.
                        if ( !PTRRecord.class.isAssignableFrom( curRecord
                            .getClass() ) )
                        {
                            continue;
                        }

                        // From the PTR Record, Issue an Additional Query looking for SRV and TXT records

                        PTRRecord ptrRecord = (PTRRecord) curRecord;

                        Record searchRecord = Record.newRecord( ptrRecord.getTarget(), Type.ANY, DClass.ANY );
                        Message searchMessage = Message
                            .newQuery( searchRecord );
                        Message searchReturnMessage = domainResolver
                            .send( searchMessage );

                        // Start to Parse and Build the Resulting Resource

                        String serviceName =
                            decodeServiceName( ptrRecord.getTarget().relativize( serviceTypeName ).toString() );
                        String scheme = serviceType.substring( 1, serviceType.indexOf( '.' ) );
                        String host = dnsDomain;
                        int port = -1;
                        String path;
                        String query = null;
                        Properties properties = new Properties();
                        TXTRecord txtRecord = null;
                        SRVRecord srvRecord = null;

                        if ( 0 == searchReturnMessage.getRcode() )
                        {

                            for ( int j = 0; j < searchReturnMessage
                                .getSectionArray( Section.ANSWER ).length; j++ )
                            {
                                Record curReturnRecord = searchReturnMessage
                                    .getSectionArray( Section.ANSWER )[j];

                                if ( TXTRecord.class
                                    .isAssignableFrom( curReturnRecord
                                        .getClass() ) )
                                {
                                    txtRecord = (TXTRecord) curReturnRecord;

                                    List txtStrings = txtRecord
                                        .getStrings();

                                    for ( Iterator iterTxtStrings = txtStrings
                                        .iterator(); iterTxtStrings
                                        .hasNext(); )
                                    {
                                        String element = (String) iterTxtStrings
                                            .next();

                                        query = decodeElement( properties, element );
                                    }
                                }
                                else if ( SRVRecord.class
                                    .isAssignableFrom( curReturnRecord
                                        .getClass() ) )
                                {
                                    srvRecord = (SRVRecord) curReturnRecord;

                                    host = srvRecord.getTarget()
                                        .toString();
                                    port = srvRecord.getPort();
                                }
                            }
                        }

                        path = ( properties.containsKey( "path" ) ? "" + properties.get( "path" ) : "/" );

                        URI uri = new URI( scheme, null, host, port, path, query, null );

                        if ( logger.isInfoEnabled() )
                        {
                            logger.info( "run(): Found URI '" + uri.toString() + "' mapping to '" + serviceName +
                                "' (serviceType=" + serviceType + ")" );
                        }

                        /**
                         * Finally, Adds the Resource
                         */
                        DNSSDDiscoverableResource dnssdr = new DNSSDDiscoverableResource();

                        dnssdr.setType( serviceType );
                        dnssdr.setName( serviceName );
                        dnssdr.setResourceDiscoverer( this );
                        dnssdr.setUrl( uri.toURL() );
                        dnssdr.setPtrRecord( ptrRecord );
                        dnssdr.setTxtRecord( txtRecord );
                        dnssdr.setSrvRecord( srvRecord );
                        dnssdr.setServiceTypeName( serviceTypeName );

                        resourcesFound.add( dnssdr );
                    }
                }
            }

            if ( !resourcesFound.isEmpty() )
            {
                this.foundResources = new ArrayList( resourcesFound );
            }
        }
        catch ( Exception e )
        {
            if ( logger.isErrorEnabled() )
            {
                logger.error( "run()", e );
            }
        }
    }

    /**
     * Configures a Chain of Resolvers pointing to the NameServer of a given DNSDomain
     * <p/>
     * TODO Fix logic using the Name to let user specify the browse domain, instead of recursively fetching domains
     * TODO Check the semantics more closely
     * TODO DNS-LLQ
     *
     * @param dnsDomain DNS Domain to get Set Up
     * @return a resolver suitable for this DNS Domain, or null if not correctly setup (i.e., no NS records, nor
     *         {,l}b._dns-sd._udp records
     * @throws IOException Someone set us up the bomb!
     * @see <a href="http://www.dns-sd.org/ServerStaticSetup.html">DNS-SD Setup on Plain Bind</a>
     */
    private Resolver setupDnsDomain( String dnsDomain )
        throws IOException
    {
        Record nsRecord = Record.newRecord( Name.fromString( dnsDomain ), Type.NS, DClass.ANY );
        Message baseMessage = Message.newQuery( nsRecord );
        Message baseReturnMessage = resolver.send( baseMessage );
        List domainResolver = new ArrayList();

        if ( 0 == baseReturnMessage.getRcode() )
        {
            for ( int i = 0; i < baseReturnMessage.getSectionArray( Section.ANSWER ).length; i++ )
            {
                NSRecord nsZoneRecord = (NSRecord) baseReturnMessage.getSectionArray( Section.ANSWER )[i];
                SimpleResolver nextResolver = new SimpleResolver( nsZoneRecord.getTarget().toString() );

                domainResolver.add( nextResolver );
            }

            /**
             * Disable completely an domain
             */
            if ( domainResolver.isEmpty() )
            {
                if ( logger.isWarnEnabled() )
                {
                    logger.warn( "Disabling domain '" + dnsDomain + "' since no NS record has been found." );
                }
                return null;
            }
        }

        return new ExtendedResolver( (Resolver[]) domainResolver.toArray( new Resolver[domainResolver.size()] ) );
    }

    /**
     * Decodes a Given Service Name, using the weird semantics defined in the RFC
     *
     * @param serviceName name of the Service to be decoded
     * @return serviceName, decoded
     */
    private String decodeServiceName( String serviceName )
    {
        serviceName = serviceName.replaceAll( "\\\\032", " " );
        serviceName = serviceName.replaceAll( "\\\\\\\\", "\\" );
        serviceName = serviceName.replaceAll( "\\\\\\.", "." );

        return serviceName;
    }

    /**
     * Decode a given name value pair, returning a path part of the url
     *
     * @param properties properties to set/read from
     * @param nvp        name/value pair
     * @return new path
     */
    private String decodeElement( Properties properties, String nvp )
    {
        int ndx = nvp.indexOf( '=' );
        StringBuffer resultingPath = new StringBuffer();
        URLCodec codec = new URLCodec();

        /**
         * Tests if nvp is in the n=v format
         */
        if ( -1 != ndx )
        {
            if ( ndx != ( -1 + nvp.length() ) )
            {
                /**
                 * n=v format
                 */
                properties.put( nvp.substring( 0, ndx ), nvp.substring( 1 + ndx ) );
            }
            else
            {
                /**
                 * It is in the n= format
                 */
                properties.put( nvp.substring( 0, ndx ), NO_VALUE );
            }
        }
        else
        {
            /**
             * It seems it's in the n format
             */
            properties.put( nvp, NO_VALUE );
        }

        int added = 0;

        for ( Iterator keyIterator = properties.keySet().iterator(); keyIterator
            .hasNext(); )
        {
            String key = (String) keyIterator.next();

            if ( "path".equals( key ) )
            {
                continue;
            }

            String value = (String) properties.get( key );

            if ( 0 != added )
            {
                resultingPath.append( "&" );
            }

            resultingPath.append( codec.encode( key ) );
            resultingPath.append( "=" );
            resultingPath.append( codec.encode( value ) );

            added++;
        }

        if ( 0 == resultingPath.length() )
        {
            return null;
        }

        return resultingPath.toString();
    }
}
