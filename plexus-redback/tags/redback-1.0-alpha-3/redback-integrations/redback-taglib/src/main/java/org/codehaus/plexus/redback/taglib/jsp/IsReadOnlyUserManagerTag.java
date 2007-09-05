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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;

import com.opensymphony.xwork.ActionContext;

/**
 * IsReadOnlyUserManagerTag:
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id$
 */
public class IsReadOnlyUserManagerTag
    extends ConditionalTagSupport
{
    protected boolean condition()
        throws JspTagException
    { 
        
        ActionContext context = ActionContext.getContext();

        PlexusContainer container = (PlexusContainer) context.getApplication().get( PlexusLifecycleListener.KEY );

        try
        {
            UserManager config = (UserManager) container.lookup( UserManager.ROLE, "configurable" );            
            
            return config.isReadOnly();
        }
        catch ( ComponentLookupException cle )
        {
            throw new JspTagException( "unable to locate security system", cle );
        }
    }
}
