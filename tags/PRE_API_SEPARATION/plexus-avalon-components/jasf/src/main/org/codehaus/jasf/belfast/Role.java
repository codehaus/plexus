package org.codehaus.jasf.belfast;

import java.util.List;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class Role
{
    private long id;
    private String name;
    private List permissions;
    
    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public List getPermissions()
    {
        return permissions;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @param list
     */
    public void setPermissions(List list)
    {
        permissions = list;
    }
    /**
     * @return
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param l
     */
    public void setId(long l)
    {
        id = l;
    }

}
