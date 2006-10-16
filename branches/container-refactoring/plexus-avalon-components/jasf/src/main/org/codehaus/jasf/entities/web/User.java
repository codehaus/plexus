package org.codehaus.jasf.entities.web;

import org.codehaus.jasf.summit.session.SessionBindingListener;

/**
 * <p>User is a basic user interface for web applications.  It is implemented by
 * the various web security implmentations - ie: db and xml.
 * </p>
 * <p>NOTE: This class extends <code>SessionBindingListener</code> because it is
 * used for web based applications only.  This is open to change.</p>
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 11, 2003
 */
public interface User 
    extends SessionBindingListener
{
    public static String ENTITY_TYPE = User.class.getName();
    
    /**
     * Get the unique user name.
     * 
     * @return String
     */
    public String getUserName();
    
    /**
     * Returns true if the user is currently logged in.
     * 
     * @return boolean
     */
    public boolean isLoggedIn();
    
    /**
     * Sets whether or not the user is logged in.
     * 
     * @param value
     */
    public void setLoggedIn( boolean value );
    
    public void updateLastAccessDate();
    
    public void incrementAccessCounter();
    
    public void incrementAccessCounterForSession();
    
    public void setTemp( String key, Object value );
    
    public Object getTemp( String key );

    public void setPerm( String key, Object value );
    
    public Object getPerm( String key );

}
