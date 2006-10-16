package org.codehaus.plexus.spe.action;

import org.codehaus.plexus.action.AbstractAction;
import org.codehaus.plexus.spe.User;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SaveUserAction
    extends AbstractAction
{
    private User user;

    private String username;

    public static User staticUser;

    public static String staticUsername;

    public void execute( Map map )
        throws Exception
    {
        staticUser = user;

        staticUsername = username;
    }
}
