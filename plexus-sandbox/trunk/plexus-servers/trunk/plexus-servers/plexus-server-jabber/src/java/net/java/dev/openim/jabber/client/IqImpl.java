// very simple delivery tree added (it looks for destination hostname and decides who will process iq)
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
package net.java.dev.openim.jabber.client;

import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.ModuleManager;
import net.java.dev.openim.SessionProcessor;
import net.java.dev.openim.tools.JIDParser;
import net.java.dev.openim.module.ServerModule;
import net.java.dev.openim.data.jabber.IQPacket;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

import java.io.StringWriter;

/**
 * @author AlAg
 * @author PV
 * @version 1.0
 * @avalon.component version="1.0" name="client.Iq" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.client.Iq"
 */
public class IqImpl extends DefaultSessionProcessor implements Iq {

    private ServerParameters m_serverParameters;
    private ModuleManager m_moduleManager;

    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.auth.Query:1.0" key="iq.auth.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.browse.Query:1.0" key="iq.browse.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.search.Query:1.0" key="iq.search.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.oob.Query:1.0" key="iq.oob.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.register.Query:1.0" key="iq.register.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.roster.Query:1.0" key="iq.roster.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.jprivate.Query:1.0" key="iq.jprivate.Query"
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.vcardtemp.VCard:1.0" key="iq.vcardtemp.VCard"
     * @avalon.dependency type="net.java.dev.openim.ModuleManager:1.0" key="ModuleManager"
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
     */
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        super.service(serviceManager);

        m_serverParameters = (ServerParameters) serviceManager.lookup("ServerParameters");
        m_moduleManager = (ModuleManager) serviceManager.lookup("ModuleManager");

    }

    public void process(final IMSession session, final Object context) throws Exception {

        ServerModule module = null;

        XmlPullParser xpp = session.getXmlPullParser();

        for (int i = 0, l = xpp.getAttributeCount(); i < l; i++) {
            getLogger().debug("Attribut ns: " + xpp.getAttributeNamespace(i) + " name: " + xpp.getAttributeName(i) + " value: " + xpp.getAttributeValue(i));
        }

        // create a new iq packet
        IQPacket iq = new IQPacket(xpp);
        getLogger().debug("Got IQ " + iq);

        // set the correct from field
        IMClientSession clientSession = null;
        if (session instanceof IMClientSession) {
            clientSession = (IMClientSession) session;
        }

        if (clientSession != null && clientSession.getUser() != null) {
            iq.setFrom(clientSession.getUser().toString());
        }

        // Is the received packet for the server?
        if (iq.getTo() == "" || m_serverParameters.getHostNameList().contains(iq.getTo())) {

            super.process(session, iq);

            // append not processed elemenents
            if (this.m_notProcessedData != null ) {
                iq.addSerializedChild(this.m_notProcessedData.toString());
            }

            // route a result
            if (IQPacket.TYPE_RESULT.equals(iq.getType())) {
                iq.setFrom(((IMClientSession) session).getUser().getJIDAndRessource());
                session.getRouter().route(session, iq);
            }

        } else if ((module = m_moduleManager.getModuleByHostname(JIDParser.getHostname(iq.getTo()))) != null) {
            // the received packet is for some server module
            SessionProcessor processor = module.getProcessor(getEventName(session, xpp.getNamespace(), xpp.getName()));
            if (processor != null) processor.process(session, iq);

        } else {
            // the received packet is not for the server or its modules
            // the packet will be roundtriped and routed
            StringWriter sw = new StringWriter();
            // serialize packet content
            while (xpp.next() != XmlPullParser.END_TAG) {
                session.roundTripNode(sw);
            }
            iq.addSerializedChild(sw.toString());

            if (clientSession != null && clientSession.getUser() != null) {
                iq.setFrom(clientSession.getUser().toString());
            }
            session.getRouter().route(session, iq);
        }
    }

}


