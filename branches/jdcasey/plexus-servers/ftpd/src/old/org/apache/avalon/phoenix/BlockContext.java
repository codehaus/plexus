/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface BlockContext
    extends Context
{
    String APP_NAME = "app.name";
    String APP_HOME_DIR = "app.home";
    String NAME = "block.name";

    /**
     * Base directory of .sar application.
     *
     * TODO: Should this be getHomeDirectory() or getWorkingDirectory() or other?
     * TODO: Should a Block be able to declare it doesn't use the Filesystem? If
     * it declares this then it would be an error to call this method.
     *
     * @return the base directory
     */
    File getBaseDirectory();

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    String getName();

    /**
     * A block can request that the application it resides in be
     * shut down. This method will schedule the blocks application
     * for shutdown. Note that this is just a request and the kernel
     * may or may not honour the request (by default the request will
     * be honored).
     */
    void requestShutdown();

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     * Names may be prefixed with '/' character.
     *
     * @param name the name of resource
     * @return the InputStream for resource or null if no such resource
     */
    InputStream getResourceAsStream( String name );

    /**
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     * @deprecated This allows block writers to "break-out" of their logging
     *             hierarchy which is considered bad form. Replace by
     *             Logger.getChildLogger(String) where original logger is aquired
     *             via AbstractLogEnabled.
     */
    Logger getLogger( String name );

    /**
     * This method gives the block access to a named {@link ClassLoader}.
     * The {@link ClassLoader}s for an application are declared in the
     * <tt>environment.xml</tt> descriptor. See the Specification for details.
     *
     * @param name the name of the classloader
     * @return the classloader
     * @throws Exception if no such {@link ClassLoader}
     */
    ClassLoader getClassLoader( String name )
        throws Exception;

    /**
     * Retrieve the proxy for this object.
     * Each Block is referenced by other Blocks via their Proxy. When Phoenix
     * shuts down the Block, it can automatically invalidate the proxy. Thus
     * any attempt to call a method on a "dead"/shutdown object will result in
     * an {@link IllegalStateException}. This is desirable as it will
     * stop objects from using the Block when it is in an invalid state.
     *
     * <p>The proxy also allows Phoenix to associate "Context" information with
     * the object. For instance, a Block may expect to run with a
     * specific ContextClassLoader set. However if this Block were to be passed
     * to another component that processed the Block in a thread that did not
     * have the correct context information setup, then the Block could fail
     * to perform as expected. By passing the proxy instead, the correct context
     * information is maintained by Phoenix.</p>
     *
     * <p>Note that only interfaces that the Block declares as offered services
     * will actually be implemented by the proxy.</p>
     */
    //Object getProxy();

    /**
     * This method is similar to {@link #getProxy()} except that it operates
     * on arbitrary objects. It will in effect proxy all interfaces that the
     * component supports.
     *
     * <p>Proxying arbitrary objects is useful for the same reason it is useful
     * to proxy the Block. Thus it is recomended that when needed you pass
     * Proxys of objects to minimize the chance of incorrect behaviour.</p>
     */
    //Object getProxy( Object other );

    /**
     * This method generates a Proxy of the specified object using the
     * specified interfaces. In other respects it is identical to
     * getProxy( Object other )
     */
    //Object getProxy( Object other, Class[] interfaces );

    /**
     * Retrieve the MBeanServer for this application.
     *
     * NOTE: Unsure if this will ever be implemented
     * may be retrievable via CM instead, or perhaps in
     * a directory or whatever.
     */
    //MBeanServer getMBeanServer();
}
