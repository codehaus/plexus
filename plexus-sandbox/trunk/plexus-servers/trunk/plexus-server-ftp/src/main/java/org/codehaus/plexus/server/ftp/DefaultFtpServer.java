/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Incubator", "FtpServer", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * $Id$
 */
package org.codehaus.plexus.server.ftp;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.server.ftp.ip.IpRestrictor;
import org.codehaus.plexus.server.ftp.usermanager.UserManager;
import org.codehaus.plexus.server.ftp.util.AsyncMessageQueue;
import org.codehaus.plexus.server.PlexusServerSocketFactory;
import org.codehaus.plexus.server.DefaultServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Rana Bhattacharyya <rana_b@yahoo.com>
 * @author Paul Hammant <Paul_Hammant@yahoo.com>
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultFtpServer
    extends DefaultServer
    implements FtpServer, Initializable, Disposable
{
    // ----------------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------------

    /** @plexus.requirement */
    private IpRestrictor ipRestrictor;

    /** @plexus.requirement */
    private UserManager userManager;

    private AsyncMessageQueue asyncMessageQueue;

    private ConnectionService connectionService;

    // ----------------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------------

    private FtpStatus ftpStatus;

    private FtpStatistics statistics;

    private int dataPort[][];

    /** @plexus.configuration default-value="20" */
    private int maxLogin;

    /** @plexus.configuration default-value="10" */
    private int anonymousLogin;

    /** @plexus.configuration default-value="120" */
    private int pollInterval;

    /** @plexus.configuration default-value="300" */
    private int defaultIdle;

    /** @plexus.configuration default-value="true" */
    private boolean anonymousAllowed;

    /** @plexus.configuration default-value="true" */
    private boolean createHome;

    /** @plexus.configuration default-value="/" */
    private File defaultRoot;

    private FtpConfig configuration;

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Initializing Server." );

        try
        {
            ftpStatus = new FtpStatus();

            configuration = new FtpConfig( getLogger(), statistics, connectionService, "systemName",
                                           anonymousAllowed, ftpStatus, ipRestrictor, asyncMessageQueue, defaultIdle,
                                           pollInterval, userManager, defaultRoot, maxLogin, anonymousLogin, createHome,
                                            0, this );

            connectionService = new ConnectionService( configuration );

            configuration.setConnectionService( connectionService );

            statistics = new FtpStatistics( configuration );

            configuration.setStatistics( statistics );

            asyncMessageQueue = new AsyncMessageQueue();

            asyncMessageQueue.setMaxSize( 4096 );
        }
        catch ( Exception ex )
        {
            throw new InitializationException( "Error starting FTP DefaultServer", ex );
        }
    }

    /** Release all resources. */
    public void dispose()
    {
        // close connection service
        if ( connectionService != null )
        {
            connectionService.dispose();

            connectionService = null;
        }

        // close message queue
        if ( asyncMessageQueue != null )
        {
            asyncMessageQueue.stop();

            asyncMessageQueue = null;
        }
    }

    public void handleConnection( Socket socket )
    {
        FtpConnection conHandle = new FtpConnection( configuration );

        conHandle.handleConnection( socket );
    }

    /**
     * Get data port. Data port number zero (0) means that
     * any available port will be used.
     */
    public int getDataPort()
    {
        synchronized ( dataPort )
        {
            int dataPort = -1;

            int loopTimes = 2;

            Thread currThread = Thread.currentThread();

            while ( ( dataPort == -1 ) && ( --loopTimes >= 0 ) && ( !currThread.isInterrupted() ) )
            {
                // search for a free port
                for ( int i = 0; i < this.dataPort.length; i++ )
                {
                    if ( this.dataPort[i][1] == 0 )
                    {
                        if ( this.dataPort[i][0] != 0 )
                        {
                            this.dataPort[i][1] = 1;
                        }
                        dataPort = this.dataPort[i][0];

                        break;
                    }
                }

                // no available free port - wait for the release notification
                if ( dataPort == -1 )
                {
                    try
                    {
                        this.dataPort.wait();
                    }
                    catch ( InterruptedException ex )
                    {
                    }
                }

            }
            return dataPort;
        }
    }

    /** Release data port */
    public void releaseDataPort( int port )
    {
        synchronized ( dataPort )
        {
            for ( int i = 0; i < dataPort.length; i++ )
            {
                if ( dataPort[i][0] == port )
                {
                    dataPort[i][1] = 0;
                    break;
                }
            }

            dataPort.notify();
        }
    }
}

