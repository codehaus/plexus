package org.codehaus.plexus.xfire.config;

public class Echo
    implements EchoIntf
{
    public String echo( String echo )
    {
        return echo;
    }
}
