/*
 * $RCSfile$
 *
 * Copyright 2000 by Informatique-MTF, SA,
 * CH-1762 Givisiez/Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 */
package org.codehaus.plexus.cotlet.model;

import java.util.ArrayList;
import java.util.List;

public class ComponentImplementation
{
    String name;

    String version;

    String description;

    String roleHint;

    String role;

    List requirements = new ArrayList();

    List configurationTags = new ArrayList();

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getDescription()
    {
        return description;
    }

    public List getRequirements()
    {
        return requirements;
    }

    public void addRequirement( ComponentRequirement requirement )
    {
        requirements.add( requirement );
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }


    public String toString()
    {
        return "ComponentImplementation{" +
               "name='" + name + "'" +
               ", version='" + version + "'" +
               ", description='" + description + "'" +
               ", roleHint='" + roleHint + "'" +
               ", role='" + role + "'" +
               ", requirements=" + requirements +
               "}";
    }

    public List getConfigurationTags()
    {
        return configurationTags;
    }

    public void addConfigurationTag( ComponentConfigurationTag configurationTag )
    {
        configurationTags.add( configurationTag );

    }

    public boolean hasConfigurationTags()
    {
        return configurationTags.size() > 0;

    }


    public boolean hasRequirements()
    {
        return requirements.size() > 0;
    }

}
