package org.codehaus.plexus.smtp;

import org.codehaus.plexus.synapse.SynapseServer;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface SmtpServer
    extends SynapseServer
{
    public static String ROLE = SmtpServer.class.getName();
}
