// there is a bug, some clients have problems that errorcode and errormsg are not set
/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim.jabber.iq.auth;



import net.java.dev.openim.data.UsersManager;
import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.IMRouter;
import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.jabber.IQPacket;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;




/**
 * @avalon.component version="1.0" name="iq.auth.Query" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.auth.Query"
 *
 * @version 1.0
 * @author AlAg
 */
public class QueryImpl extends DefaultSessionProcessor implements Query {
    
    private ServerParameters    m_serverParameters;
    private UsersManager m_usersManager;
    private AccountRepositoryHolder         m_accountHolder;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
     * 
     * @avalon.dependency type="net.java.dev.openim.data.UsersManager:1.0" key="UsersManager"
     * @avalon.dependency type="net.java.dev.openim.data.storage.AccountRepositoryHolder:1.0"  key="AccountRepositoryHolder"
     *
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.auth.Username:1.0" key="iq.auth.Username"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.auth.Password:1.0" key="iq.auth.Password"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.auth.Digest:1.0" key="iq.auth.Digest"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.auth.Resource:1.0" key="iq.auth.Resource"
     */
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_usersManager = (UsersManager)serviceManager.lookup( "UsersManager" );
        m_accountHolder = (AccountRepositoryHolder)serviceManager.lookup( "AccountRepositoryHolder" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
        super.service( serviceManager );
    }

    //-------------------------------------------------------------------------
    public void process( final IMSession session, final Object context ) throws Exception{
        
        IMClientSession clientSession = (IMClientSession)session;
        
        String iqId = ((IQPacket)context).getId();
        String type = ((IQPacket)context).getType();

        
        User user = m_usersManager.getNewUser();
        clientSession.setUser( user );
        user.setHostname( m_serverParameters.getHostName() );

        
        // GET
        if( IQPacket.TYPE_GET.equals( type ) ){
            super.process( session, context );
            String s = null;
            
            Account account = m_accountHolder.getAccount( user.getName() );
            if( account == null ){ // user does not exists
                s = "<iq type='"+IQPacket.TYPE_ERROR+"' id='"+iqId+"'>"
                    +"<query xmlns='jabber:iq:auth'><username>"+user.getName()+"</username></query>"
                    + "<error code='401'>Unauthorized</error>"
                    + "</iq>";                
            }
            
            else{ // user exists
                s = "<iq type='"+IQPacket.TYPE_RESULT+"' id='"+iqId+"' from='"+ m_serverParameters.getHostName() +"'>"
                            +"<query xmlns='jabber:iq:auth'>"
                            +"<username>"+user.getName()+"</username>";

                if( user.isAuthenticationTypeSupported( Account.AUTH_TYPE_PLAIN ) ){
                    s += "<password/>";
                }
                if( user.isAuthenticationTypeSupported( Account.AUTH_TYPE_DIGEST ) ){
                    s += "<digest/>";
                }
                s += "<resource/></query></iq>";
            }
            
            session.writeOutputStream( s );            
        }
        
        // SET
        else if( IQPacket.TYPE_SET.equals( type ) ){
            super.process( session, context );

            
            try{
                user.authenticate( Long.toString( session.getId() ) );      
                IMRouter router = session.getRouter();
                router.registerSession( clientSession );

                String s = "<iq type='"+IQPacket.TYPE_RESULT+"' id='"+iqId+"' />";
                session.writeOutputStream( s );
                
                // get all enqued message
                //router.deliverQueueMessage( session, user.getName() );
                
            } 

            catch( Exception e ){
                getLogger().debug( e.getMessage(), e );
                String s = "<iq type='"+IQPacket.TYPE_ERROR+"' id='"+iqId+"' />";
                // TODO: add error code and msg
                session.writeOutputStream( s );
            }
        }
        
    }

    
}

