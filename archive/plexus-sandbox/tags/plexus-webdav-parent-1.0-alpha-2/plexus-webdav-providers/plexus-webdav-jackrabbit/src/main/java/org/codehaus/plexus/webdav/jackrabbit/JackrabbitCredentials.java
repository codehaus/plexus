package org.codehaus.plexus.webdav.jackrabbit;

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

import org.apache.jackrabbit.server.CredentialsProvider;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * JackrabbitCredentials 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JackrabbitCredentials
    implements CredentialsProvider
{
    public static Credentials getDefaultCredentials( HttpServletRequest request )
    {
        return new SimpleCredentials( "anonymous", "".toCharArray() );
    }

    public Credentials getCredentials( HttpServletRequest request )
        throws LoginException, ServletException
    {
        return getCredentials( request );
    }
}