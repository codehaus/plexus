package org.codehaus.plexus.server.ftp.usermanager;

import java.util.Collection;

/**
 * @author Jason van Zyl
 * @plexus.component 
 */
public class MemoryUserManager
    implements UserManager
{
    public User getUserByName( String name )
    {
        return new User();
    }

    public boolean doesExist( String name )
    {
        return true;
    }

    public boolean authenticate( String login,
                                 String password )
    {
        return true;
    }

    public String getAdminName()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void save( User user )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete( String user )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection getAllUserNames()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reload()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
