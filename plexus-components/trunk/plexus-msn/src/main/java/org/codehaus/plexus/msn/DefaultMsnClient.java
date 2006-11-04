package org.codehaus.plexus.msn;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.io.IOException;

import rath.msnm.MSNMessenger;
import rath.msnm.SwitchboardSession;
import rath.msnm.UserStatus;
import rath.msnm.entity.MsnFriend;
import rath.msnm.event.MsnAdapter;
import rath.msnm.msg.MimeMessage;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: IrcBot.java 1462 2005-02-09 15:47:38Z jvanzyl $
 */
public class DefaultMsnClient
    extends AbstractLogEnabled
    implements MsnClient
{
    private String login;

    private String password;

    private MSNMessenger msn;

    private String initialStatus = UserStatus.ONLINE;

    public void login()
        throws MsnException
    {
        msn = new MSNMessenger( login, password );

        msn.setInitialStatus( initialStatus );

        LoginAdapter adapter = new LoginAdapter( msn, getLogger() );

        msn.addMsnListener( adapter );

        msn.login();

        getLogger().info( "Connection. Waiting for the response...." );

        while ( adapter.getStatus() == LoginAdapter.NOT_INITIALIZED )
        {
            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e )
            {
            }
        }

        if ( adapter.getStatus() == LoginAdapter.LOGIN_ERROR )
        {
            throw new MsnException( "Login failed : " + adapter.getError() );
        }
    }

    public void logout()
        throws MsnException
    {
        if ( msn != null )
        {
            msn.logout();
        }
    }

    public void sendMessage( String recipient, String message )
        throws MsnException
    {
        SwitchboardSession ss = null;

        try
        {
            ss = msn.doCallWait( recipient );
        }
        catch ( InterruptedException e )
        {
        }
        catch ( IOException e )
        {
        }

        try
        {
            while ( ss == null )
            {
                ss = msn.doCallWait( recipient );
            }
        }
        catch ( InterruptedException e )
        {
            System.out.println( "session docallwait InterruptedException:" );
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            System.out.println( "session docallwait:" );
            e.printStackTrace();
        }

        if ( ss == null )
        {
            throw new MsnException( "Can't create a SwitchboardSession." );
        }

        MimeMessage msg = new MimeMessage();

        msg.setKind( MimeMessage.KIND_MESSAGE );

        msg.setMessage( message );

        try
        {
            ss.sendInstantMessage( msg );
        }
        catch ( IOException e )
        {
            throw new MsnException( "The message isn't send.", e );
        }
    }

    public void setLogin( String login )
    {
        this.login = login;
    }

    public String getLogin()
    {
        return login;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }
}

class LoginAdapter
    extends MsnAdapter
{
    public static final int NOT_INITIALIZED = -1;

    public static final int LOGIN_OK = 0;

    public static final int LOGIN_ERROR = 1;

    private int status = NOT_INITIALIZED;

    private String error;

    private MSNMessenger messenger;

    private Logger logger;

    public LoginAdapter( MSNMessenger messenger, Logger logger )
    {
        this.messenger = messenger;

        this.logger = logger;
    }

    public void loginComplete( MsnFriend own )
    {
        if ( own.getLoginName().equals( messenger.getLoginName() ) )
        {
            status = LOGIN_OK;

            logger.info( "Connected." );
        }
    }

    public void loginError( String header )
    {
        logger.error( header );

        status = LOGIN_ERROR;

        error = header;
    }

    public int getStatus()
    {
        return status;
    }

    public String getError()
    {
        return error;
    }
}
