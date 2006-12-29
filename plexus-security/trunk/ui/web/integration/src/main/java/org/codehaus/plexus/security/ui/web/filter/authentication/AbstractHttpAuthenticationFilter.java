package org.codehaus.plexus.security.ui.web.filter.authentication;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.security.ui.web.filter.PlexusServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * AbstractHttpAuthenticationFilter
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractHttpAuthenticationFilter
    extends PlexusServletFilter
{
    private String realmName;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        realmName = filterConfig.getInitParameter( "realm-name" );
    }

    public String getRealmName()
    {
        return realmName;
    }

    public void setRealmName( String realmName )
    {
        this.realmName = realmName;
    }
}
