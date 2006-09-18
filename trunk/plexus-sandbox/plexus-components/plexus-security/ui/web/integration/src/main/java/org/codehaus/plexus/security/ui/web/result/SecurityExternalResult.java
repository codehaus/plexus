package org.codehaus.plexus.security.ui.web.result;

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

import com.opensymphony.webwork.dispatcher.ServletActionRedirectResult;
import com.opensymphony.xwork.ActionInvocation;

/**
 * SecurityExternalResult 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork"
 *                   role-hint="securityExternalResult"
 *                   instantiation-strategy="per-lookup"
 */
public class SecurityExternalResult
    extends ServletActionRedirectResult
{
    /**
     * @plexus.configuration default-value="pssRedirect"
     */
    private String externalActionName;
    
    private String externalResult;
    
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        setNamespace( "/" );
        setActionName( externalActionName );
        super.execute( invocation );
    }

    public String getExternalResult()
    {
        return externalResult;
    }

    public void setExternalResult( String externalResult )
    {
        this.externalResult = externalResult;
    }
    
}
