/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.cornerstone.services.connection;

import java.net.ServerSocket;
import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * This is the service through which ConnectionManagement occurs.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface ConnectionManager
{
    String ROLE = ConnectionManager.class.getName();

    /**
     * Start managing a connection.
     * Management involves accepting connections and farming them out to threads
     * from pool to be handled.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to
     * @param handlerFactory the factory from which to aquire handlers
     * @param threadPool the thread pool to use
     * @exception Exception if an error occurs
     */
    void connect( String name,
                  ServerSocket socket,
                  ConnectionHandlerFactory handlerFactory,
                  ThreadPool threadPool )
        throws Exception;

    /**
     * Start managing a connection.
     * This is similar to other connect method except that it uses default thread pool.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to
     * @param handlerFactory the factory from which to aquire handlers
     * @exception Exception if an error occurs
     */
    void connect( String name,
                  ServerSocket socket,
                  ConnectionHandlerFactory handlerFactory )
        throws Exception;

    /**
     * This shuts down all handlers and socket, waiting for each to gracefully shutdown.
     *
     * @param name the name of connection
     * @exception Exception if an error occurs
     */
    void disconnect( String name )
        throws Exception;

    /**
     * This shuts down all handlers and socket.
     * If tearDown is true then it will forcefully shutdown all connections and try
     * to return as soon as possible. Otherwise it will behave the same as
     * void disconnect( String name );
     *
     * @param name the name of connection
     * @param tearDown if true will forcefully tear down all handlers
     * @exception Exception if an error occurs
     */
    void disconnect( String name, boolean tearDown )
        throws Exception;
}
