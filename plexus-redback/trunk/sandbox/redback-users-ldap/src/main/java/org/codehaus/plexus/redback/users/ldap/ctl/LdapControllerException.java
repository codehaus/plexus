package org.codehaus.plexus.redback.users.ldap.ctl;

public class LdapControllerException
    extends Exception
{

    public LdapControllerException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public LdapControllerException( String message )
    {
        super( message );
    }

}
