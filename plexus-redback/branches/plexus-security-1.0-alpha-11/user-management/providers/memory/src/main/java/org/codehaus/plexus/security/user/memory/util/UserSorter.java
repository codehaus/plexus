package org.codehaus.plexus.security.user.memory.util;

import org.codehaus.plexus.security.user.User;

import java.util.Comparator;

/**
 * UserSorter
 */
public class UserSorter
    implements Comparator
{
    private boolean ascending;

    public UserSorter()
    {
        this.ascending = true;
    }

    public UserSorter( boolean ascending )
    {
        this.ascending = ascending;
    }

    public int compare( Object o1, Object o2 )
    {
        if ( !( o1 instanceof User ) )
        {
            return 0;
        }

        if ( !( o2 instanceof User ) )
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

        User u1 = null;
        User u2 = null;

        if ( isAscending() )
        {
            u1 = (User) o1;
            u2 = (User) o2;
        }
        else
        {
            u1 = (User) o2;
            u2 = (User) o1;
        }

        return u1.getUsername().compareTo( u2.getUsername() );
    }

    public boolean isAscending()
    {
        return ascending;
    }

    public void setAscending( boolean ascending )
    {
        this.ascending = ascending;
    }

}
