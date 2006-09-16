package org.codehaus.plexus.security.example.web.action;

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

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * MainAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="main"
 */
public class MainAction
    extends PlexusActionSupport
{
    private String dest;

    public String getDest()
    {
        return dest;
    }

    public void setDest( String dest )
    {
        this.dest = dest;
    }

    public String show()
    {
        if ( StringUtils.isNotEmpty( dest ) )
        {
            return dest;
        }

        return SUCCESS;
    }
}
