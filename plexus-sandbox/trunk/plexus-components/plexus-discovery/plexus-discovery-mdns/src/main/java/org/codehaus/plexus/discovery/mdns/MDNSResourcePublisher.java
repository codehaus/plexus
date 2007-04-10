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

package org.codehaus.plexus.discovery.mdns;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.discovery.*;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import javax.jmdns.ServiceInfo;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 * A MDNS Implementation of an Resource Publisher
 *
 * @author Aldrin Leal
 * @plexus.component role="org.codehaus.plexus.discovery.ResourcePublisher" role-hint="mdns"
 */
public class MDNSResourcePublisher
        implements ResourcePublisher, LogEnabled, Startable {
    /**
     * Signal jMDNS's ServiceInfo there's no value defined
     */
    private static final byte[] NO_VALUE = new byte[0];

    /**
     * Have we started?
     */
    private boolean started;

    /**
     * ResourceCache
     */
    private IdentityHashMap resourceCache;

    /**
     * Logger instance
     */
    private Logger logger;

    /**
     * jMDNS Instance
     *
     * @plexus.requirement
     */
    private JmDNSWrapper jmDNSWrapper;

    /**
     * Default Constructor
     */
    public MDNSResourcePublisher() {
        reset();
    }

    /**
     * Reset
     */
    private void reset() {
        if (null != this.jmDNSWrapper) {
            this.jmDNSWrapper.getJmdns().unregisterAllServices();
        }

        this.started = false;
        if (null != this.resourceCache) {
            this.resourceCache.clear();
        }
        this.resourceCache = new IdentityHashMap();
    }

    /**
     * {@inheritDoc}
     */
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    /**
     * jmDNSWrapper Getter
     *
     * @return the jmDNSWrapper
     */
    public JmDNSWrapper getJmDNSWrapper() {
        return jmDNSWrapper;
    }

    /**
     * jmDNSWrapper Setter
     *
     * @param jmDNSWrapper the jmDNSWrapper to set
     */
    public void setJmDNSWrapper(JmDNSWrapper jmDNSWrapper) {
        this.jmDNSWrapper = jmDNSWrapper;
    }

    /**
     * {@inheritDoc}
     */
    public void start()
            throws StartingException {
        if (started) {
            return;
        }

        started = true;
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
            throws StoppingException {
        try {
            if (!started) {
                return;
            }

            for (Iterator iterResources = this.resourceCache.keySet()
                    .iterator(); iterResources.hasNext();) {
                DiscoverableResource curResource = (DiscoverableResource) iterResources
                        .next();

                this.deregisterResource(curResource);
            }

            reset();
        }
        catch (Exception exc) {
            throw new StoppingException("stop()", exc);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deregisterResource(DiscoverableResource resource)
            throws ResourceDeregistrationException {
        try {
            ServiceInfo serviceInfo = getServiceInfo(resource);

            if (!this.jmDNSWrapper.isRunning()) {
                return;
            }

            if (logger.isInfoEnabled()) {
                logger.info("deregisterResource(resource=" + resource + "): serviceInfo=" + serviceInfo + ")");
            }

            this.jmDNSWrapper.getJmdns().unregisterService(serviceInfo);

            this.resourceCache.remove(serviceInfo);
        }
        catch (Exception e) {
            throw new ResourceDeregistrationException("deregisterResource()", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void registerResource(DiscoverableResource resource)
            throws ResourceRegistrationException {
        try {
            ServiceInfo serviceInfo = getServiceInfo(resource);

            if (logger.isInfoEnabled()) {
                logger.info("registerResource(resource=" + resource + "): serviceInfo=" + serviceInfo + ")");
            }

            this.jmDNSWrapper.getJmdns().registerService(serviceInfo);
        }
        catch (Exception e) {
            throw new ResourceRegistrationException("registerResource()", e);
        }
    }

    /**
     * Retrieves the ServiceInfo out from a resource cache
     *
     * @param resource resource to retrieve/cache
     * @return serviceInfo record
     * @throws UnsupportedEncodingException Invalid Encoding
     */
    private ServiceInfo getServiceInfo(DiscoverableResource resource)
            throws UnsupportedEncodingException {
        if (!this.resourceCache.containsKey(resource)) {
            ServiceInfo serviceInfo = new ServiceInfo("_" + resource.getUrl().getProtocol() + "._tcp.local.",
                    resource.getName(), resource.getUrl().getPort(), 0, 0,
                    queryToHashTable(resource.getUrl()));
            this.resourceCache.put(resource, serviceInfo);
        }

        return (ServiceInfo) this.resourceCache.get(resource);
    }

    /**
     * Converts the remaining parts of an URL into a Hashtable, suitable for
     * post-processing to the ServiceInfo constructor.
     * <p/>
     * Path is appended, as well as all the other properties decoded out from
     * the Query part of the URL
     *
     * @param url URL to get Decoded
     * @return Hashtable with the Query from the URL, plus the Path
     * @throws UnsupportedEncodingException Invalid Encoding
     * @throws CodecException               Codec Failure
     * @see URL#getQuery()
     * @see <a
     *      href="http://files.dns-sd.org/draft-cheshire-dnsext-dns-sd.txt">IETF
     *      Draft ("DNS-Based Service Discovery")</a>
     * @see <a
     *      href="http://svn.apache.org/repos/asf/jakarta/commons/proper/codec/tags/CODEC_1_3/src/java/org/apache/commons/codec/net/StringEncodings.java">Commons-Codec:
     *      URLCodec Source</a>
     */
    private Hashtable queryToHashTable(URL url)
            throws CodecException, UnsupportedEncodingException {
        Hashtable hashTable = new Hashtable();

        if (StringUtils.isNotEmpty(url.getPath())) {
            hashTable.put("path", url.getPath());
        }

        if (StringUtils.isNotEmpty(url.getQuery())) {
            String query = new String(URLCodec.decodeUrl(url.getQuery()
                    .getBytes(URLCodec.ENCODING_US_ASCII)));
            String[] nvps = query.split("\\&");

            for (int i = 0; i < nvps.length; i++) {
                String nvp = nvps[i];
                int ndx = nvp.indexOf('=');

                /**
                 * Tests if nvp is in the n=v format
                 */
                if (-1 != ndx) {
                    if (ndx != (-1 + nvp.length())) {
                        /**
                         * n=v format
                         */
                        hashTable.put(nvp.substring(0, ndx), nvp
                                .substring(1 + ndx));
                    } else {
                        /**
                         * It is in the n= format
                         */
                        hashTable.put(nvp.substring(0, ndx), NO_VALUE);
                    }
                } else {
                    /**
                     * It seems it's in the n format
                     */
                    hashTable.put(nvp, NO_VALUE);
                }
            }
        }

        return hashTable;
    }
}
