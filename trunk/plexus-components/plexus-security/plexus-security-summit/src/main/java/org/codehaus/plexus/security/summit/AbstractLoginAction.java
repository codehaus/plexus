package org.codehaus.plexus.security.summit;

import org.codehaus.plexus.action.AbstractAction;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractLoginAction
    extends AbstractAction
{
    public void execute(Map context)
        throws Exception
    {
        getLogger().debug( "User is logging in." );

        // If a User is logging in, we should refresh the
        // session here. Invalidating session and starting a
        // new session would seem to be a good method, but I
        // (JDM) could not get this to work well (it always
        // required the user to login twice). Maybe related
        // to JServ? If we do not clear out the session, it
        // is possible a new User may accidently (if they
        // login incorrectly) continue on with information
        // associated with the previous User. Currently the
        // only keys stored in the session are "turbine.user"
        // and "turbine.acl".
        SecureRunData data = (SecureRunData) context.get("data");
        
        Enumeration names = data.getSession().getAttributeNames();
        if (names != null)
        {
            // copy keys into a new list, so we can clear the session
            // and not get ConcurrentModificationException
            List nameList = new ArrayList();
            while (names.hasMoreElements())
            {
                nameList.add(names.nextElement());
            }

            HttpSession session = data.getSession();
            Iterator nameIter = nameList.iterator();
            while (nameIter.hasNext())
            {
                try
                {
                    session.removeAttribute((String) nameIter.next());
                }
                catch (IllegalStateException invalidatedSession)
                {
                    break;
                }
            }
        }

        // Remove the action parameter so the action is executed again later
        for (Iterator itr = data.getParameters().keys(); itr.hasNext();)
        {
            String key = (String) itr.next();
            if (key.equals("action"))
            {
                synchronized (itr)
                {
                    itr.remove();
                }
                return;
            }
        }

        login( context );
    }

    protected abstract void login( Map context )
        throws Exception;
}
