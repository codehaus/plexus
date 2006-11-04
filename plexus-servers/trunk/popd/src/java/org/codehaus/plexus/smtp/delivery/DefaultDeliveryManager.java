/******************************************************************************
 * $Workfile:  $
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************
 * Copyright (c) 2003, Eric Daugherty (http://www.ericdaugherty.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Eric Daugherty nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 * *****************************************************************************
 * For current versions and more information, please visit:
 * http://www.ericdaugherty.com/java/mailserver
 *
 * or contact the author at:
 * java@ericdaugherty.com
 *****************************************************************************/

package org.codehaus.plexus.smtp.delivery;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.smtp.delivery.DeliveryProcessor;
import org.codehaus.plexus.smtp.delivery.FileDeliveryProcessor;
import org.codehaus.plexus.smtp.delivery.DeliveryManager;

public class DefaultDeliveryManager
    extends AbstractLogEnabled
    implements Configurable, DeliveryManager
{
    private Configuration configuration;

    /**
     * Gets a reference to the configurationManager.
     */
    public DefaultDeliveryManager()
    {
    }

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.configuration = configuration;
    }

    /**
     * Returns the DeliveryProcessor that should be used to
     * deliver messages to the specified domain.
     *
     * @param domain Internet domain name.
     * @return the DeliveryProcessor implementation to use.
     */
    public DeliveryProcessor getDeliveryProcessor( String domain )
    {
        domain = domain.trim();
        boolean isLocal = false;

        Configuration[] domains = configuration.getChildren( "domain" );

        for ( int index = 0; index < domains.length; index++ )
        {
            if ( domains[index].getValue( "" ).equalsIgnoreCase( domain ) )
            {
                isLocal = true;
                break;
            }
        }

        // Return the local Delivery processor or the remote
        // one.
        if ( isLocal )
        {
            return new FileDeliveryProcessor();
        }
        else
        {
            return new SmtpDeliveryProcessor();
        }
    }
}
