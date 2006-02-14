package org.codehaus.plexus.ldap.helper;
/*
 * The MIT License
 * Copyright (c) 2005, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import javax.naming.ldap.LdapName;
import javax.naming.InvalidNameException;
import java.util.Properties;

/**
 * This class contains the configuration for a ldap connection.
 * <p/>
 * Properties of a ldap connection:
 * <ul>
 * <li>Hostname - String, required.
 * <li>Port - int, not required. If 0 then the default value is used by the ldap driver.
 * <li>Base DN - String, required.
 * <li>Context factory - String, required.
 * <li>Bind DN - String, not required.
 * <li>Password - String, not required.
 * </ul>
 * Note that both the bind dn and password must be set if any are set.
 *
 * @author <a href="mailto:trygvis@inamo.no">trygvis</a>
 * @version $Id: LdapConnectionConfiguration.java,v 1.3 2006/02/06 15:15:54 trygvis Exp $
 */
public class LdapConnectionConfiguration
{
    private String hostname;

    private int port;

    private LdapName baseDN;

    private String contextFactory;

    private LdapName bindDN;

    private String password;

    private Properties extraProperties;

    public LdapConnectionConfiguration( String hostname, int port, LdapName baseDN, String contextFactory,
                                        LdapName bindDN, String password, Properties extraProperties )
    {
        this.hostname = hostname;
        this.port = port;
        this.baseDN = new LdapName( baseDN.getRdns() );
        this.contextFactory = contextFactory;
        this.bindDN = new LdapName( bindDN.getRdns() );
        this.password = password;
        this.extraProperties = extraProperties;
    }

    public LdapConnectionConfiguration( String hostname, int port, String baseDN, String contextFactory,
                                        String bindDN, String password, Properties extraProperties )
        throws InvalidNameException
    {
        this( hostname, port, new LdapName( baseDN ), contextFactory, new LdapName( bindDN ), password, extraProperties );
    }

    public LdapConnectionConfiguration( String hostname, int port, LdapName baseDN, String contextFactory )
    {
        this( hostname, port, baseDN, contextFactory, null, null, new Properties( ) );
    }

    public LdapName getBaseDN()
    {
        return baseDN;
    }

    public String getContextFactory()
    {
        return contextFactory;
    }

    public String getHostname()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }

    public LdapName getBindDN()
    {
        return bindDN;
    }

    public String getPassword()
    {
        return password;
    }

    public Properties getExtraProperties()
    {
        if ( extraProperties == null )
        {
            extraProperties = new Properties();
        }

        return extraProperties;
    }

    /**
     * This method will check to se if the configuration is 'sane'.
     *
     * @throws LdapException
     */
    public void check()
        throws LdapException
    {
        if ( hostname == null )
        {
            hostname = "localhost";
        }
        if ( port < 0 || port > 65535 )
        {
            throw new LdapException( "The port must be between  and 65535." );
        }
        if ( baseDN == null )
        {
            throw new LdapException( "The base DN must be set." );
        }
        if ( contextFactory == null )
        {
            throw new LdapException( "The context factory must be set." );
        }
        if ( password != null && bindDN == null )
        {
            throw new LdapException( "The password cant be set unless the bind dn is." );
        }
    }
}
