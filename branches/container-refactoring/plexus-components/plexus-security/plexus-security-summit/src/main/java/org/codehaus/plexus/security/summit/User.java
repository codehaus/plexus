package org.codehaus.plexus.security.summit;

import org.codehaus.plexus.security.summit.session.SessionBindingListener;

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
    String ENTITY_TYPE = User.class.getName();

    /**
     * Get the unique user name.
     *
     * @return String
     */
    String getUserName();

    /**
     * Returns true if the user is currently logged in.
     *
     * @return boolean
     */
    boolean isLoggedIn();

    /**
     * Sets whether or not the user is logged in.
     *
     * @param value
     */
    void setLoggedIn( boolean value );

    void updateLastAccessDate();

    void incrementAccessCounter();

    void incrementAccessCounterForSession();

    void setTemp( String key, Object value );

    Object getTemp( String key );

    void setPerm( String key, Object value );

    Object getPerm( String key );
}
