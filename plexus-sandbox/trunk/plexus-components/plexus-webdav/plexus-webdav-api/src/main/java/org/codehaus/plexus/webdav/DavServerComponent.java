package org.codehaus.plexus.webdav;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.webdav.servlet.DavServerRequest;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * DavServerComponent 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface DavServerComponent
{
    /** The Plexus ROLE name */
    public static final String ROLE = DavServerComponent.class.getName();

    /**
     * Get the Prefix for this server component.
     * @return the prefix associated with this component.
     */
    public String getPrefix();

    /**
     * Set the prefix for this server component.
     * @param prefix the prefix to use.
     */
    public void setPrefix( String prefix );

    /**
     * Get the root directory for this server.
     * 
     * @return the root directory for this server.
     */
    public File getRootDirectory();

    /**
     * Set the root directory for this server's content.
     * 
     * @param rootDirectory the root directory for this server's content.
     */
    public void setRootDirectory( File rootDirectory );

    /**
     * Add a Server Listener for this server component.
     * 
     * @param listener the listener to add for this component.
     */
    public void addListener( DavServerListener listener );
    
    /**
     * Remove a server listener for this server component.
     * 
     * @param listener the listener to remove.
     */
    public void removeListener( DavServerListener listener );

    /**
     * Perform any initialization needed.
     * 
     * @param servletConfig the servlet config that might be needed.
     * @throws DavServerException if there was a problem initializing the server component.
     */
    public void init( ServletConfig servletConfig ) throws DavServerException;

    /**
     * Performs a simple filesystem check for the specified resource.
     * 
     * @param resource the resource to check for.
     * @return true if the resource exists.
     */
    public boolean hasResource( String resource );

    /**
     * Process incoming request.
     * 
     * @param request the incoming request to process.
     * @param response the outgoing response to provide.
     */
    public void process( DavServerRequest request, HttpServletResponse response )
        throws DavServerException, ServletException, IOException;
}
