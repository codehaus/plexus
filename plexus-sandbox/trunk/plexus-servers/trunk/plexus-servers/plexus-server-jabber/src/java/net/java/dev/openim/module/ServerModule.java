// I'm not sure how this interface should look like.
// every module should has its identity and features as defined in discovery protocol
// module can be hooked to the processing schema having its own hostname (ie. conference.openim.net, pubsub.openim.net etc.)
// if server receives a packet for this module, then the eventname of packet is passed to getProcessor() method
// and calls process on returned processor
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
package net.java.dev.openim.module;

import net.java.dev.openim.SessionProcessor;

import java.util.List;


/**
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public interface ServerModule 
{
    public ServerModuleIdentity getId();
    public ServerModuleFeatures getFeatures();
    public List getHostNameList();
    public SessionProcessor getProcessor(String eventName);
}


