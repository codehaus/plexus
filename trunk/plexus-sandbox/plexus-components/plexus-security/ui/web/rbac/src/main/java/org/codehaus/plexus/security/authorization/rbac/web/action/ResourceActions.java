package org.codehaus.plexus.security.authorization.rbac.web.action;

import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.Preparable;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;
/*
 * Copyright 2005 The Codehaus.
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
 * OperationActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityResource"
 */
public class ResourceActions
    extends PlexusActionSupport
    implements ModelDriven, Preparable
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String resourceIdentifier;

    private Resource resource;

    public void prepare()
        throws Exception
    {
        if ( resource == null )
        {
            if ( manager.resourceExists( resourceIdentifier ) )
            {
                resource = manager.getResource( resourceIdentifier );
                resourceIdentifier = resource.getIdentifier();
            }
            else
            {
                resource = manager.createResource( "identifier" );
            }
        }
    }


    public String save()
        throws RbacActionException
    {
        // todo figure out if there is anyway to actually have this model driven action work with jdo objects
        Resource temp = manager.createResource( resourceIdentifier );

        temp.setIdentifier( resource.getIdentifier() );
        temp.setPattern( resource.isPattern() );

        manager.saveResource( temp );

        return SUCCESS;
    }

    public String remove()
        throws RbacActionException
    {
        try
        {
            manager.removeResource( manager.getResource( resourceIdentifier ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate resource to remove " + resourceIdentifier, ne );
        }
        return SUCCESS;
    }

    public Object getModel()
    {
        return resource;
    }

    public String getResourceIdentifier()
    {
        return resourceIdentifier;
    }

    public void setResourceIdentifier( String resourceIdentifier )
    {
        this.resourceIdentifier = resourceIdentifier;
    }

    public void setResource( Resource resource )
    {
        this.resource = resource;
    }
}
