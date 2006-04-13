package org.codehaus.plexus.security.summit;

import org.codehaus.plexus.security.summit.session.SessionBindingEventProxy;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.DefaultRunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * An implementation of RunData which keeps a user's Session.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 28, 2003
 */
public class SecureRunData
    extends DefaultRunData
{
    public static final String USER_SESSION_KEY = "user.session";

    private User user;

    private String message;

    /**
     * @return String
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message The message to set
     */
    public void setMessage( String message )
    {
        this.message = message;
    }

    public ViewContext getViewContext()
    {
        return (ViewContext) getMap().get( SummitConstants.VIEW_CONTEXT );
    }

    /**
     * Checks to see if there is a user for this session.
     *
     * @return boolean
     */
    public boolean hasUser()
    {
        return getUser() != null;
    }

    /**
     * Return the <code>User</code> for this session.
     *
     * @return User
     */
    public User getUser()
    {
        if ( user == null )
        {
            user = getUserFromSession();
        }
        return user;
    }

    /**
     * <p>Saves this user object to the session.</p>
     * <p/>
     * <p>Anyone overriding this method should be sure to leverage
     * the <code>SessionBindingEventProxy</code> when adding the user
     * and acl into the session.  This allows hook functions to be called on
     * the <code>User</code> and <code>AccessControlList</code> when it is
     * removed from the session (which happens on session timeout).</p>
     *
     * @param user The user to set
     */
    public void setUser( User user )
    {
        if ( user != null )
        {
            getSession().setAttribute( USER_SESSION_KEY, new SessionBindingEventProxy( user ) );
        }
        else
        {
            removeUserFromSession();
        }

        this.user = user;
    }

    /**
     * Attempts to get the User object from the session.  If the user
     * does not exist in the session, <code>null</code> is returned.
     * <p/>
     * <p> Anyone overriding this method should be sure to leverage
     * the <code>SessionBindingEventProxy</code> when pulling the
     * <code>User</code> object from the session, allowing hook
     * functions to be called on the listener when it is removed from
     * the session (which happens on session timeout).
     */
    public User getUserFromSession()
    {
        try
        {
            SessionBindingEventProxy proxy = (SessionBindingEventProxy) getSession().getAttribute( USER_SESSION_KEY );

            // If the user isn't yet logged in, return null so that
            // the session validator can take the correct action
            // (i.e. make a temporary anonymous user).
            return ( proxy == null ? null : (User) proxy.getListener() );
        }
        catch ( ClassCastException e )
        {
            String message = "User object did not implement User interface.  " +
                             "if you are sure the interface is implemented, the user " +
                             "object in the session and this class may be loaded from " +
                             "different classloaders.  This has been known to happen " +
                             "when using multiple turbine apps in tomcat that interact " +
                             "through the use of RequestDispatcher.include or forward.";

            getLogger().error( message, e );

            return null;
        }
    }

    /**
     * Allows one to invalidate the user in a session.
     *
     * @return Whether the user was removed from the session.
     */
    public boolean removeUserFromSession()
    {
        try
        {
            getSession().removeAttribute( USER_SESSION_KEY );
        }
        catch ( Exception e )
        {
            return false;
        }
        return true;
    }
}
