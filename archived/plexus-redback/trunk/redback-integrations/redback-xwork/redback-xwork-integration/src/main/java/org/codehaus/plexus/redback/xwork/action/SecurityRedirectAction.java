package org.codehaus.plexus.redback.xwork.action;

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

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * SecurityRedirectAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-redirect"
 * instantiation-strategy="per-lookup"
 */
public class SecurityRedirectAction
    extends PlexusActionSupport
{
    private String externalResult;

    public String redirect()
    {
        if ( StringUtils.isNotEmpty( externalResult ) )
        {
            return externalResult;
        }

        return SUCCESS;
    }

    public String getExternalResult()
    {
        return externalResult;
    }

    public void setExternalResult( String name )
    {
        this.externalResult = name;
    }
}
