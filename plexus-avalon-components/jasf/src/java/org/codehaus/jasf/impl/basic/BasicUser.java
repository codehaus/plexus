package org.codehaus.jasf.impl.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jasf.entities.web.User;
import org.codehaus.jasf.summit.session.SessionBindingEvent;

/**
 * @author Dan Diephouse
 * @since Nov 20, 2002
 * 
 * A simple Entity implementation of a User.  There is a simple
 * one to many association with the user's credentials.
 */
public class BasicUser implements User, BasicEntity
{
    String userName;
    
    String password;
    
    List roles;
    
    boolean loggedIn = false;
    
    Hashtable temp;
    
    Date lastAccessDate;
    
    int accessCounter = 0;
    
    int sessionAccessCounter = 0;
    
    public BasicUser()
    {
        roles = new ArrayList();
        temp = new Hashtable();
    }
    
    /**
     * Return the username, which is a unique identifier for each
     * user.
     * 
     * @return String
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Sets the username.
     * @param username The username to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    /**
     * Returns the password.
     * @return String
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     * @param password The password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public List getRoles()
    {
        return roles;
    }
    
    public void addRole( Role r )
    {
        roles.add(r);
    }
    
    public void removeRole( Role r )
    {
        roles.remove(r);   
    }
    
    /**
     * Check to see if the user has the credential in any of their roles.
     * 
     * @param c
     */
    public boolean hasCredential( String c )
    {
        Iterator itr = roles.iterator();
        while (itr.hasNext())
        {
            Role r = (Role) itr.next();
            if (r.hasCredential(c))
                return true;
        }
    
        // The Credential was not found.
        return false;
    }
        
    /**
     * @return boolean
     */
    public boolean hasLoggedIn()
    {
        return loggedIn;
    }
    
    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#setLoggedIn(boolean)
     */
    public void setLoggedIn( boolean loggedIn )
    {
        this.loggedIn = loggedIn;
    }

    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#isLoggedIn()
     */
    public boolean isLoggedIn()
    {
        return false;
    }
    
    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#setTemp(String, Object)
     */
    public void setTemp(String key, Object value)
    {
        temp.put(key, value);
    }


    /**
     * Gets the last access date for this User.  This is the last time
     * that the user object was referenced.
     *
     * @return A Java Date with the last access date for the user.
     */
    public java.util.Date getLastAccessDate()
    {
        if (lastAccessDate == null)
        {
            updateLastAccessDate();
        }
        return lastAccessDate;
    }

    /**
     * Sets the last access date for this User. This is the last time
     * that the user object was referenced.
     */
    public void updateLastAccessDate()
    {
        lastAccessDate = new java.util.Date();
    }
       
    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#getTemp(String)
     */
    public Object getTemp(String key)
    {
        return temp.get(key);
    }
    
    /**
     * This is just a call to setTemp, because there is no permanent storage
     * mechanism yet.
     * 
     * @see org.apache.fulcrum.jasf.entities.web.User#setPerm(java.lang.String,
     * java.lang.Object)
     */
    public void setPerm(String key, Object value)
    {
        setTemp(key, value);
    }
    
    /**
     * This is just a call to getTemp, because there is no permanent storage
     * mechanism yet.
     * 
     * @see org.apache.fulcrum.jasf.entities.web.User#getPerm(java.lang.String)
     */
    public Object getPerm(String key)
    {
        return getTemp(key);
    }
    
    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#incrementAccessCounter()
     */
    public void incrementAccessCounter()
    {
        accessCounter++;
    }
    
    /**
     * @see org.apache.fulcrum.jasf.entities.web.User#incrementAccessCounterForSession()
     */
    public void incrementAccessCounterForSession()
    {
        sessionAccessCounter++;
    }
    
    /**
     * Currently does nothing.
     * 
     * @see org.apache.fulcrum.security.session.SessionBindingListener#valueBound(org.apache.fulcrum.security.session.SessionBindingEvent)
     */
    public void valueBound(SessionBindingEvent event)
    {
        // Do nothing
    }
    
    /**
     * Currently does nothing.
     * 
     * @see org.apache.fulcrum.security.session.SessionBindingListener#valueUnbound(org.apache.fulcrum.security.session.SessionBindingEvent)
     */
    public void valueUnbound(SessionBindingEvent event)
    {
        // Do nothing
    }
}
