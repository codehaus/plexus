/**
 * ========================================================================
 * 
 * Copyright 2006 Rahul Thakur.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.plexus.tutorial;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * Provider for Plexus Component role {@link WebsiteMonitor}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class DefaultWebsiteMonitor
    extends AbstractLogEnabled
    implements WebsiteMonitor
{

    /**
     * Websites to monitor.
     * 
     * @plexus.configuration
     */
    private List websites;

    /**
     * @see org.codehaus.plexus.tutorial.WebsiteMonitor#monitor(java.lang.String)
     */
    public void monitor()
        throws Exception
    {
        HttpClient client = new HttpClient();

        for ( Iterator it = websites.iterator(); it.hasNext(); )
        {
            String website = (String) it.next();
            HttpMethod getMethod = new GetMethod( website );
            getMethod.setFollowRedirects( false );

            try
            {
                int statusCode = client.executeMethod( getMethod );

                if ( statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES )
                {
                    if ( getLogger().isErrorEnabled() )
                        getLogger().error(
                                           "HTTP request returned HTTP status code: " + statusCode + " for website: "
                                               + website );
                    throw new Exception( "HTTP request returned HTTP status code: " + statusCode + " for website: "
                        + website );
                }
                if ( getLogger().isInfoEnabled() )
                    getLogger().info(
                                      "HTTP request returned HTTP status code: " + statusCode + " for website: "
                                          + website );
            }
            catch ( UnknownHostException e )
            {
                if ( getLogger().isErrorEnabled() )
                    getLogger().error( "Specified host '" + website + "' could not be resolved." );
                throw e;
            }
            finally
            {
                if ( null != getMethod )
                    getMethod.releaseConnection();
            }
        }
    }

    public void addWebsites( List websites )
    {
        this.websites = websites;
    }

    public boolean isInitialized()
    {
        return ( null != websites && this.websites.size() > 0 );
    }

}
