package org.codehaus.plexus.security.ui.web.filter.authentication;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpAuthentication 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface HttpAuthentication
{
    /**
     * The Plexus ROLE name.
     */
    public static final String ROLE = HttpAuthentication.class.getName();
    
    /**
     * Check the Http Authentication headers and then authenticate the user.
     * 
     * @param request
     * @param response
     */
    public void authenticate(HttpServletRequest request, HttpServletResponse response )
        throws HttpAuthenticationException;
    
    /**
     * Issue Authentication Challenge Response to the client.
     * 
     * @param request the request to use.
     * @param response the response to use.
     * @param realmName the realm name to return to the client.
     * @parem exception the exception to base the message off of.
     */
    public void challenge( HttpServletRequest request, HttpServletResponse response, String realmName, HttpAuthenticationException exception )
        throws IOException;

}
