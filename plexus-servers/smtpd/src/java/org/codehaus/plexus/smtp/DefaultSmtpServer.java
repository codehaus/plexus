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

package org.codehaus.plexus.smtp;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.synapse.AbstractSynapseServer;
import org.codehaus.plexus.synapse.handler.ServiceHandler;

/**
 * Manages the interaction between the server plugins and the
 * SMTP client.  This class provides the implementation
 * of rfc821.
 *
 * @author Eric Daugherty
 */
public class DefaultSmtpServer
    extends AbstractSynapseServer
    implements Serviceable, Initializable, SmtpServer
{
    private ServiceHandler serviceHandler;

    /** @see Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        super.service( serviceManager );

        serviceHandler = (ServiceHandler) serviceManager.lookup( ServiceHandler.class.getName() );
    }

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        super.initialize();

        getReactor().registerServiceHandler( serviceHandler );
    }

    public void dispose()
    {
    }
}
