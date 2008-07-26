package org.codehaus.plexus.jabber;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class DefaultJabberClient
    extends AbstractLogEnabled
    implements JabberClient
{
    private static final String RESOURCE_NAME = "plexus-jabber";

    private String host;

    private int port = -1;

    private String user;

    private String password;

    private String imDomainName;

    private boolean isSslConnection;

    private XMPPConnection conn;

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#connect()
     */
    public void connect()
        throws JabberClientException
    {
        try
        {
            if ( !isSslConnection )
            {
                conn = new XMPPConnection( getHost(), getPort(), getImDomainName() );
            }
            else
            {
                conn = new SSLXMPPConnection( getHost(), getPort(), getImDomainName() );
            }
        }
        catch ( XMPPException e )
        {
            throw new JabberClientException( "Can't connect to " + getHost() + ":" + getPort(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#disconnect()
     */
    public void disconnect()
        throws JabberClientException
    {
        if ( conn != null )
        {
            if ( conn.isConnected() )
            {
                conn.close();
            }
            conn = null;
        }
    }

    private XMPPConnection getConnection()
        throws JabberClientException
    {
        if ( conn == null || !conn.isConnected() )
        {
            connect();
        }

        return conn;
    }

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#logon()
     */
    public void logon()
        throws JabberClientException
    {
        XMPPConnection conn = getConnection();

        if ( user != null )
        {
            try
            {
                conn.login( user, password, RESOURCE_NAME );

                if ( !conn.isAuthenticated() )
                {
                    throw new JabberClientException( "Authentication failed." );
                }
            }
            catch ( XMPPException e )
            {
                if ( e.getXMPPError() != null && e.getXMPPError().getCode() == 401 )
                {
                    getLogger().info( "User " + user + " doesn't exist. Trying to create it." );

                    try
                    {
                        conn.getAccountManager().createAccount( user, password );

                        conn.login( user, password, RESOURCE_NAME );
                    }
                    catch ( XMPPException createException )
                    {
                        throw new JabberClientException( "Can't create an account for user " + user + " on "
                                                         + getHost(), createException );
                    }
                }
                else
                {
                    throw new JabberClientException( "Can't connect to " + getHost() + " with user " + user, e );
                }
            }
        }
        else
        {
            try
            {
                conn.loginAnonymously();
            }
            catch ( XMPPException e )
            {
                throw new JabberClientException( "Can't open an anonymous session on " + getHost(), e );
            }
        }
    }

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#logoff()
     */
    public void logoff()
        throws JabberClientException
    {
        disconnect();
    }

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#sendMessageToUser(java.lang.String, java.lang.String)
     */
    public void sendMessageToUser( String recipientUser, String message )
        throws JabberClientException
    {
        XMPPConnection conn = getConnection();

        try 
        {
            conn.createChat( recipientUser ).sendMessage( message );
        }
        catch ( XMPPException e )
        {
            throw new JabberClientException( "Can't send a message to " + recipientUser + " user", e );
        }

        // TODO: replace with test from smack API to see if message was delivered
        // little sleep to be sure that message was sent
        try
        {
            Thread.sleep( 1000 );
        }
        catch( InterruptedException e )
        {
        }
    }

    /**
     * @see org.codehaus.plexus.jabber.JabberClient#sendMessageToGroup(java.lang.String, java.lang.String)
     */
    public void sendMessageToGroup( String recipientGroup, String message )
        throws JabberClientException
    {
        XMPPConnection conn = getConnection();

        try
        {
            conn.createGroupChat( recipientGroup ).sendMessage( message );
        }
        catch ( XMPPException e )
        {
            throw new JabberClientException( "Can't send a message to " + recipientGroup + " group", e );
        }

        // TODO: replace with test from smack API to see if message was delivered
        // little sleep to be sure that message was sent
        try
        {
            Thread.sleep( 1000 );
        }
        catch( InterruptedException e )
        {
        }
    }

    /**
     * @return Returns the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host The host to set.
     */
    public void setHost( String host )
    {
        this.host = host;
    }

    /**
     * @return Returns the port
     */
    public int getPort()
    {
        if ( port <= 0 )
        {
            return port;
        }
        else if ( isSslConnection )
        {
            return 5223;
        }
        else
        {
            return 5222;
        }
    }

    /**
     * @param port The port to set.
     */
    public void setPort( int port )
    {
        this.port = port;
    }

    /**
     * @return Returns the isSslConnection.
     */
    public boolean isSslConnection()
    {
        return isSslConnection;
    }

    /**
     * @param isSslConnection The isSslConnection to set.
     */
    public void setSslConnection( boolean isSslConnection )
    {
        this.isSslConnection = isSslConnection;
    }

    /**
     * @return Returns the IM domain name.
     */
    public String getImDomainName()
    {
        if ( imDomainName == null )
        {
            return getHost();
        }
        else
        {
            return imDomainName;
        }
    }

    /**
     * @param host The IM domain name to set.
     */
    public void setImDomainName( String imDomainName )
    {
        this.imDomainName = imDomainName;
    }

    /**
     * @return Returns the user.
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user The user to set.
     */
    public void setUser( String user )
    {
        this.user = user;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword( String password )
    {
        this.password = password;
    }
}
