package org.codehaus.jasf.impl.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Role is a collection of credentials that a user has.  A user can
 * participate in multiple roles.
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 19, 2003
 */
public class Role
{
    List credentials;
    
    String name;
    
    public Role()
    {
        credentials = new ArrayList();
    }

    public List getCredentials()
    {
        return credentials;
    }
    
    public void addCredential( String c )
    {
        credentials.add(c);
    }
    /**
     * Method hasCredential.
     * @param c
     * @return boolean
     */
    public boolean hasCredential(String credential)
    {
        Iterator itr = credentials.iterator();
        while (itr.hasNext())
        {
            String roleCred = (String) itr.next();
            if (roleCred.equals(credential))
                return true;
        }

        return false;
    }
        
    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
