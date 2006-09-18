package org.codehaus.plexus.security.authorization.rbac.web.action.admin;

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

import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * RolesAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-roles"
 *                   instantiation-strategy="per-lookup"
 */
public class RolesAction
    extends PlexusActionSupport
{
    private static final String LIST = "list";
    
    /**
     * @plexus.requirement
     */
    private RBACManager manager;
    
    private List allRoles;
    
    public String list()
    {
        allRoles = manager.getAllRoles();
        
        if(allRoles == null)
        {
            allRoles = new ArrayList();
        }
        
        return LIST;
    }
}
