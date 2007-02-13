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

import org.codehaus.plexus.discovery.DiscoverableResource;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TXTRecord;

/**
 * DNSSDDiscoverableResource is an extension from DiscoverableResource which gets hold of the Associated PTR Record
 * and its Zone, in order to manage it into the DNS Server
 */
public class DNSSDDiscoverableResource
    extends DiscoverableResource
{
    /**
     * Zone which keeps the PTR Record *
     */
    private Name serviceTypeName;

    /**
     * PTR Record *
     */
    private PTRRecord ptrRecord;

    /**
     * TXT Record *
     */
    private TXTRecord txtRecord;

    /**
     * SRV Record *
     */
    private SRVRecord srvRecord;

    /**
     * Gets the associated PTR Record
     *
     * @return PTR Record associated with this DNSSD Discoverable Resource
     */
    public PTRRecord getPtrRecord()
    {
        return ptrRecord;
    }

    /**
     * Sets the associated PTR Record
     *
     * @param ptrRecord PTR Record being used
     */
    public void setPtrRecord( PTRRecord ptrRecord )
    {
        this.ptrRecord = ptrRecord;
    }

    /**
     * Gets the Zone which keeps the PTR Record
     *
     * @return Zone which keeps the PTR Record
     */
    public Name getServiceTypeName()
    {
        return serviceTypeName;
    }

    /**
     * Sets the Zone which keeps the PTR Record
     *
     * @param serviceTypeName serviceTypeName which keeps the PTR Record
     */
    public void setServiceTypeName( Name serviceTypeName )
    {
        this.serviceTypeName = serviceTypeName;
    }

    /**
     * Gets the SRV Record for this service
     *
     * @return SRV Record
     */
    public SRVRecord getSrvRecord()
    {
        return srvRecord;
    }

    /**
     * Sets the SRV Record for this service
     *
     * @param srvRecord SRV Record
     */
    public void setSrvRecord( SRVRecord srvRecord )
    {
        this.srvRecord = srvRecord;
    }

    /**
     * Gets the TXT Record for this service
     *
     * @return TXT Record
     */
    public TXTRecord getTxtRecord()
    {
        return txtRecord;
    }

    /**
     * Sets the TXT Record for this service
     *
     * @param txtRecord TXT Record
     */
    public void setTxtRecord( TXTRecord txtRecord )
    {
        this.txtRecord = txtRecord;
    }
}
