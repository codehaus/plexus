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

public class ComponentConfigurationTag
{
    private String name;

    private String className;

    private String description;

    private List children = new ArrayList();

    private String defaultValue;

    private boolean collection;

    private String elementImlementation;

    public String getElementImlementation()
    {
        return elementImlementation;
    }

    public void setElementImlementation( String elementImlementation )
    {
        this.elementImlementation = elementImlementation;
    }


    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public List getChildren()
    {
        return children;
    }

    public void addChild( ComponentConfigurationTag child )
    {
        children.add( child );
    }

    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
    }


    public String getDefaultValue()
    {
        return defaultValue;
    }


    public boolean isLeaf()
    {
        boolean retValue = false;

        if ( LeafTypes.contains( className ) )
        {
            retValue = true;
        }
        else
        {
            retValue = children.isEmpty();
        }

        return retValue;

    }

    public void setCollection( boolean collection )
    {
        this.collection = collection;
    }

    public boolean isCollection()
    {
        return collection;
    }


    public void setElementType( ComponentConfigurationTag element )
    {
        children.add( element );

    }

}
