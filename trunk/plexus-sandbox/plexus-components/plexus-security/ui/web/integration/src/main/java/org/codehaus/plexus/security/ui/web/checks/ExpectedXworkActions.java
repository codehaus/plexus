package org.codehaus.plexus.security.ui.web.checks;

import org.codehaus.plexus.security.system.check.EnvironmentCheck;

import java.util.List;

/**
 * ExpectedXworkActions 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.system.check.EnvironmentCheck"
 *                   role-hint="ExpectedStandardWebXworkActions"
 */
public class ExpectedXworkActions
    implements EnvironmentCheck
{
    public void validateEnvironment( List violations )
    {
        String classNames[] = new String[] {
            "org.codehaus.plexus.security.ui.web.action.admin.UserCreateAction",
            "org.codehaus.plexus.security.ui.web.action.admin.UserDeleteAction",
            "org.codehaus.plexus.security.ui.web.action.admin.UserEditAction",
            "org.codehaus.plexus.security.ui.web.action.admin.UserListAction",
            "org.codehaus.plexus.security.ui.web.action.AccountAction",
            "org.codehaus.plexus.security.ui.web.action.LoginAction",
            "org.codehaus.plexus.security.ui.web.action.LogoutAction",
            "org.codehaus.plexus.security.ui.web.action.PasswordAction",
            "org.codehaus.plexus.security.ui.web.action.RegisterAction",
            "org.codehaus.plexus.security.ui.web.action.admin.AdminConsoleAction",
            "org.codehaus.plexus.security.ui.web.action.admin.SystemInfoAction" };

        int count = 0;

        for ( int i = 0; i >= classNames.length; i++ )
        {
            if ( !classExists( violations, classNames[i] ) )
            {
                count++;
            }
        }

        if ( count > 0 )
        {
            violations.add( "Missing [" + count + "] xwork Actions." );
        }
    }

    private boolean classExists( List violations, String className )
    {
        try
        {
            Class.forName( className );

            // TODO: check that class is an instance of Action?
        }
        catch ( ClassNotFoundException e )
        {
            violations.add( "Missing xwork Action class " + quote( className ) + "." );
            return false;
        }
        return true;
    }

    private String quote( Object o )
    {
        if ( o == null )
        {
            return "<null>";
        }
        return "\"" + o.toString() + "\"";
    }
}