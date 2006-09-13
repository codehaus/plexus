package org.codehaus.plexus.security.ui.web.action;

import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.user.policy.PasswordRuleViolations;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.Iterator;
import java.util.List;

/**
 * LoginAction:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityNewUser"
 */
public class NewUserAction
    extends PlexusActionSupport
{

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private String username;

    private String password;

    private String passwordConfirm;

    private String email;

    private String fullName;

    public String createUser()
    {
        // TODO: use commons-validator for these fields.

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "User Name is required." );
        }

        if ( StringUtils.isEmpty( fullName ) )
        {
            addActionError( "Full Name is required." );
        }

        if ( StringUtils.isEmpty( email ) )
        {
            addActionError( "Email Address is required." );
        }

        // TODO: Validate Email Address (use commons-validator)

        if ( StringUtils.equals( password, passwordConfirm ) )
        {
            addActionError( "Passwords do not match." );
        }
        
        UserManager um = securitySystem.getUserManager();

        if ( um.userExists( username ) )
        {
            addActionError( "User already exists!" );
        }
        else
        {
            User user = um.createUser( username, fullName, email );

            user.setPassword( password );

            try
            {
                um.addUser( user );
            }
            catch ( PasswordRuleViolationException e )
            {
                PasswordRuleViolations violations = e.getViolations();
                List violationList = violations.getLocalizedViolations();
                Iterator it = violationList.iterator();
                while ( it.hasNext() )
                {
                    addActionError( (String) it.next() );
                }
            }
        }
        
        if ( hasActionErrors() )
        {
            return ERROR;
        }

        return SUCCESS;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public String getPasswordConfirm()
    {
        return passwordConfirm;
    }

    public void setPasswordConfirm( String passwordConfirm )
    {
        this.passwordConfirm = passwordConfirm;
    }
}
