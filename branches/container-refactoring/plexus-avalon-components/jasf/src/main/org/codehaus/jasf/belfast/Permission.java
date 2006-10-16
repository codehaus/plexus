package org.codehaus.jasf.belfast;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class Permission
{
    private long id;
    private String permission;

    /**
     * @return
     */
    public String getPermission()
    {
        return permission;
    }

    /**
     * @param string
     */
    public void setPermission(String string)
    {
        permission = string;
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
