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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Wrapper for JmDNS, required for either MDNSResourceDiscoverer and
 * JMDNSResourcePublisher
 *
 * @author Aldrin Leal
 * @plexus.component role="org.codehaus.plexus.discovery.mdns.JmDNSWrapper" role-hint="default"
 * instantiation-strategy="singleton"
 */
public class JmDNSWrapper
        implements Startable {
    /**
     * JmDNS Instance
     */
    private JmDNS jmdns;

    /**
     * InetAddress
     */
    private InetAddress inetAddress;

    /**
     * Flags whether are we running (i.e., alive) or not
     */
    private boolean running;

    /**
     * InetAddress Getter
     *
     * @return InetAddress as an String, Raw Format
     */
    public String getInetAddress() {
        return this.inetAddress.getHostAddress();
    }

    /**
     * InetAddress Setter
     *
     * @param address Address, in order to be supplied to InetAddress.getByName
     * @throws Exception Someone has set us up the bomb!!!1!
     * @see InetAddress#getByName(String)
     */
    public void setInetAddress(String address)
            throws Exception {
        this.inetAddress = InetAddress.getByName(address);
    }

    /**
     * Gets the JmDNS instance being used
     *
     * @return the jmdns
     */
    public JmDNS getJmdns() {
        return jmdns;
    }

    /**
     * {@inheritDoc}
     */
    public void start()
            throws StartingException {
        try {
            this.jmdns = (null == this.inetAddress ? new JmDNS() : new JmDNS(this.inetAddress));
            this.running = true;
        }
        catch (IOException e) {
            throw new StartingException("Couldn't start():", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
            throws StoppingException {
        try {
            jmdns.close();
            this.running = false;
        }
        catch (Exception e) {
            throw new StoppingException("Couldn't stop():", e);
        }
    }

    /**
     * Are we running?
     *
     * @return true if we are running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
