package org.codehaus.plexus.xfire.soap;

/**
 * @plexus.soap.name EchoService
 * @plexus.soap.namespace echo
 * @plexus.soap.style document
 * @plexus.soap.use literal
 * @plexus.soap.scope application
 * 
 * @plexus.component role="org.codehaus.plexus.xfire.soap.Echo" role-hint="default"
 */
public class DefaultEcho
    implements Echo
{
    public String echo( String echo )
    {
        return echo;
    }
}
