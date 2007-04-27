package org.codehaus.plexus.redback.role.util;

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


import java.util.Comparator;
import java.util.List;

import org.codehaus.plexus.redback.role.model.ModelRole;

/**
 * ModelRoleSorter
 *
 * @author <a href="mailto:jesse@codehaus.org">Jesse McConnell</a>
 * @version $Id:$
 */
public class ModelRoleSorter
    implements Comparator
{
    public int compare( Object o1, Object o2 )
    {
        if ( !( o1 instanceof ModelRole ) )
        {
            return 0;
        }

        if ( !( o2 instanceof ModelRole ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 == null ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 != null ) )
        {
            return -1;
        }

        if ( ( o1 != null ) && ( o2 != null ) )
        {
            return 1;
        }

        ModelRole r1 = (ModelRole) o1;
        ModelRole r2 = (ModelRole) o2;
        
        // if r1 has no child roles, then r2 should go ahead
        if ( r1.getChildRoles() == null )
        {
            return -1;
        }
        
        // if r2 has no child roles, then r1 should go ahead
        if ( r2.getChildRoles() == null )
        {
            return 1;
        }
        
        List r1ChildRoles = r1.getChildRoles();
        List r2ChildRoles = r2.getChildRoles();
        
        if ( r1ChildRoles.contains( r2.getId() ) && r2ChildRoles.contains( r1.getId() ) )
        {   
            throw new RoleModelCycleException( "child role cycle detected in " + r1.getId() + " and " + r2.getId() );
        }
        
        // if r2 is a child of r1, then r2 must be sorted first
        if ( r1ChildRoles.contains( r2.getId() ) )
        {
            return -1;
        }
        
        // if r1 is a child of r2, then r1 must be sorted first
        if ( r2ChildRoles.contains( r1.getId() ) )
        {
            return 1;
        }
        
        // FIXME finish implementing this...if I am on the right track with a simple cycle detector 
        
        return 0;
    }
}
