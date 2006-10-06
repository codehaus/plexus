package org.codehaus.plexus.security.ui.web.util;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CookieUtils 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CookieUtils
{
    public static final int SESSION_COOKIE = (-1);
    
    public static Cookie setCookie( HttpServletResponse response, String domain, String name, String value, String path, int maxAge )
    {
        Cookie cookie = new Cookie( name, value );
        if ( maxAge > 0 )
        {
            cookie.setMaxAge( maxAge );
        }
        cookie.setDomain( domain );
        cookie.setPath( path );
        response.addCookie( cookie );

        return cookie;
    }

    public static Cookie getCookie( HttpServletRequest request, String name )
    {
        Cookie cookies[] = request.getCookies();

        if ( cookies == null || StringUtils.isEmpty( name ) )
        {
            return null;
        }

        for ( int i = 0; i < cookies.length; i++ )
        {
            if ( StringUtils.equals( name, cookies[i].getName() ) )
            {
                return cookies[i];
            }
        }

        return null;
    }
}
