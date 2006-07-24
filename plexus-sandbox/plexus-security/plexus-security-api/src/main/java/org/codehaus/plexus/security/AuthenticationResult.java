package org.codehaus.plexus.security;

import java.io.Serializable;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: AuthenticationResult.java 2990 2006-01-09 16:33:52Z evenisse $
 */
public class AuthenticationResult
    implements Serializable
{
    private boolean isAuthenticated = false;

    private String principal;

    private String message;

    private Exception exception;

    public void setAuthenticated( boolean isAuthenticated )
    {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException( Exception exception )
    {
        this.exception = exception;
    }
}
