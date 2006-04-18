package org.codehaus.plexus.security.osuser;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.ResourceController;

import com.opensymphony.user.UserManager;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractResourceController
    extends AbstractLogEnabled
    implements ResourceController
{
    private UserManager userManager;

    public boolean isAuthorized( Object entity, Object resource )
    {
        if ( entity instanceof Group )
        {
            return isAuthorized( (Group) entity, resource );
        }
        else if ( entity instanceof User )
        {
            return isAuthorized( (User) entity, resource );
        }
        else
        {
            return false;
        }
    }

    public abstract boolean isAuthorized( Group group, Object resource );

    public abstract boolean isAuthorized( User user, Object resource );

    protected UserManager getUserManager()
    {
        return userManager;
    }
}