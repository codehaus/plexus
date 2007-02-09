package org.codehaus.plexus.dns;

import java.util.Collection;

/** @author Jason van Zyl */
public interface DnsServer
{
    String ROLE = DnsServer.class.getName();

    Collection findMXRecords( String hostname );    
}
