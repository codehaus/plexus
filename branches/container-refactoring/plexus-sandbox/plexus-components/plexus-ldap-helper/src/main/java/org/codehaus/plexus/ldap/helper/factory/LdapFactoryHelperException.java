package org.codehaus.plexus.ldap.helper.factory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LdapFactoryHelperException
    extends Exception
{
    public LdapFactoryHelperException( String message )
    {
        super( message );
    }

    public LdapFactoryHelperException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
