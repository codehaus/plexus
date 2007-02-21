package org.codehaus.plexus.redback.authorization.memory;

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

import org.codehaus.plexus.redback.rbac.AbstractRole;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * MemoryRole 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class MemoryRole
    extends AbstractRole
    implements Role, java.io.Serializable
{

    /**
     * Field name
     */
    private String name;

    /**
     * Field description
     */
    private String description;

    /**
     * Field assignable
     */
    private boolean assignable = false;

    /**
     * Field childRoleNames
     */
    private List childRoleNames = new ArrayList();

    /**
     * Field permissions
     */
    private List permissions = new ArrayList();
    
    /**
     * Field permanent
     */
    private boolean permanent = false;

    /**
     * Method addPermission
     * 
     * @param memoryPermission
     */
    public void addPermission( Permission memoryPermission )
    {
        if ( !( memoryPermission instanceof MemoryPermission ) )
        {
            throw new ClassCastException( "MemoryRole.addPermissions(memoryPermission) parameter must be instanceof "
                + MemoryPermission.class.getName() );
        }
        getPermissions().add( ( (MemoryPermission) memoryPermission ) );
    }

    /**
     * Method equals
     * 
     * @param other
     */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof MemoryRole ) )
        {
            return false;
        }

        MemoryRole that = (MemoryRole) other;
        boolean result = true;
        result = result && ( getName() == null ? that.getName() == null : getName().equals( that.getName() ) );
        return result;
    }

    /**
     * Method getChildRoles
     */
    public List getChildRoleNames()
    {
        return this.childRoleNames;
    }

    /**
     * Get null
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get null
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Method getPermissions
     */
    public List getPermissions()
    {
        return this.permissions;
    }

    /**
     * Method hashCode
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }

    /**
     * Get 
     *             true if this role is available to be assigned to
     * a user
     *           
     */
    public boolean isAssignable()
    {
        return this.assignable;
    }

    /**
     * Method removePermission
     * 
     * @param memoryPermission
     */
    public void removePermission( Permission memoryPermission )
    {
        if ( !( memoryPermission instanceof MemoryPermission ) )
        {
            throw new ClassCastException( "MemoryRole.removePermissions(memoryPermission) parameter must be instanceof "
                                              + MemoryPermission.class.getName() );
        }
        getPermissions().remove( ( (MemoryPermission) memoryPermission ) );
    }

    /**
     * Set 
     *             true if this role is available to be assigned to
     * a user
     *           
     * 
     * @param assignable
     */
    public void setAssignable( boolean assignable )
    {
        this.assignable = assignable;
    }

    /**
     * Set null
     * 
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Set null
     * 
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Set null
     * 
     * @param permissions
     */
    public void setPermissions( List permissions )
    {
        this.permissions = permissions;
    }
    
    /**
     * Method toString
     */
    public java.lang.String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "name = '" );
        buf.append( getName() + "'" );
        return buf.toString();
    }

    public void addChildRoleName( String name )
    {
        this.childRoleNames.add( name );
    }

    public void setChildRoleNames( List names )
    {
        if(names == null)
        {
            this.childRoleNames.clear();
        }
        else
        {
            this.childRoleNames = names;
        }
    }

    public boolean isPermanent()
    {
        return permanent;
    }

    public void setPermanent( boolean permanent )
    {
        this.permanent = permanent;
    }
}
