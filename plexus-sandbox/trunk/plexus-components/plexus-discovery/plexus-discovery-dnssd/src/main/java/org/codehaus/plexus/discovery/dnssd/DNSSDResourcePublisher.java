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

import java.net.UnknownHostException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.discovery.DiscoverableResource;
import org.codehaus.plexus.discovery.ResourceDeregistrationException;
import org.codehaus.plexus.discovery.ResourcePublisher;
import org.codehaus.plexus.discovery.ResourceRegistrationException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Update;

/**
 * A Resource Publisher for DNS-SD + TSIG-API (if keys supplied)
 * 
 * @author Aldrin Leal
 * @plexus.component role="org.codehaus.plexus.discovery.ResourcePublisher"
 *                   lifecycle-handler="basic"
 */
public class DNSSDResourcePublisher implements ResourcePublisher, LogEnabled,
		Startable {
	/**
	 * Matcher for Inet Port
	 */
	private static final Pattern PATTERN_PORT = Pattern
			.compile("(.*)\\:(\\d+)$");

	/**
	 * Default TTL Value
	 */
	private static final int DEFAULT_TTL_VALUE = 3600;

	/**
	 * Have we been started?
	 */
	private boolean started;

	/**
	 * Are we disabling TCP usage?
	 */
	private boolean disableTcp;

	/**
	 * Logger instance
	 */
	private Logger logger;

	/**
	 * TTL for Published Resources
	 */
	private int defaultTtl = DEFAULT_TTL_VALUE;

	/**
	 * ResourceCache
	 */
	private IdentityHashMap resourceCache;

	/**
	 * DNS Domain
	 */
	private String domain;

	/**
	 * DNS Server
	 */
	private String dnsServer;

	/**
	 * Update Key Data
	 */
	private String keyData;

	/**
	 * Key Algorithm
	 */
	private String keyAlgorithm;

	/**
	 * Key Name
	 */
	private String keyName;

	/**
	 * Constructor
	 */
	public DNSSDResourcePublisher() {
		super();
		this.resourceCache = new IdentityHashMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Gets the Key being used
	 * 
	 * @return keyData being used
	 */
	public String getKeyData() {
		return keyData;
	}

	/**
	 * Sets the keyData being used
	 * 
	 * @param keyData
	 *            keyData being used
	 */
	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}

	/**
	 * Gets the DNS Domain where to Register Services
	 * 
	 * @return the DNS Domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets the DNS Domain from where to Register Services
	 * 
	 * @param domain
	 *            the DNS Domain, like 'services.modafocas.org.'
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Gets the DNS Server from where to make your registrations
	 * 
	 * @return the dns service
	 */
	public String getDnsServer() {
		return dnsServer;
	}

	/**
	 * Sets the DNS Server
	 * 
	 * @param dnsServer
	 *            the dns server
	 */
	public void setDnsServer(String dnsServer) {
		this.dnsServer = dnsServer;
	}

	/**
	 * Returns true if the resolver must disable TCP usage
	 * 
	 * @return true if disabled
	 */
	public boolean isDisableTcp() {
		return disableTcp;
	}

	/**
	 * Sets the TCP Disabling Flag
	 * 
	 * @param disableTcp
	 *            true if TCP should be disabled
	 */
	public void setDisableTcp(boolean disableTcp) {
		this.disableTcp = disableTcp;
	}

	/**
	 * Returns the Key Algorithm
	 * 
	 * @return keyData algorithm
	 */
	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	/**
	 * Sets the Key Algorithm
	 * 
	 * @param keyAlgorithm
	 *            keyData Algorithm, like "hmac-md5"
	 */
	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	/**
	 * Gets the Default TTL Value for Registration
	 * 
	 * @return default TTL Value, in seconds
	 */
	public int getDefaultTtl() {
		return defaultTtl;
	}

	/**
	 * Sets the Default TTL Value for Registration
	 * 
	 * @param defaultTtl
	 *            TTL Value, in seconds
	 */
	public void setDefaultTtl(int defaultTtl) {
		this.defaultTtl = defaultTtl;
	}

	/**
	 * Gets the Key Name
	 * 
	 * @return Key Name
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * Sets the Key Name
	 * 
	 * @param keyName
	 *            the Key Name to Set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() throws StartingException {
		if (started) {
			return;
		}

		started = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() throws StoppingException {
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
		} catch (Exception exc) {
			if (logger.isWarnEnabled()) {
				logger.warn("stop()", exc);
			}

			throw new StoppingException("stop()", exc);
		}
	}

	/**
	 * Registers a Given DiscoverableResource
	 * 
	 * @param resource
	 *            resource to get registered
	 * @throws org.codehaus.plexus.discovery.ResourceRegistrationException
	 * 
	 */
	public void registerResource(DiscoverableResource resource)
			throws ResourceRegistrationException {
		try {
			DNSSDDiscoverableResource dnssdr;

			if (DNSSDDiscoverableResource.class.isAssignableFrom(resource
					.getClass())) {
				dnssdr = (DNSSDDiscoverableResource) resource;

				if (dnssdr.getPtrRecord() != null
						|| dnssdr.getSrvRecord() != null
						|| dnssdr.getTxtRecord() != null) {
					throw new ResourceRegistrationException(
							"Resource already registered!");
				}
			} else {
				throw new ResourceRegistrationException(
						"Resource must be of class "
								+ DNSSDDiscoverableResource.class
										.getCanonicalName());
			}

			Name domainName = Name.fromString(this.getDomain());
			Name serviceDomainName = Name.fromString(dnssdr.getType(),
					domainName);
			Name fullName = Name
					.fromString(dnssdr.getName(), serviceDomainName);

			PTRRecord ptrRecord = new PTRRecord(serviceDomainName, DClass.IN,
					this.defaultTtl, fullName);
			SRVRecord srvRecord = new SRVRecord(fullName, DClass.IN,
					this.defaultTtl, 0, 0, dnssdr.getUrl().getPort(), Name
							.fromString(dnssdr.getUrl().getHost()));
			TXTRecord txtRecord = new TXTRecord(fullName, DClass.IN,
					this.defaultTtl, "path=" + dnssdr.getUrl().getPath());

			Resolver resolver = getResolver();

			Update update = new Update(domainName, DClass.IN);
			Record[] records = new Record[] { ptrRecord, srvRecord, txtRecord };
			update.delete(records);
			update.add(records);

			Message returnMessage = resolver.send(update);

			if (0 != returnMessage.getRcode()) {
				throw new ResourceRegistrationException("Update failed: "
						+ returnMessage.toString());
			}

			dnssdr.setPtrRecord(ptrRecord);
			dnssdr.setSrvRecord(srvRecord);
			dnssdr.setTxtRecord(txtRecord);

			this.resourceCache.put(dnssdr, null);
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("registerResource()", e);
			}

			if (ResourceRegistrationException.class.isAssignableFrom(e
					.getClass())) {
				throw (ResourceRegistrationException) e;
			}

			throw new ResourceRegistrationException(
					"While registrating resource", e);
		}
	}

	/**
	 * Constructs / Returns a Resolver Suitable for our cause
	 * 
	 * @return a SimpleResolver, with port and TSIG set
	 * @throws UnknownHostException
	 */
	Resolver getResolver() throws UnknownHostException {
		Resolver resolver;

		Matcher portMatcher = PATTERN_PORT.matcher(this.dnsServer);
		if (portMatcher.matches()) {
			resolver = new SimpleResolver(portMatcher.group(1));
			resolver.setPort(Integer.parseInt(portMatcher.group(2)));
		} else {
			resolver = new SimpleResolver(this.dnsServer);
		}

		if (!this.disableTcp) {
			resolver.setTCP(true);
		}

		if (null != this.keyData && null != this.keyAlgorithm) {
			resolver.setTSIGKey(new TSIG(getAlgorithmName(), this.keyName,
					this.keyData));
		}
		return resolver;
	}

	/**
	 * Update for backwards compatibility w/ dnsjava
	 * 
	 * @return Name Object from the Algorithm
	 * @see TSIG#TSIG(String, String, String)
	 */
	private Name getAlgorithmName() {
		String algorithmName = this.keyAlgorithm;

		if ("md5".equalsIgnoreCase(algorithmName)) {
			algorithmName = "HMAC-MD5.SIG-ALG.REG.INT.";
		} else if ("sha-1".equalsIgnoreCase(algorithmName)) {
			algorithmName = "hmac-sha1.";
		} else if ("sha-256".equalsIgnoreCase(algorithmName)) {
			algorithmName = "hmac-sha256.";
		}

		return Name.fromConstantString(algorithmName);
	}

	/**
	 * Unregisters a Given Resource
	 * 
	 * @param resource
	 *            resource to get unregistered
	 * @throws org.codehaus.plexus.discovery.ResourceDeregistrationException
	 * 
	 */
	public void deregisterResource(DiscoverableResource resource)
			throws ResourceDeregistrationException {
		try {
			DNSSDDiscoverableResource dnssdr;

			if (DNSSDDiscoverableResource.class.isAssignableFrom(resource
					.getClass())) {
				dnssdr = (DNSSDDiscoverableResource) resource;

				if (null == dnssdr.getPtrRecord()
						|| null == dnssdr.getSrvRecord()
						|| null == dnssdr.getTxtRecord()) {
					throw new ResourceRegistrationException(
							"Resource not properly registered!");
				}
			} else {
				throw new ResourceDeregistrationException(
						"Resource must be of class "
								+ DNSSDDiscoverableResource.class
										.getCanonicalName());
			}

			Name domainName = Name.fromString(this.getDomain());

			Resolver resolver = getResolver();

			Update update = new Update(domainName, DClass.IN);
			Record[] records = new Record[] { dnssdr.getPtrRecord(),
					dnssdr.getSrvRecord(), dnssdr.getTxtRecord() };
			update.delete(records);

			Message returnMessage = resolver.send(update);

			if (0 != returnMessage.getRcode()) {
				throw new ResourceDeregistrationException("Update failed: "
						+ returnMessage.toString());
			}

			dnssdr.setPtrRecord(null);
			dnssdr.setSrvRecord(null);
			dnssdr.setTxtRecord(null);
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("deregisterResource()", e);
			}

			if (ResourceDeregistrationException.class.isAssignableFrom(e
					.getClass())) {
				throw (ResourceDeregistrationException) e;
			}

			throw new ResourceDeregistrationException(
					"While registrating resource", e);
		}
	}
}
