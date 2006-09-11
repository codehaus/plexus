package org.codehaus.plexus.security.ui.web.action;

import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.Preparable;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;
/*
 * Copyright 2005 The Apache Software Foundation.
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

/**
 * LoginAction:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityUserManagement"
 */
public class UserManagementAction
    extends PlexusActionSupport
    implements ModelDriven, Preparable
{

    /**
     * @plexus.requirement
     */
    private UserManager manager;

    private User user;

    private String username;

    public void prepare()
        throws Exception
    {
        if ( username == null )
        {
            username = ((User)session.get( SecuritySession.USERKEY )).getUsername();
            user = manager.findUser( username );
        }
        else
        {
            user = manager.findUser( username );
        }
    }

    public String save()
        throws Exception
    {
        User temp = manager.findUser( username );

        temp.setEmail( user.getEmail() );
        temp.setFullName( user.getFullName() );
        temp.setLocked( user.isLocked() );

        manager.updateUser( temp );

        return SUCCESS;
    }

    public Object getModel()
    {
        return user;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }
}
