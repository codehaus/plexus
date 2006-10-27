package org.codehaus.plexus.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.jetty.configuration.HttpListener;
import org.codehaus.plexus.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.jetty.configuration.ServletContext;
import org.codehaus.plexus.jetty.configuration.WebContext;
import org.codehaus.plexus.jetty.configuration.Webapp;

import java.io.File;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public interface ServletContainer
{
    String ROLE = ServletContainer.class.getName();

    boolean hasContext( String contextPath );

    void addListener( HttpListener listener )
        throws ServletContainerException, UnknownHostException;

    void removeListener( HttpListener listener )
    throws ServletContainerException;
        
    void addProxyListener( ProxyHttpListener listener )
        throws ServletContainerException, UnknownHostException;

    void startApplication( String contextPath )
        throws ServletContainerException;

    void stopApplication( String contextPath )
        throws ServletContainerException;

    // ----------------------------------------------------------------------------
    // For simple document serving
    // ----------------------------------------------------------------------------

    void deployWarDirectory( File directory, DefaultPlexusContainer container, Webapp webapp )
        throws ServletContainerException;

    public void deployServletContext( ServletContext servletContext )
        throws ServletContainerException;

    void deployContext( WebContext webContext )
        throws ServletContainerException;

    void clearContexts();
    
    boolean isPortRegistered( HttpListener listener );
}
