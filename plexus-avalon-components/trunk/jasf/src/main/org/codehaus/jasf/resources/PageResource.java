package org.codehaus.jasf.resources;

import java.util.ArrayList;
import java.util.List;

/**
 * The PageResource and associated ResourceControllers are a simple way to
 * restrict access to certain web pages.  They in no way provide a way to handle
 * actions that a user might perform while using the webpage, such as updating
 * the database or changing some setting.
 * 
 * @author Dan Diephouse
 * @since Nov 21, 2002
 */
public class PageResource
{
    public final static String RESOURCE_TYPE = 
        PageResource.class.getName();
    
    String name;
    
    List resources;
    
    String credential;
    
    public PageResource( String name )
    {
        this.name = name;
        resources = new ArrayList();
    }
    
    public PageResource()
    {
        resources = new ArrayList();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName( String name )
    {
        this.name = name;
    }
    /**
     * Returns the resources.
     * @return List
     */
    public List getResources()
    {
        return resources;
    }

    /**
     * Sets the children resources if this is a directory.
     * 
     * @param resources The resources to set
     */
    public void setResources(List resources)
    {
        this.resources = resources;
    }

    /**
     * Adds a resource to the list of children if this is a directory.
     * 
     * @param resource the child file.
     */
    public void addResource( PageResource resource )
    {
        resources.add(resource);
    }

    /**
     * Returns true if this resource has child resources, ie: it is a directory.
     * 
     * @return boolean
     */
    public boolean hasChildren()
    {
        if (resources.size() > 0)
            return true;
            
        return false;
    }
    
    public boolean equals( Object resource )
    {
        if (((PageResource) resource).getName().equals(getName()))
            return true;
        
        return false;
    }
}
