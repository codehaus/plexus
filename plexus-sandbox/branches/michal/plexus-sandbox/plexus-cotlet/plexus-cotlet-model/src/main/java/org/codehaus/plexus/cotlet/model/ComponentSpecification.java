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

public class ComponentSpecification
{
    private String description;

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String toString()
    {
        return "ComponentSpecification{" +
               "description='" + description + "'" +
               ", name='" + name + "'" +
               "}";
    }
}
