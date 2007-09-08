package org.codehaus.plexus.redback.taglib.jsp;

/*
 * Copyright 2006 The Codehaus.
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

import com.opensymphony.xwork.ActionContext;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.redback.authorization.AuthorizationException;
import org.codehaus.plexus.redback.configuration.UserConfiguration;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * IfConfiguredTag:
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id$
 */
public class IfConfiguredTag
    extends ConditionalTagSupport
{
    private String option;

    private String value;
    
    public void setOption( String option )
    {
        this.option = option;
    }
   
    public void setValue( String value )
    {
        this.value = value;
    }

    protected boolean condition()
        throws JspTagException
    { 
        
        ActionContext context = ActionContext.getContext();

        PlexusContainer container = (PlexusContainer) context.getApplication().get( PlexusLifecycleListener.KEY );

        try
        {
            UserConfiguration config = (UserConfiguration) container.lookup( UserConfiguration.ROLE );            
            
            if ( value != null )
            {
                String configValue = config.getString( option );
                
                if ( value.equals( configValue ) )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return config.getBoolean( option );
            }
        }
        catch ( ComponentLookupException cle )
        {
            throw new JspTagException( "unable to locate security system", cle );
        }
    }
}
