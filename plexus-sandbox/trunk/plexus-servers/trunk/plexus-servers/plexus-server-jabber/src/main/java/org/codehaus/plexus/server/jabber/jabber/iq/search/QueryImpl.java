/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.jabber.iq.search;


import java.util.List;

import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.ServerParameters;

import org.codehaus.plexus.server.jabber.data.Account;
import org.codehaus.plexus.server.jabber.data.jabber.IQPacket;
import org.codehaus.plexus.server.jabber.data.storage.AccountRepositoryHolder;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;

import org.apache.avalon.framework.service.ServiceManager;
import org.xmlpull.v1.XmlPullParser;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.search.Query" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.search.Query"
 */
public class QueryImpl
    extends DefaultSessionProcessor
    implements Query
{

    private ServerParameters m_serverParameters;
    private AccountRepositoryHolder m_accountHolder;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.ServerParameters:1.0"  key="ServerParameters"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.data.storage.AccountRepositoryHolder:1.0"  key="AccountRepositoryHolder"
     */
    public void service( ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        m_serverParameters = (ServerParameters) serviceManager.lookup( "ServerParameters" );
        m_accountHolder = (AccountRepositoryHolder) serviceManager.lookup( "AccountRepositoryHolder" );

    }

    //-------------------------------------------------------------------------
    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        IMClientSession clientSession = (IMClientSession) session;
        String type = ( (IQPacket) context ).getType();

        // GET
        if ( IQPacket.TYPE_GET.equals( type ) )
        {
            get( clientSession, context );
        }
        else if ( IQPacket.TYPE_SET.equals( type ) )
        {
            set( clientSession, context );
        }
    }


    //-------------------------------------------------------------------------
    private void get( final IMClientSession session,
                      Object context )
        throws Exception
    {


        String iqId = ( (IQPacket) context ).getId();

        String s = "<iq type='result'";
        s += " from='" + m_serverParameters.getHostName() + "'";
        s += " to='" + session.getUser().getJIDAndRessource() + "'";
        s += " id='" + iqId + "'";
        s += ">";
        s += "<query xmlns='jabber:iq:search'>";
        s += "<nick/>";
/*
 We will be able to search thru all these when we'll refactor user-manager lib
        s += "<first/>";
        s += "<last/>";
        s += "<email/>";
*/
        s += "<instructions>Fill in one or more fields to search for any matching Jabber users.</instructions>";
        s += "</query></iq>";

        session.writeOutputStream( s );
    }

    //-------------------------------------------------------------------------
    private void set( final IMClientSession session,
                      final Object context )
        throws Exception
    {

        final XmlPullParser xpp = session.getXmlPullParser();
        String iqId = ( (IQPacket) context ).getId();

        xpp.nextTag(); // <nick>
        String searchText = xpp.nextText();
        xpp.nextTag(); // </nick>

        getLogger().debug( "Search for account name " + searchText );
        // maybe we should proceed to a search via the vcard...
        List list = m_accountHolder.getAccountList( searchText );

        String s = "<iq type='result'";
        s += " from='" + m_serverParameters.getHostName() + "'";
        s += " to='" + session.getUser().getJIDAndRessource() + "'";
        s += " id='" + iqId + "'";
        s += ">";


        if ( list.size() > 0l )
        {
            s += "<query xmlns='jabber:iq:search'>";

            for ( int i = 0, l = list.size(); i < l; i++ )
            {
                Account account = (Account) list.get( i );
                s += "<item jid='" + account.getName() + '@' + m_serverParameters.getHostName() + "'>";
                s += "<first>" + account.getName() + "</first>";
                s += "<last>" + account.getName() + "</last>";
                s += "<nick>" + account.getName() + "</nick>";
                s += "<email>" + account.getName() + '@' + m_serverParameters.getHostName() + "</email>";
                s += "</item>";
            }
            s += "</query>";
        }
        else
        {
            s += "<query xmlns='jabber:iq:search'/>";
        }
        s += "</iq>";

        session.writeOutputStream( s );
    }

}

