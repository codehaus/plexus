package org.codehaus.plexus.service.jetty;

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

import java.io.File;
import java.net.UnknownHostException;

import org.codehaus.plexus.DefaultPlexusContainer;

import org.mortbay.http.HttpContext;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ServletContainer
{
    String ROLE = ServletContainer.class.getName();

    boolean hasContext( String contextPath );

    void addListener( String address, int port )
        throws ServletContainerException, UnknownHostException;

    // TODO: addVirtualHost();

    void deployWarFile( File war, boolean extractWar, File extractionLocation, DefaultPlexusContainer container, String context,
                        String virtualHost, int port )
        throws ServletContainerException;

    void deployWarDirectory( File war, String context, String virtualHost, int port, DefaultPlexusContainer container )
        throws ServletContainerException;

    void startApplication( String contextPath )
        throws ServletContainerException;
}
