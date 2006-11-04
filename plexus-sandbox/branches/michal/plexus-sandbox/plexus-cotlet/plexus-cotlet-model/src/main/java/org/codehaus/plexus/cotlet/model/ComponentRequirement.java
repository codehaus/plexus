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

public class ComponentRequirement
{
    private String role;

    private String roleHint;

    private String property;

    private String version;

    private String cardinality;


    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getCardinality()
    {
        return cardinality;
    }

    public void setCardinality( String cardinality )
    {
        this.cardinality = cardinality;
    }


    public String toString()
    {
        return "ComponentRequirement{" +
               "role='" + role + "'" +
               ", roleHint='" + roleHint + "'" +
               ", property='" + property + "'" +
               ", version='" + version + "'" +
               ", cardinality='" + cardinality + "'" +
               "}";
    }
}




